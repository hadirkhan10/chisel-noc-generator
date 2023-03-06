package noc
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Queue
import noc.MeshNetworkSim.NodeID
import noc.Route.Route


class Channel(src: NodeSim, dst: NodeSim) {
  def from = src
  def to = dst
}


class NodeSim(val id: (Int, Int)) {
  import Route._

  var channels: LinkedHashMap[Route, (Channel, Channel)] = LinkedHashMap()

  // each route has two channel tuple
  // one channel is the output from this route
  // the other channel is the input to this route
  // TODO: make this channel creation generic for each node
  //val channels: Map[Route, (Channel, Channel)] = Map(
  //  E -> (new Channel(src = this, dst = new Node((1, 0))), new Channel(src = new Node((1, 0)), dst = this)), S -> (new Channel(src = this, dst = new Node((0, 1))), new Channel(src = new Node((0, 1)), dst = this)))

//  val channels: Map[Route, (Channel, Channel)] = Map(
//    E -> (new Channel(src = this, dst = new Node((0, 0))), new Channel(src = new Node((0, 0)), dst = this)), S -> (new Channel(src = this, dst = new Node((0, 1))), new Channel(src = new Node((0, 1)), dst = this)))


  // we will follow a credit-based flow control for buffer management
  // each of the downstream channel has it's own buffer in the upstream router
  // before sending the packet the router checks if the selected downstream's channel
  // has an empty packet space in the buffer. Upon sending the packet, the router
  // decrements the buffer space. When the downstream node forwards the packet, it
  // sends back a credit to the upstream router which then increments the space in the buffer


  val router: Router = new Router(channels)

  def startRequest(dstNodeID: (Int, Int), payload: Seq[Int]): Boolean = {
    // this function would tell the router of this node to start a sending request
    router.initiateRequest(srcNodeID=id, dstNodeID=dstNodeID, payload)
  }

}

//class Counter(numOfChannels: Int, sizeOfBuffer: Int) {
//  var counters: ArrayBuffer[Int] = ArrayBuffer.fill(numOfChannels)(0)
//}

class Router(channels: LinkedHashMap[Route,(Channel,Channel)]) {
  import Route._
  // the packets we receive from each input channel
  var packetBuffer: Seq[Queue[Packet]] = Seq.fill(channels.size)(Queue())


  def initiateRequest(srcNodeID: NodeID, dstNodeID: NodeID, payload: Seq[Int]): Boolean = {
    // get the route for the destination node
    val routes: Seq[Route] = routingTableLookup(srcNodeID, dstNodeID)
    // get the first route digit
    val route = routes.head
    // send the rest of the routing digits
    val nextRoute = routes.tail
    val packet = formPacket(payload, nextRoute)
    sendRequest(route, packet)
  }


  def sendRequest(route: Route, packet: Packet): Boolean = {
    println("sending request towards direction: " + route)
    channels(route)._1.to.router.getRequest(packet, from = route)
  }

  def formPacket(payload: Seq[Int], routes: Seq[Route]): Packet = {
    new Packet(payload, routes)
  }

  def getRequest(packet: Packet, from: Route): Boolean = {
    // this function will be called when we receive a packet from some source
    println("Received the request")
    println("next hop is: " + packet.header.head)
    println("remaining route is: " + packet.header.tail)
    // get the route from the header phit
    val route = packet.header
    // check if the route is X
    if (route == Seq(X)) {
      // we are the destination so send a true response
      println("REACHED THE DESTINATION!!")
      // TODO: also store the payload in queue
      true
    } else {
      // we are an intermediate node
      // use the route to select the output port and pass the tail to the next node
      val hop = route.head
      val nextRoute = route.tail
      val payload = packet.payload
      val nextPacket = formPacket(payload, nextRoute)
      // send the packet forward to the next node
      sendRequest(hop, nextPacket)
    }
  }

  def routingTableLookup(srcNodeID: NodeID, dstNodeID: NodeID): Seq[Route] = {
    val routingTable: LinkedHashMap[NodeID, Seq[Route]] = MeshNetworkSim.getRoutingTable(srcNodeID)
    val routeToDst: Seq[Route] = routingTable(dstNodeID)
    routeToDst
  }

}

// the payload could be null in-case of a header phit that's why we need +A
// so that it has an upper bound of Any
class Packet(data: Seq[Int], routes: Seq[Route]) {

  val header =  routes
  val payload: Seq[Int] = data

}

object Route extends Enumeration {
  type Route = Value
  // N points to the north direction node
  // S points to the south direction node
  // E points to the east direction node
  // W points to the west direction node
  // X points to the current node being the destination node
  val N, S, E, W, X = Value
}

//object PhitType extends Enumeration {
//  type PhitType = Value
//  // H is a header phit that carries the route to the next node
//  // P is a payload phit that carries the data payload to be sent to next node
//  // EN is an ending phit that indicates the end of the packet
//  val H, P, EN = Value
//}

object MeshNetworkSim {
  import Route._
  type NodeID = (Int, Int)

  // the key is the source node
  // the value is a hashmap for all destination nodes and their routes
  var routingTables: LinkedHashMap[NodeID, LinkedHashMap[NodeID, Seq[Route]]] = LinkedHashMap()
  var mesh: ArrayBuffer[ArrayBuffer[NodeSim]] = ArrayBuffer()
  var row = 0
  var col = 0


  def apply(rows: Int, cols: Int, packetSize: Int, phits: Int): Unit = {
    mesh = ArrayBuffer.tabulate(rows, cols) { case (i, j) => new NodeSim((j, i)) }
    row = rows
    col = cols
    // creates a routing table for each node
    for (i <- 0 until row) {
      for (j <- 0 until col) {
        val sourceNode = mesh(i)(j)
        val routingTable: LinkedHashMap[NodeID, Seq[Route]] = RoutingTable(topology = mesh, sourceNode = sourceNode)
        routingTables += (sourceNode.id -> routingTable)
      }
    }
  }

  def connectNodes() = {

    //Map[Route, (Channel, Channel)]
    // Map(
    //  E -> (new Channel(src = this, dst = new Node((1, 0))), new Channel(src = new Node((1, 0)), dst = this)), S -> (new Channel(src = this, dst = new Node((0, 1))), new Channel(src = new Node((0, 1)), dst = this)))

    val nodes = mesh.flatten
    for (i <- 0 until nodes.length) {
      val xCord = nodes(i).id._1
      val yCord = nodes(i).id._2
      if (xCord == 0) {
        // all nodes in left column
        nodes(i).channels += (E -> (new Channel(src=nodes(i), dst=nodes(i+1)), new Channel(src=nodes(i+1), dst=nodes(i))))
      }
      if (xCord == col-1) {
        // all nodes in right column
        nodes(i).channels += (W -> (new Channel(src=nodes(i), dst=nodes(i-1)), new Channel(src=nodes(i-1), dst=nodes(i))))
      }
      if (xCord != 0 && xCord != col-1) {
        // middle nodes
        nodes(i).channels += (E -> (new Channel(src=nodes(i), dst=(nodes(i+1))), new Channel(src=nodes(i+1), dst=nodes(i))))
        nodes(i).channels += (W -> (new Channel(src=nodes(i), dst=nodes(i-1)), new Channel(src=nodes(i-1), dst=nodes(i))))
      }

      if (yCord == 0) {
        // top row nodes
        nodes(i).channels += (S -> (new Channel(src=nodes(i), dst=nodes(i+col)), new Channel(src=nodes(i+col), dst=nodes(i))))
      }
      if (yCord == row-1) {
        // bottom row nodes
        nodes(i).channels += (N -> (new Channel(src=nodes(i), dst=nodes(i-col)), new Channel(src=nodes(i-col), dst=nodes(i))))
      }
      if (yCord != 0 && yCord != row-1) {
        // middle nodes
        nodes(i).channels += (N -> (new Channel(src = nodes(i), dst = (nodes(i-col))), new Channel(src = nodes(i-col), dst = nodes(i))))
        nodes(i).channels += (S -> (new Channel(src = nodes(i), dst = nodes(i+col)), new Channel(src = nodes(i+col), dst = nodes(i))))
      }

    }
  }

  def startRequest(from: NodeID, to: NodeID, payload: Seq[Int]): Boolean = {
    val nodes = mesh.flatten
    val indexOfSrcNode = nodes.indexWhere(n => n.id == from)
    val indexOfDstNode = nodes.indexWhere(n => n.id == to)
    val srcNode = nodes(indexOfSrcNode)
    val dstNode = nodes(indexOfDstNode)
    srcNode.startRequest(dstNode.id, payload)

  }


  def getRoutingTable(sourceNode: NodeID): LinkedHashMap[NodeID, Seq[Route]] = routingTables(sourceNode)

}

object RoutingTable {
  import Route._

  def apply(topology: ArrayBuffer[ArrayBuffer[NodeSim]], sourceNode: NodeSim): LinkedHashMap[NodeID, Seq[Route]] = {
    // initialize an empty route table
    var routeTable: LinkedHashMap[NodeID, Seq[Route]] = LinkedHashMap()
    // get a list of all the nodes in the mesh
    val nodes: ArrayBuffer[NodeSim] = topology.flatten

    for (i <- 0 until nodes.length) {

      if (sourceNode.id._1 == nodes(i).id._1 && sourceNode.id._2 == nodes(i).id._2) {
        routeTable += (sourceNode.id -> Seq(X))
      } else {
        routeTable += (nodes(i).id -> getRoute(toNode = nodes(i), fromNode = sourceNode))
      }
    }

    routeTable
  }

  def getRoute(toNode: NodeSim, fromNode: NodeSim): Seq[Route] = {
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

  def apply(row: Int, col: Int, node: NodeSim): LinkedHashMap[NodeSim, Seq[Route]] = {
    if (row==2 && col==2) {
      if (node.id == (0,0)) {
        LinkedHashMap[NodeSim, Seq[Route]](
          new NodeSim((0,0)) -> List(X),
          new NodeSim((1,0)) -> List(E, X),
          new NodeSim((0,1)) -> List(S, X),
          new NodeSim((1,1)) -> List(E,S,X))
      } else if (node.id == (1,0)) {
        LinkedHashMap[NodeSim, Seq[Route]](
          new NodeSim((0, 0)) -> List(W, X),
          new NodeSim((1, 0)) -> List(X),
          new NodeSim((0, 1)) -> List(W, S, X),
          new NodeSim((1, 1)) -> List(S, X))
      } else if (node.id == (0, 1)) {
        LinkedHashMap[NodeSim, Seq[Route]](
          new NodeSim((0, 0)) -> List(N, X),
          new NodeSim((1, 0)) -> List(E, N, X),
          new NodeSim((0, 1)) -> List(X),
          new NodeSim((1, 1)) -> List(E, X))
      } else if (node.id == (1, 1)) {
        LinkedHashMap[NodeSim, Seq[Route]](
          new NodeSim((0, 0)) -> List(W, N, X),
          new NodeSim((1, 0)) -> List(N, X),
          new NodeSim((0, 1)) -> List(W, X),
          new NodeSim((1, 1)) -> List(X))
      } else {LinkedHashMap[NodeSim, Seq[Route]]()}
    } else {
      LinkedHashMap[NodeSim, Seq[Route]]()
    }
  }

}

// NOTES:
// phits is the number of digits that our channel ports can transfer each cycle
// word is a phit concatenated with the phit type
// packet is a collection of words
// each router expects a header phit followed by multiple payload phits and
// a single ending phit

// calculate the number of words in a packet
// for e.g if packet size = 64 bits
// and phits = 16 bits then each packet
// has 4 words + 2 (1 header and 1 ending) that need to be transferred per cycle

class MeshNetworkModel extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MeshNetwork"
  it should "give the correct route for all source nodes in a 2x2 mesh" in {
    val row = 2
    val col = 2

    val packetSize = 16
    val phits = 16

    val nWords = packetSize/phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows=row, cols=col, packetSize=16, phits=16)
    MeshNetworkSim.connectNodes()
    for (i <- 0 until row) {
      for (j <- 0 until col) {
        val sourceNode = new NodeSim((j,i))
        val routingTable = MeshNetworkSim.getRoutingTable(sourceNode.id)
        val testRoutingTable = RoutingTableTestData(row,col,sourceNode)
        routingTable zip testRoutingTable foreach { case (m1, m2) => assert(m1._2 == m2._2)}
      }
    }
  }

  it should "send the packet to a destination node (2,2) from node (0,0) in a 3x3 mesh" in {
    val row = 3
    val col = 3

    val packetSize = 16
    val phits = 16

    val nWords = packetSize / phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows = row, cols = col, packetSize = 16, phits = 16)
    // this connects the nodes together in a mesh
    MeshNetworkSim.connectNodes()
    // this functions returns true when the destination node receives the data
    println("STARTING TO SEND DATA FROM NODE (0,0) TO NODE (2,2)")
    val result = MeshNetworkSim.startRequest(from = (0, 0), to = (2, 2), Seq.fill(nWords)(data))
    assert (result == true)
  }

  it should "send the packet to a destination node (1,2) from node (0,0) in a 3x3 mesh" in {
    val row = 3
    val col = 3

    val packetSize = 16
    val phits = 16

    val nWords = packetSize / phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows = row, cols = col, packetSize = 16, phits = 16)
    // this connects the nodes together in a mesh
    MeshNetworkSim.connectNodes()
    // this functions returns true when the destination node receives the data
    println("STARTING TO SEND DATA FROM NODE (0,0) TO NODE (1,2)")
    val result = MeshNetworkSim.startRequest(from = (0, 0), to = (1, 2), Seq.fill(nWords)(data))
    assert(result == true)
  }

  it should "send the packet to a destination node (0,0) from node (2,2) in a 3x3 mesh" in {
    val row = 3
    val col = 3

    val packetSize = 16
    val phits = 16

    val nWords = packetSize / phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows = row, cols = col, packetSize = 16, phits = 16)
    // this connects the nodes together in a mesh
    MeshNetworkSim.connectNodes()
    // this functions returns true when the destination node receives the data
    println("STARTING TO SEND DATA FROM NODE (2,2) TO NODE (0,0)")
    val result = MeshNetworkSim.startRequest(from = (2, 2), to = (0, 0), Seq.fill(nWords)(data))
    assert(result == true)
  }
  it should "send the packet to a destination node (2,1) from node (0,2) in a 3x3 mesh" in {
    val row = 3
    val col = 3

    val packetSize = 16
    val phits = 16

    val nWords = packetSize / phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows = row, cols = col, packetSize = 16, phits = 16)
    // this connects the nodes together in a mesh
    MeshNetworkSim.connectNodes()
    // this functions returns true when the destination node receives the data
    println("STARTING TO SEND DATA FROM NODE (0,2) TO NODE (2,1)")
    val result = MeshNetworkSim.startRequest(from = (0, 2), to = (2, 1), Seq.fill(nWords)(data))
    assert(result == true)
  }

  it should "send the packet to a destination node (3,1) from node (0,0) in a 2x4 mesh" in {
    val row = 2
    val col = 4

    val packetSize = 16
    val phits = 16

    val nWords = packetSize / phits
    val data = 10

    // this setups up the mesh of nodes and their routers
    MeshNetworkSim(rows = row, cols = col, packetSize = 16, phits = 16)
    // this connects the nodes together in a mesh
    MeshNetworkSim.connectNodes()
    // this functions returns true when the destination node receives the data
    println("STARTING TO SEND DATA FROM NODE (0,0) TO NODE (3,1)")
    val result = MeshNetworkSim.startRequest(from = (0, 0), to = (3, 1), Seq.fill(nWords)(data))
    assert(result == true)
  }
}
