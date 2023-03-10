package noc
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.stage.ChiselStage
import chisel3.util.Cat

import scala.collection.mutable.ArrayBuffer

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

import Routes._

case class MeshNetworkParams(nRows: Int, nCols: Int, phits: Int, bufferSize: Int) {
  require(nRows > 1)
  require(nCols > 1)
  val numOfNodes = nRows * nCols
  val maxRoutes = nCols - 1 + nRows
}

class MeshNetwork(p: MeshNetworkParams) extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Vec(p.maxRoutes, UInt()))
  })


  // create the mesh of node modules
  val mesh = Seq.tabulate(p.nRows, p.nCols) { case(i,j) => Module(new MeshNode(xCord = j, yCord = i, p))}


  val nodes = mesh.flatten
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
      nodes(i).io.in(Route.W.id).bits := 0.U
      nodes(i).io.in(Route.W.id).valid := false.B
    }
    if (xCord == p.nCols-1) {
      // this node is in right column
      // connection to left adjacent node
      nodes(i).io.out(Route.W.id) <> nodes(i-1).io.in(Route.E.id)
      nodes(i-1).io.out(Route.E.id) <> nodes(i).io.in(Route.W.id)
      // no connection to the right adjacent node
      nodes(i).io.out(Route.E.id).ready := false.B
      nodes(i).io.in(Route.E.id).bits := 0.U
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
      nodes(i).io.in(Route.N.id).bits := 0.U
      nodes(i).io.in(Route.N.id).valid := false.B
    }
    if (yCord == p.nRows-1) {
      // this node is in the bottom row
      // connection to the top adjacent node
      nodes(i).io.out(Route.N.id) <> nodes(i - p.nCols).io.in(Route.S.id)
      nodes(i - p.nCols).io.out(Route.S.id) <> nodes(i).io.in(Route.N.id)
      // no connection to the bottom adjacent node
      nodes(i).io.out(Route.S.id).ready := false.B
      nodes(i).io.in(Route.S.id).bits := 0.U
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


  val routingTable = VecInit.tabulate(p.numOfNodes,p.nRows,p.nCols) { (n,x,y) => {
    val sourceNodeIndex = n
    val nodes = mesh.flatten
    val sourceNode = nodes(sourceNodeIndex)
    val destNode = (y,x)

    VecInit(getRoute((sourceNode.xCord, sourceNode.yCord), destNode))
  }}

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

  // TODO: Figure out the logic for this
  def getSourceNodeID(id: (Int,Int)): Int = {
    ???
  }

  // helper function for correctly figuring out the id
  // since our mesh is in column major format
  def getDestNodeID(id: (Int, Int)): (Int, Int) = {
    (id._2, id._1)
  }
   //finding the route from node 00 to node 01

  val destNodeID = getDestNodeID((0,1))
  io.out := routingTable(0)(destNodeID._1)(destNodeID._2)


}

object MeshNetworkMain extends App {
  val p = MeshNetworkParams(2,2,16,1)
  println((new ChiselStage).emitVerilog(new MeshNetwork(p)))
}


