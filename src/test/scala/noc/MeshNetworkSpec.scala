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
  def sendRequestToAdjacentNode(dut: MeshNetwork, srcNode: Int, routes: Seq[Route], payload: Seq[Int], p: MeshNetworkParams, counter: Int): Unit = {
    var cycleCounter = counter
    val nextHop = routes.head
    val nextRoute = routes.tail
    val nextHopNodeID = getNextHopFlattenedID(nextHop, srcNode, p)
    if (nextHop == X) {
      println("packet reached the destination, storing the payload in internal buffer")
      dut.io.state(srcNode).expect(idle)
      for (i <- 0 until p.payloadPhits) {
        println("the payload phit received is : " + dut.io.data(srcNode)(i).peek())
        dut.io.data(srcNode)(i).expect(i + 1)
      }

    } else {
      println("now sending packet to the next adjacent node")
      println("sending the header to " + nextHop + " node")
      dut.io.state(srcNode).expect(sendingHeader)
      dut.clock.step()
      cycleCounter += 1 // just for printing clock cycles
      println("CYCLE: " + cycleCounter)
      println("sending payload phits")
      dut.io.state(srcNode).expect(sendingPayload)
      dut.io.state(nextHopNodeID).expect(receivingPayload)
      dut.clock.step(p.payloadPhits)
      cycleCounter += p.payloadPhits // just for printing clock cycles
      println("CYCLE: " + cycleCounter)
      sendRequestToAdjacentNode(dut, nextHopNodeID, nextRoute, payload, p, cycleCounter)
    }
  }
  def startRequest(dut: MeshNetwork, srcNodeID: (Int, Int), destNode: (Int, Int), routes: Seq[Route], payload: Seq[Int], p: MeshNetworkParams, counter: Int) = {
    var cycleCounter = counter
    println("CYCLE: " + cycleCounter)
    println("starting the request from node " + srcNodeID + " to node " + destNode)
    println("payload phits in the packet are: " + payload.length)
    val sourceNodeID = getSourceNodeID(srcNodeID._1, srcNodeID._2, p)
    val nodeIO = dut.io.requestPacket(sourceNodeID)
    val nextHop = routes.head
    val nextRoute = routes.tail
    val nextHopNodeID = getNextHopFlattenedID(nextHop, sourceNodeID, p)

    nodeIO.ready.expect(true.B)
    nodeIO.valid.poke(true.B)
    nodeIO.bits.destNodeID(0).poke(destNode._1.U)
    nodeIO.bits.destNodeID(1).poke(destNode._2.U)

    payload.foreach {p => {
      println("writing the payload ")
      nodeIO.bits.payload.poke(p.U)
      dut.clock.step()
      cycleCounter += 1      // just for printing clock cycles
      println("CYCLE: " + cycleCounter)
    }}
    println("all the payload phits to the node are written")
    nodeIO.valid.poke(false.B)

    if (nextHop == X) {
      println("sending to ourselves so do nothing, just store the data in internal buffer")
      dut.io.state(sourceNodeID).expect(idle)
      for (i <- 0 until p.payloadPhits) {
        dut.io.data(sourceNodeID)(i).expect(i+1)
      }
      println("finished sending the packet")
    } else {
      println("sending the header to " + nextHop + " node")
      // sender node should be in sending header state
      dut.io.state(sourceNodeID).expect(sendingHeader)
      // other nodes are in idle state
      val otherNodeIDs = (0 until p.numOfNodes).filter(_ !== sourceNodeID)
      otherNodeIDs.foreach { i => dut.io.state(i).expect(idle) }
      dut.clock.step()
      cycleCounter += 1 // just for printing clock cycles
      println("CYCLE: " + cycleCounter)
      // sender node should be in sending payload state
      println("sending payload phits")
      dut.io.state(sourceNodeID).expect(sendingPayload)
      // receiving adjacent node should be in receiving payload state
      dut.io.state(nextHopNodeID).expect(receivingPayload)
      // other nodes should be in idle state
      (0 until p.numOfNodes).filter(_ !== sourceNodeID).filter(_ !== nextHopNodeID).foreach {
        s => dut.io.state(s).expect(idle)
      }
      dut.clock.step(p.payloadPhits)
      cycleCounter += p.payloadPhits // just for printing clock cycles
      println("CYCLE: " + cycleCounter)
      // the packet from sender node is transmitted to the next hop here
      // if the next hop is destination then we don't move forward
      sendRequestToAdjacentNode(dut, nextHopNodeID, nextRoute, payload, p, cycleCounter)
    }

  }

  def getSourceNodeID(xCord: Int, yCord: Int, p: MeshNetworkParams): Int = {
    if (yCord == 0) {
      xCord
    } else {
      p.nCols*yCord + xCord
    }
  }

  it should "send data from each node to every other node in a 2x2 mesh" in {
    val p = MeshNetworkParams(2, 2, 16, payloadPhits = 3)
    MeshNetworkSim(rows = p.nRows, cols = p.nCols, packetSize = 16, phits = 16)
    MeshNetworkSim.connectNodes()


    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // the mesh looks like the following
      //  node 00 --- node 10
      //    |           |
      //  node 01 --- node 11
      println("-------------------------------------------------------------------")
      println("|                                                                 |")
      println("|************ STARTING RIGOROUS TESTING FOR 2x2 MESH *************|")
      println("|                                                                 |")
      println("-------------------------------------------------------------------")
      var cycleCounter = 0
      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j, i)
            val payload = Seq.tabulate(p.payloadPhits)(i => i + 1)
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p, cycleCounter)
          }
        }
      }}

    }
  }

  it should "send data from each node to every other node in a 3x3 mesh" in {
    val p = MeshNetworkParams(3, 3, 16, payloadPhits = 1)
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
      var cycleCounter = 0
      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j, i)
            val payload = Seq.tabulate(p.payloadPhits)(i => i + 1)
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p, cycleCounter)
          }
        }
      }}

    }

  }

  it should "send data from each node to every other node in a 4x2 mesh" in {
    val p = MeshNetworkParams(4, 2, 16, payloadPhits = 1)
    MeshNetworkSim(rows = p.nRows, cols = p.nCols, packetSize = 16, phits = 16)
    MeshNetworkSim.connectNodes()
    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // the mesh looks like the following
      //  node 00 --- node 10
      //    |           |
      //  node 01 --- node 11
      //    |           |
      //  node 02 --- node 12
      //    |           |
      //  node 03 --- node 13
      println("-------------------------------------------------------------------")
      println("|                                                                 |")
      println("|************ STARTING RIGOROUS TESTING FOR 4X2 MESH *************|")
      println("|                                                                 |")
      println("-------------------------------------------------------------------")
      var cycleCounter = 0
      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j, i)
            val payload = Seq.tabulate(p.payloadPhits)(i => i + 1)
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p, cycleCounter)
          }
        }
      }
      }

    }

  }

  it should "send data from each node to every other node in a 3x5 mesh" in {
    val p = MeshNetworkParams(3, 5, 16, payloadPhits = 1)
    MeshNetworkSim(rows = p.nRows, cols = p.nCols, packetSize = 16, phits = 16)
    MeshNetworkSim.connectNodes()
    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // the mesh looks like the following
      //  node 00 --- node 10 --- node 20 --- node 30 --- node 40
      //    |           |           |           |           |
      //  node 01 --- node 11 --- node 21 --- node 31 --- node 41
      //    |           |           |           |           |
      //  node 02 --- node 12 --- node 22 --- node 32 --- node 42

      println("-------------------------------------------------------------------")
      println("|                                                                 |")
      println("|************ STARTING RIGOROUS TESTING FOR 3X5 MESH *************|")
      println("|                                                                 |")
      println("-------------------------------------------------------------------")
      var cycleCounter = 0
      val nodeSims = MeshNetworkSim.mesh.flatten
      nodeSims.foreach { nodeSim => {
        for (i <- 0 until p.nRows) {
          for (j <- 0 until p.nCols) {
            val srcNodeID = (nodeSim.id._1, nodeSim.id._2)
            val destNodeID = (j, i)
            val payload = Seq.tabulate(p.payloadPhits)(i => i + 1)
            val routingTable = MeshNetworkSim.getRoutingTable(srcNodeID)
            val routes = routingTable(destNodeID)
            startRequest(dut, srcNodeID, destNodeID, routes, payload, p, cycleCounter)
          }
        }
      }
      }

    }

  }
}