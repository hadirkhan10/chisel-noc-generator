package noc
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class MeshNetworkSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MeshNetwork"
  it should "send data from node 00 to node 11 in a 2x2 mesh" in {
    import MeshNode.State._
    val p = MeshNetworkParams(2,2,16,1)
    test(new MeshNetwork(p)).withAnnotations(Seq(WriteVcdAnnotation)) {dut =>
      // the mesh looks like the following
      //  node 00 --- node 10
      //    |           |
      //  node 01 --- node 11

      // the routing direction will be node 00 -> node 10 -> node 11
      // the state output for nodes is a flattened mesh so
      // node 00 -> io.state(0)  node 10 -> io.state(1) node 11 -> io.state(3)

      // all nodes are idle, ready to accept the request, and have no data in the buffer
      dut.io.state.foreach {s => s.expect(idle)}
      dut.io.requestPacket.foreach {rp => rp.ready.expect(true.B)}
      dut.io.data.foreach {d => d.expect(0.U)}

      // start a request from node 00
      val node0 = dut.io.requestPacket(0)
      node0.valid.poke(true.B)
      node0.bits.payload.poke(10.U)
      node0.bits.destNodeID(0).poke(1.U)
      node0.bits.destNodeID(1).poke(1.U)
      dut.clock.step()
      node0.valid.poke(false.B)
      // node 00 should be in sending header state
      dut.io.state(0).expect(sendingHeader)
      // node 10 should be in idle state
      dut.io.state(1).expect(idle)
      // node 11 should be in idle state
      dut.io.state(3).expect(idle)
      dut.clock.step()
      // node 00 should be in sending payload state
      dut.io.state(0).expect(sendingPayload)
      // node 01 should be in receiving payload state
      dut.io.state(1).expect(receivingPayload)
      // node 11 should be in idle state
      dut.io.state(3).expect(idle)
      dut.clock.step()
      // node 00 should be in sending end state
      dut.io.state(0).expect(sendingEnd)
      // node 01 should be in receiving end state
      dut.io.state(1).expect(receivingEnd)
      // node 11 should be in idle state
      dut.io.state(3).expect(idle)
      dut.clock.step()
      // the packet from node 00 to node 10 is transmitted here
      // node 10 now starts transmitting the packet to node 11

      // node 00 should be in idle state
      dut.io.state(0).expect(idle)
      // node 01 should be in sending header state
      dut.io.state(1).expect(sendingHeader)
      // node 11 should be in idle state
      dut.io.state(2).expect(idle)
      dut.clock.step()
      // node 01 should be in sending payload state
      dut.io.state(1).expect(sendingPayload)
      // node 11 should be in receiving payload state
      dut.io.state(3).expect(receivingPayload)
      dut.clock.step()
      // node 01 should be in sending end state
      dut.io.state(1).expect(sendingEnd)
      // node 11 should be in receiving end state
      dut.io.state(3).expect(receivingEnd)
      dut.clock.step()
      // the packet from node 01 to node 11 is transmitted here
      // since node 11 is the destination it goes to idle instead of sending header state
      // it also stores the data received in its buffer
      dut.io.state(1).expect(idle)
      dut.io.state(3).expect(idle)
      dut.io.data(3).expect(10.U)
    }

  }
  
  // TODO: send data from node 11 to node 00 in a 2x2 mesh
  // TODO: send data from node 00 to node 00 in a 2x2 mesh
  // TODO: send data from node 11 to node 00 in a 2x2 mesh

}