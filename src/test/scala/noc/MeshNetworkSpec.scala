package noc
import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec


class MeshNetworkSpec extends AnyFlatSpec with ChiselScalatestTester {
  import MeshNode.State._
  import Route._
  behavior of "MeshNetwork"

  def getNextHopFlattenedID(nextHop: Route, flattenedSourceNodeID: Int, p: MeshNetworkParams): Int = {
    if (nextHop == E) {
      flattenedSourceNodeID + 1
    } else if (nextHop == W) {
      flattenedSourceNodeID - 1
    } else if (nextHop == S) {
      flattenedSourceNodeID + p.nCols
    } else if (nextHop == N) {
      flattenedSourceNodeID - p.nCols
    } else {
      0
    }
  }
  def sendRequestToAdjacentNode(dut: MeshNetwork, srcNode: Int, routes: Seq[Route], payload: Int, p: MeshNetworkParams): Unit = {
    val nextHop = routes.head
    val nextRoute = routes.tail
    val nextHopNodeID = getNextHopFlattenedID(nextHop, srcNode, p)
    if (nextHop == X) {
      dut.io.state(srcNode).expect(idle)
      dut.io.data(srcNode).expect(payload.U)
      println("finished sending the packet")
    } else {
      dut.io.state(srcNode).expect(sendingHeader)
      dut.clock.step()
      dut.io.state(srcNode).expect(sendingPayload)
      dut.io.state(nextHopNodeID).expect(receivingPayload)
      dut.clock.step()
      dut.io.state(srcNode).expect(sendingEnd)
      dut.io.state(nextHopNodeID).expect(receivingEnd)
      dut.clock.step()
      sendRequestToAdjacentNode(dut, nextHopNodeID, nextRoute, payload, p)
    }
  }
  def startRequest(dut: MeshNetwork, srcNodeID: (Int, Int), destNode: (Int, Int), routes: Seq[Route], payload: Int, p: MeshNetworkParams) = {
    val sourceNodeID = getSourceNodeID(srcNodeID._1, srcNodeID._2, p)
    val nodeIO = dut.io.requestPacket(sourceNodeID)
    val nextHop = routes.head
    val nextRoute = routes.tail
    val nextHopNodeID = getNextHopFlattenedID(nextHop, sourceNodeID, p)

    nodeIO.ready.expect(true.B)
    nodeIO.valid.poke(true.B)
    nodeIO.bits.payload.poke(payload.U)
    nodeIO.bits.destNodeID(0).poke(destNode._1.U)
    nodeIO.bits.destNodeID(1).poke(destNode._2.U)
    dut.clock.step()
    nodeIO.valid.poke(false.B)

    if (nextHop == X) {
      dut.io.state(sourceNodeID).expect(receivingEnd)
      dut.clock.step()
      dut.io.state(sourceNodeID).expect(idle)
      dut.io.data(sourceNodeID).expect(payload)
      println("finished sending the packet")
    } else {

      // sender node should be in sending header state
      dut.io.state(sourceNodeID).expect(sendingHeader)
      // other nodes are in idle state
      val otherNodeIDs = (0 until p.numOfNodes).filter(_ !== sourceNodeID)
      otherNodeIDs.foreach { i => dut.io.state(i).expect(idle) }
      dut.clock.step()
      // sender node should be in sending payload state
      dut.io.state(sourceNodeID).expect(sendingPayload)
      // receiving adjacent node should be in receiving payload state
      dut.io.state(nextHopNodeID).expect(receivingPayload)
      // other nodes should be in idle state
      (0 until p.numOfNodes).filter(_ !== sourceNodeID).filter(_ !== nextHopNodeID).foreach {
        s => dut.io.state(s).expect(idle)
      }
      dut.clock.step()
      // sender node should be in ending state
      dut.io.state(sourceNodeID).expect(sendingEnd)
      // receiving adjacent node should be in receiving end state
      dut.io.state(nextHopNodeID).expect(receivingEnd)
      // other nodes should be in idle state
      //    (0 until p.numOfNodes).filter(i => (i.U !== sourceNodeID & i.U !== nextHopNodeID)).foreach {
      //      s => dut.io.state(s).expect(idle)
      //    }
      dut.clock.step()
      // the packet from sender node is transmitted to the next hop here
      // if the next hop is destination then we don't move forward
      sendRequestToAdjacentNode(dut, nextHopNodeID, nextRoute, payload, p)
    }

  }

  def getSourceNodeID(xCord: Int, yCord: Int, p: MeshNetworkParams): Int = {
    if (yCord == 0) {
      xCord
    } else if (yCord == p.nRows - 1) {
      p.nCols * (p.nRows - 1) + xCord
    } else {
      (p.nCols - 1) + yCord + xCord
    }
  }

  it should "send data from each node to every other node in a 2x2 mesh" in {
    val p = MeshNetworkParams(2,2,16,1)
    MeshNetworkSim(rows=p.nRows, cols=p.nCols, packetSize=16, phits=16)
    MeshNetworkSim.connectNodes()

    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) {dut =>
      // the mesh looks like the following
      //  node 00 --- node 10
      //    |           |
      //  node 01 --- node 11
      println("-------------------------------------------------------------------")
      println("|                                                                 |")
      println("|************ STARTING RIGOROUS TESTING FOR 2x2 MESH *************|")
      println("|                                                                 |")
      println("-------------------------------------------------------------------")
      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j,i)
            val payload = i*j+1
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            println("starting the request from node " + srcNodeID + " to node " + destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p)
          }
        }
      }}


    }

  }

  it should "send data from each node to every other node in a 3x3 mesh" in {
    val p = MeshNetworkParams(3, 3, 16, 1)
    MeshNetworkSim(rows = p.nRows, cols = p.nCols, packetSize = 16, phits = 16)
    MeshNetworkSim.connectNodes()
    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // the mesh looks like the following
      //  node 00 --- node 10 --- node 20
      //    |           |           |
      //  node 01 --- node 11 --- node 21
      //    |           |           |
      //  node 02 --- node 12 --- node 22
      println("-------------------------------------------------------------------")
      println("|                                                                 |")
      println("|************ STARTING RIGOROUS TESTING FOR 3X3 MESH *************|")
      println("|                                                                 |")
      println("-------------------------------------------------------------------")

      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j, i)
            val payload = i * j + 1
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            println("starting the request from node " + srcNodeID + " to node " + destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p)
          }
        }
      }}

    }

  }

}