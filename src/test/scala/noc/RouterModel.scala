package noc
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.Map
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
}

object RoutingTable {
  import Route._
  def apply(topology: MeshNetwork, sourceNode: Node): Map[Node, Seq[Route]] = {
    // initialize an empty route table
    var routeTable: Map[Node, Seq[Route]] = Map()
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

//object RoutingTableTestData {
//  def apply(row: Int, col: Int, node: Node) = {
//    if (row==2 && col==4) {
//      val routeTableforSrc0 = Map(node -> Seq())
//    }
//  }
//
//}

class RoutingTableModel extends AnyFlatSpec with ChiselScalatestTester {
  import Route._
  behavior of "RoutingTable"
  it should "give the correct route for source node 0" in {
    val row = 2
    val col = 4
    val mesh = new MeshNetwork(row,col)

    val routingTable: Map[Node, Seq[Route]] = RoutingTable(topology = mesh, sourceNode = mesh.grid(1)(1))
    routingTable.foreach { case(n, routes) => println("node: " + n.id + " with routes: " + routes)}
  }
}

// later the topology can be of type abstract Network
class RouterModel(topology: MeshNetwork) {

}
