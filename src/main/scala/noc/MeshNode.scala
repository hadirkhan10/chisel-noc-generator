package noc
import chisel3._
import chisel3.util._

//class Router(channels: Vec[Channel], bufferSize: Int) {
//  // buffer to store incoming packet for each input channel
//  //val packetBuffer = Seq.fill(channels.length)(Module(new Queue(UInt(), bufferSize)))
//}

class Channel(phits: Int) extends Bundle {
  val in = Flipped(Decoupled(UInt(phits.W)))
  val out = Decoupled(UInt(phits.W))
}

class MeshNode(val xCord: Int, val yCord: Int, p: MeshNetworkParams) extends Module {
  var nChannels = 4
  if (xCord == 0 || xCord == p.nCols-1) {
    nChannels -= 1
    if (yCord == 0 || yCord == p.nRows-1) nChannels -= 1
  } else if (yCord == 0 || yCord == p.nRows-1) {
    nChannels -= 1
  }
  println("creating node: " + (xCord,yCord) + " with channels: " + nChannels)

//  val io = IO(Vec(4, new Channel(phits)))
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })
  private val xCordReg = RegInit(xCord.U)
  private val yCordReg = RegInit(yCord.U)

  def id: Vec[UInt] = VecInit(xCordReg, yCordReg)
  io.out := io.in

   //val router = new Router(io, bufferSize)


}
