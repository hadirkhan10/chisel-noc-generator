package noc
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.stage.ChiselStage
import chisel3.util._
import noc.MeshNetwork.PhitType

object MeshNetwork {
  object Route extends Enumeration {
    type Route = Value
    // N points to the north direction node
    // S points to the south direction node
    // E points to the east direction node
    // W points to the west direction node
    // X points to the current node being the destination node
    val N, S, E, W, X = Value
  }

  object Routes extends ChiselEnum {
    val N, S, E, W, X = Value
  }

  object PhitType extends ChiselEnum {
    val H, P, EN = Value
  }
}


case class MeshNetworkParams(nRows: Int, nCols: Int, phits: Int, bufferSize: Int) {
  require(nRows > 1)
  require(nCols > 1)
  val numOfNodes = nRows * nCols
  val maxRoutes = nCols - 1 + nRows
  val bitsForNodeID: Int = log2Ceil(List(nRows, nCols).max)
  val bitsForPhitType: Int = log2Ceil(PhitType.all.length)
  val bitsForRouteType: Int = log2Ceil(5) // always 5 because for mesh the route will always be N,S,E,W,X
  val bitsForRouting: Int = bitsForRouteType*maxRoutes
}

class SendRequestPacket(p: MeshNetworkParams) extends Bundle {
  // one UInt for each coordinate
  val destNodeID = Vec(2, UInt(p.bitsForNodeID.W))
  val payload = UInt(p.phits.W)
}
class MeshNetwork(p: MeshNetworkParams) extends Module {
  import MeshNetwork.Routes._
  import MeshNetwork.Route
  import MeshNode.State
  val io = IO(new Bundle {
    // the request packet from the testbench containing payload and addr of destination node
    val requestPacket = Vec(p.numOfNodes, Flipped(Decoupled(new SendRequestPacket(p))))
    // just for debugging
    val state = Vec(p.numOfNodes, Output(State()))
  })


  // create the mesh of node modules
  val mesh = Seq.tabulate(p.nRows, p.nCols) { case(i,j) => Module(new MeshNode(xCord = j, yCord = i, p))}
  val nodes = mesh.flatten

  val routingTable = VecInit.tabulate(p.numOfNodes, p.nRows, p.nCols) { (n, x, y) => {
    val sourceNodeIndex = n
    val sourceNode = nodes(sourceNodeIndex)
    val destNode = (y, x)

    VecInit(getRoute((sourceNode.xCord, sourceNode.yCord), destNode))
  }}

  nodes.zip(io.requestPacket).zip(io.state).foreach { case((node, p), s) => {
    node.io.requestPacket <> p
    s := node.io.state
    val destNodeID = getDestNodeID((node.io.routeLookup.destNodeID(0), node.io.routeLookup.destNodeID(1)))
    node.io.routeLookup.route := routingTable(node.io.routeLookup.sourceNodeID)(destNodeID._1)(destNodeID._2)
  }}

  for (i <- 0 until nodes.length) {
    val xCord = nodes(i).xCord
    val yCord = nodes(i).yCord
    if (xCord == 0) {
      // this node is in left column
      // connection to right adjacent node
      nodes(i).io.out(Route.E.id) <> nodes(i+1).io.in(Route.W.id)
      nodes(i+1).io.out(Route.W.id) <> nodes(i).io.in(Route.E.id)
      // no connection to the left adjacent node
      nodes(i).io.out(Route.W.id).ready := false.B
      nodes(i).io.in(Route.W.id).bits.word.foreach {o => o := 0.U}
      nodes(i).io.in(Route.W.id).valid := false.B
    }
    if (xCord == p.nCols-1) {
      // this node is in right column
      // connection to left adjacent node
      nodes(i).io.out(Route.W.id) <> nodes(i-1).io.in(Route.E.id)
      nodes(i-1).io.out(Route.E.id) <> nodes(i).io.in(Route.W.id)
      // no connection to the right adjacent node
      nodes(i).io.out(Route.E.id).ready := false.B
      nodes(i).io.in(Route.E.id).bits.word.foreach {o => o := 0.U}
      nodes(i).io.in(Route.E.id).valid := false.B
    }
    if (xCord != 0 && xCord != p.nCols-1) {
      // middle nodes
      // connection to left adjacent node
      nodes(i).io.out(Route.W.id) <> nodes(i-1).io.in(Route.E.id)
      nodes(i-1).io.out(Route.E.id) <> nodes(i).io.in(Route.W.id)
      // connection to right adjacent node
      nodes(i).io.out(Route.E.id) <> nodes(i+1).io.in(Route.W.id)
      nodes(i+1).io.out(Route.W.id) <> nodes(i).io.in(Route.E.id)
    }
    if (yCord == 0) {
      // this node is in the top row
      // connection to the bottom adjacent node
      nodes(i).io.out(Route.S.id) <> nodes(i+p.nCols).io.in(Route.N.id)
      nodes(i+p.nCols).io.out(Route.N.id) <> nodes(i).io.in(Route.S.id)
      // no connection to the top adjacent node
      nodes(i).io.out(Route.N.id).ready := false.B
      nodes(i).io.in(Route.N.id).bits.word.foreach {o => o := 0.U}
      nodes(i).io.in(Route.N.id).valid := false.B
    }
    if (yCord == p.nRows-1) {
      // this node is in the bottom row
      // connection to the top adjacent node
      nodes(i).io.out(Route.N.id) <> nodes(i - p.nCols).io.in(Route.S.id)
      nodes(i - p.nCols).io.out(Route.S.id) <> nodes(i).io.in(Route.N.id)
      // no connection to the bottom adjacent node
      nodes(i).io.out(Route.S.id).ready := false.B
      nodes(i).io.in(Route.S.id).bits.word.foreach {o => o := 0.U}
      nodes(i).io.in(Route.S.id).valid := false.B
    }
    if (yCord != 0 && yCord != p.nRows-1) {
      // middle nodes
      // connection to the top adjacent node
      nodes(i).io.out(Route.N.id) <> nodes(i - p.nCols).io.in(Route.S.id)
      nodes(i - p.nCols).io.out(Route.S.id) <> nodes(i).io.in(Route.N.id)
      // connection to the bottom adjacent node
      nodes(i).io.out(Route.S.id) <> nodes(i+p.nCols).io.in(Route.N.id)
      nodes(i+p.nCols).io.out(Route.N.id) <> nodes(i).io.in(Route.S.id)
    }
  }




  def getRoute(srcNode: (Int, Int), dstNode: (Int, Int), route: Seq[UInt] = Seq()): Seq[UInt] = {

    val srcXCord = srcNode._1
    val srcYCord = srcNode._2

    val dstXCord = dstNode._1
    val dstYCord = dstNode._2


    if (srcXCord == dstXCord && srcYCord == dstYCord) {
      val result = route ++ Seq.fill(p.maxRoutes - route.length)(X.asUInt)
      result
    }
    else {
      if (srcXCord < dstXCord) {
        val srcNode = (srcXCord + 1, srcYCord)
        getRoute(srcNode, dstNode, route :+ E.asUInt)
      } else if (srcXCord > dstXCord) {
        val srcNode = (srcXCord - 1, srcYCord)
        getRoute(srcNode, dstNode, route :+ W.asUInt)
      } else {
        if (srcYCord < dstYCord) {
          val srcNode = (srcXCord, srcYCord + 1)
          getRoute(srcNode, dstNode, route :+ S.asUInt)
        } else if (srcYCord > dstYCord) {
          val srcNode = (srcXCord, srcYCord - 1)
          getRoute(srcNode, dstNode, route :+ N.asUInt)
        } else {
          // this condition never rises
          getRoute(srcNode, dstNode)
        }
      }
    }

  }

  // helper function for correctly figuring out the id
  // since our mesh is in column major format
  def getDestNodeID(id: (UInt, UInt)): (UInt, UInt) = {
    (id._2, id._1)
  }



}

object MeshNetworkMain extends App {
  val p = MeshNetworkParams(2,2,16,1)
  println((new ChiselStage).emitVerilog(new MeshNetwork(p)))
}


