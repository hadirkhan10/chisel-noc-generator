package noc
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.stage.ChiselStage
import chisel3.util.Cat

import scala.collection.mutable.ArrayBuffer

object Routes extends ChiselEnum {
  val N, S, E, W, X = Value
}

import Routes._

case class MeshNetworkParams(nRows: Int, nCols: Int, phits: Int, bufferSize: Int) {
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


  // setting all the inputs to be 0 or false
  mesh.flatten.foreach(n => n.io.in.zip(n.io.out).foreach { case(din, dout) => {
    din.valid :=  false.B
    din.bits := 0.U
    dout.ready := false.B
  }})


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
  val p = MeshNetworkParams(2,4,16,1)
  println((new ChiselStage).emitVerilog(new MeshNetwork(p)))
}


