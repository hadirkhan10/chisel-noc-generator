package noc
import chisel3._
import chisel3.util._

//class Router(channels: Vec[Channel], bufferSize: Int) {
//  // buffer to store incoming packet for each input channel
//  //val packetBuffer = Seq.fill(channels.length)(Module(new Queue(UInt(), bufferSize)))
//}

//class Channel(phits: Int) extends Bundle {
//  val id: Routes.Type =
//}

class MeshNode(val xCord: Int, val yCord: Int, p: MeshNetworkParams) extends Module {
  var nChannels = 4
  if (xCord == 0 || xCord == p.nCols-1) {
    nChannels -= 1
    if (yCord == 0 || yCord == p.nRows-1) nChannels -= 1
  } else if (yCord == 0 || yCord == p.nRows-1) {
    nChannels -= 1
  }

  val io = IO(new Bundle {
    val in = Vec(4, Flipped(Decoupled(UInt(p.phits.W))))
    val out = Vec(4, Decoupled(UInt(p.phits.W)))
  })
  // Decoupled
  // ready -> input
  // valid -> output
  // bits -> output

  // Flipped Decoupled
  // ready -> output
  // valid -> input
  // bits -> input
  private val xCordReg = RegInit(xCord.U)
  private val yCordReg = RegInit(yCord.U)

  def id: Vec[UInt] = VecInit(xCordReg, yCordReg)

  // setting all outputs to be 0 or false
  io.out.foreach {dio => {
    dio.valid := false.B
    dio.bits := 0.U
  }}
  io.in.foreach {dio => {
    dio.ready := false.B
  }}

   //val router = new Router(io, bufferSize)


}
