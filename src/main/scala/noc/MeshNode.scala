package noc
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object MeshNode {
  object State extends ChiselEnum {
    val idle, sendingHeader, sendingPayload, receivingPayload = Value
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
    val data = Output(Vec(p.payloadPhits, UInt(p.phits.W)))
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
  val payload = RegInit(VecInit(Seq.fill(p.payloadPhits)(0.U(p.phits.W))))


  val (payloadIndex, isWrapping) = Counter(0 until p.payloadPhits, enable =
                                                                          (state === idle && io.requestPacket.fire) ||
                                                                          (state === receivingPayload) ||
                                                                          (state === sendingPayload))

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
        payload(payloadIndex) := io.requestPacket.bits.payload
        isReady := Mux((route.head === X.asUInt) && isWrapping, true.B, Mux(isWrapping, false.B, true.B))
        // if the request has the dest node the same as the source node then stay at idle state
        // otherwise go to sending header state when a complete packet is received from the testbench
        state := Mux(route.head === X.asUInt && isWrapping, idle, Mux(isWrapping, sendingHeader, idle))
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
      val payloadPhit = formPayloadPhit(payload(payloadIndex), p)
      io.out(nextHop).valid := true.B
      io.out(nextHop).bits := payloadPhit
      // instead of going to the sendingEnd state, go to the idle state if payload is sent
      state := Mux(isWrapping, idle, sendingPayload)
      isReady := Mux(isWrapping, true.B, isReady)
    }
    is (receivingPayload) {
      val index = io.in.indexWhere(dio => dio.valid === true.B)
      payload(payloadIndex) := io.in(index).bits.word(1)
      // instead of going to the ending state directly go to idle state if received all the payload phits
      state := Mux(isWrapping, Mux(nextHop === X.asUInt, idle, sendingHeader), receivingPayload)
      isReady := Mux(isWrapping, Mux(nextHop === X.asUInt, true.B, isReady), isReady)
    }
  }
  def formHeaderPhit(nextRoute: UInt, p: MeshNetworkParams): Phit = {
    val header = Wire(new Phit(p))
    header.word(0) := nextRoute
    // for the unused word send the number of payload phits
    header.word(1) := p.payloadPhits.U
    header
  }

  def formPayloadPhit(data: UInt, p: MeshNetworkParams): Phit = {
    val payload = Wire(new Phit(p))
    payload.word(0) := 0.U
    payload.word(1) := data
    payload
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
  io.data := payload


}
