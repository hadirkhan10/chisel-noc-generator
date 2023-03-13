package noc
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object MeshNode {
  object State extends ChiselEnum {
    val idle, sendingHeader, sendingPayload, sendingEnd, receivingPayload, receivingEnd = Value
  }
}

class RoutingTableLookup(p: MeshNetworkParams) extends Bundle {
  val sourceNodeID = Output(UInt(log2Ceil(p.numOfNodes).W))
  val destNodeID = Output(Vec(2, UInt(p.bitsForNodeID.W)))
  val route = Input(Vec(p.maxRoutes, UInt()))
}

class Phit(p: MeshNetworkParams) extends Bundle {
  // a word that is transmitted each cycle contains the following data
  // -----------------------------------------
  // |  Route |        Payload               |
  // -----------------------------------------

  //val word = MixedVec(Seq(UInt(p.bitsForPhitType.W), UInt(p.bitsForRouting.W), UInt(p.phits.W)))
  val word = MixedVec(Seq(UInt(p.bitsForRouting.W), UInt(p.phits.W)))

}

class MeshNode(val xCord: Int, val yCord: Int, p: MeshNetworkParams) extends Module {
  import MeshNode.State
  import MeshNode.State._
  import MeshNetwork.PhitType._
  import MeshNetwork.Routes._

  val io = IO(new Bundle {
    // input from other adjacent nodes
    val in = Vec(4, Flipped(Decoupled(new Phit(p))))
    // input from the network to send a packet to a dest node
    val requestPacket = Flipped(Decoupled(new RequestPacket(p)))
    // output to other adjacent nodes
    val out = Vec(4, Decoupled(new Phit(p)))
    // the data stored in buffer for debugging/testing
    val data = Output(UInt(p.phits.W))
    // current state of the node for debugging/testing
    val state = Output(State())
    // output to lookup the routing table
    val routeLookup = new RoutingTableLookup(p)
  })

  // Decoupled
  // ready -> input
  // valid -> output
  // bits -> output

  // Flipped Decoupled
  // ready -> output
  // valid -> input
  // bits -> input

  private val xCordReg = RegInit(xCord.U)
  private val yCordReg = RegInit(yCord.U)

  val state = RegInit(idle)
  val isReady = RegInit(true.B)
  val nextHop = RegInit(0.U)
  val nextRoute = RegInit(0.U)
  val payload = RegInit(0.U)
  // used to store the payload in the destination node
  val buffer = RegInit(0.U)



  def id: Vec[UInt] = VecInit(xCordReg, yCordReg)

  io.in.foreach {dio => {
    dio.ready := isReady
  }}

  io.out.foreach(dio => {
    dio.valid := false.B
    dio.bits.word.foreach(o => o := 0.U)
  })

  switch (state) {
    is (idle) {
      when(io.requestPacket.fire) {
        // we got a request from the testbench to start sending a packet
        // do the routing table lookup
        val route = io.routeLookup.route
        nextHop := route.head
        nextRoute := VecInit(route.tail).asUInt
        payload := io.requestPacket.bits.payload
        isReady := false.B
        // if the request has the dest node the same as the source node then skip to receivingEnd state
        state := Mux(route.head === X.asUInt, receivingEnd, sendingHeader)
      } .elsewhen(io.in.map(dio => dio.valid).reduce(_ || _)) {
        // we are getting a packet from an adjacent node
        // figure out which adjacent node is sending a valid data
        // assuming getting data from a single node at a given time
        val index = io.in.indexWhere(dio => dio.valid === true.B)
        // getting the routes list as UInt so converting to a Vec[Bool] in order to slice the first digit route from head
        nextHop := VecInit(io.in(index).bits.word(0).asBools.slice(0,p.bitsForRouteType)).asUInt
        // getting the tail of the routes list to pass to the next hop node
        nextRoute := VecInit(io.in(index).bits.word(0).asBools.slice(p.bitsForRouteType,p.bitsForRouting)).asUInt
        // get the packet being received from the adjacent node
        state := receivingPayload
      }
    }
    is (sendingHeader) {
      val header = formHeaderPhit(nextRoute, p)
      io.out(nextHop).valid := true.B
      io.out(nextHop).bits := header
      state := sendingPayload
    }
    is (sendingPayload) {
      val payloadPhit = formPayloadPhit(payload, p)
      io.out(nextHop).valid := true.B
      io.out(nextHop).bits := payloadPhit
      state := sendingEnd
    }
    is (sendingEnd) {
      val endingPhit = formEndingPhit(p)
      io.out(nextHop).valid := true.B
      io.out(nextHop).bits := endingPhit
      state := idle
      isReady := true.B
    }
    is (receivingPayload) {
      val index = io.in.indexWhere(dio => dio.valid === true.B)
      //payload := io.in(index).bits.word(2)
      payload := io.in(index).bits.word(1)

      state := receivingEnd
    }
    is (receivingEnd) {
      val index = io.in.indexWhere(dio => dio.valid === true.B)
      // check if nextHop is X which mean this node is the destination
      // if not destination then send a new request to the adjacent node
      state := Mux(nextHop === X.asUInt, idle, sendingHeader)
      isReady := true.B
      buffer := payload

    }
  }
  def formHeaderPhit(nextRoute: UInt, p: MeshNetworkParams): Phit = {
    val header = Wire(new Phit(p))
    header.word(0) := nextRoute
    header.word(1) := 0.U
    header
  }

  def formPayloadPhit(data: UInt, p: MeshNetworkParams): Phit = {
    val payload = Wire(new Phit(p))
    payload.word(0) := 0.U
    payload.word(1) := data
    payload
  }

  def formEndingPhit(p: MeshNetworkParams): Phit = {
    val end = Wire(new Phit(p))
    //end.word(0) := EN.asUInt
    end.word(0) := 0.U
    end.word(1) := 0.U
    end
  }

  def getSourceNodeID(xCord: Int, yCord: Int): UInt = {
    if (yCord == 0) {
      xCord.U
    } else if (yCord == p.nRows-1) {
      (p.nCols * (p.nRows-1) + xCord).U
    } else {
      ((p.nCols-1) + yCord + xCord).U
    }
  }

  io.routeLookup.sourceNodeID := getSourceNodeID(xCord, yCord)

  // always connect this with the request packet dest node address
  // since we only read the value during packet fire
  // which would have these bits set appropriately
  io.routeLookup.destNodeID(0) := io.requestPacket.bits.destNodeID(0)
  io.routeLookup.destNodeID(1) := io.requestPacket.bits.destNodeID(1)

  io.requestPacket.ready := isReady
  io.state := state
  io.data := buffer


}
