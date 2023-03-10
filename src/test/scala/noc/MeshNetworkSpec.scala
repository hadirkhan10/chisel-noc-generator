package noc
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class MeshNetworkSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MeshNetwork"
  it should "print values of routes" in {
    val p = MeshNetworkParams(2,2,16,1)
    test(new MeshNetwork(p)) {dut =>
      dut.io.in.poke(false.B)
      dut.clock.step(4)
    }

  }
}