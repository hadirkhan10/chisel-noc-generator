package noc
import chiseltest.ChiselScalatestTester
import noc.Route.X
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ArrayBuffer


class Node(position: (Int, Int)) {
  val id: (Int, Int) = position
}

object Route extends Enumeration {
  type Route = Value
  val N, S, E, W, X = Value
}



class MeshNetwork(row: Int, col: Int) {
  val grid: Seq[Seq[Node]] = Seq.tabulate(row, col){case (i,j) => new Node((j,i))}

  def getNode(id: (Int, Int)): Node = {
    val nodes = grid.flatten
    val index = nodes.indexWhere(n => n.id == id)
    nodes(index)
  }
}

object RoutingTable {
  import Route._
  def apply(topology: MeshNetwork, sourceNode: Node): LinkedHashMap[Node, Seq[Route]] = {
    // initialize an empty route table
    var routeTable: LinkedHashMap[Node, Seq[Route]] = LinkedHashMap()
    // get a list of all the nodes in the mesh
    val nodes: Seq[Node] = topology.grid.flatten

    for (i <- 0 until nodes.length) {

      if (sourceNode.id._1 == nodes(i).id._1 && sourceNode.id._2 == nodes(i).id._2) {
        routeTable += (sourceNode -> Seq(X))
      } else {
        routeTable += (nodes(i) -> getRoute(toNode = nodes(i), fromNode = sourceNode))
      }
    }

    routeTable
  }

  def getRoute(toNode: Node, fromNode: Node): Seq[Route] = {
    var route: ArrayBuffer[Route] = ArrayBuffer[Route]()
    val srcXCord = fromNode.id._1
    val srcYCord = fromNode.id._2

    val dstXCord = toNode.id._1
    val dstYCord = toNode.id._2

    var newXCord = srcXCord
    var newYCord = srcYCord


    while (newXCord != dstXCord || newYCord != dstYCord) {
      if (newXCord < dstXCord) {
        newXCord += 1
        route += E
      } else if (newXCord > dstXCord) {
        newXCord -= 1
        route += W
      } else {
        // x cords are equal
        // start moving down/up now
        if (newYCord < dstYCord) {
          newYCord += 1
          route += S
        } else if (newYCord > dstYCord) {
          newYCord -= 1
          route += N
        }
      }
    }
    route += X
    route.toSeq
  }


}

object RoutingTableTestData {
  import Route._

  def apply(row: Int, col: Int, node: Node): LinkedHashMap[Node, Seq[Route]] = {
    if (row==2 && col==2) {
      if (node.id == (0,0)) {
        LinkedHashMap[Node, Seq[Route]](
          new Node((0,0)) -> List(X),
          new Node((1,0)) -> List(E, X),
          new Node((0,1)) -> List(S, X),
          new Node((1,1)) -> List(E,S,X))
      } else if (node.id == (1,0)) {
        LinkedHashMap[Node, Seq[Route]](
          new Node((0, 0)) -> List(W, X),
          new Node((1, 0)) -> List(X),
          new Node((0, 1)) -> List(W, S, X),
          new Node((1, 1)) -> List(S, X))
      } else if (node.id == (0, 1)) {
        LinkedHashMap[Node, Seq[Route]](
          new Node((0, 0)) -> List(N, X),
          new Node((1, 0)) -> List(E, N, X),
          new Node((0, 1)) -> List(X),
          new Node((1, 1)) -> List(E, X))
      } else if (node.id == (1, 1)) {
        LinkedHashMap[Node, Seq[Route]](
          new Node((0, 0)) -> List(W, N, X),
          new Node((1, 0)) -> List(N, X),
          new Node((0, 1)) -> List(W, X),
          new Node((1, 1)) -> List(X))
      }
      else {???}
    } else {
      LinkedHashMap[Node, Seq[Route]]()
    }
  }

}

class RoutingTableModel extends AnyFlatSpec with ChiselScalatestTester {
  import Route._
  behavior of "RoutingTable"
  it should "give the correct route for all source nodes in a 2x2 mesh" in {
    val row = 2
    val col = 2
    val mesh = new MeshNetwork(row,col)
    for (i <- 0 until row) {
      for (j <- 0 until col) {
        val sourceNode = new Node((i,j))
        val routingTable: LinkedHashMap[Node, Seq[Route]] = RoutingTable(topology = mesh, sourceNode = mesh.getNode(sourceNode.id))
        val testRoutingTable = RoutingTableTestData(row,col,sourceNode)

        routingTable zip testRoutingTable foreach { case (m1, m2) => assert(m1._2 == m2._2)}
      }
    }

  }
}

// later the topology can be of type abstract Network
class RouterModel(topology: MeshNetwork) {

}
