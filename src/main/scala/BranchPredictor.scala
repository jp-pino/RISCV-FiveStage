package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output
import chisel3.util.MuxLookup

// Milestone 4. Implement BranchPredictor. 
class BranchPredictor extends Module {
  final val ADDRESS_BITS = 9

  val io = IO(new Bundle {
    val setup = Input(Bool())

    // Signals to update table
    val taken = Input(Bool())
    val address = Input(UInt(32.W))
    val target = Input(UInt(32.W))
    val update = Input(Bool())
    val controlSignals = Input(new ControlSignals)

    // Signals to get predictions
    val addressPrediction = Input(UInt(32.W))
    val targetPrediction = Output(UInt(32.W))
    val prediction = Output(Bool())
  })

  val tag = Wire(UInt(ADDRESS_BITS.W))
  val history   = Mem((scala.math.pow(2, ADDRESS_BITS).toInt), UInt(2.W))
  val addresses = Mem((scala.math.pow(2, ADDRESS_BITS).toInt), UInt(32.W))
  val targets   = Mem((scala.math.pow(2, ADDRESS_BITS).toInt), UInt(32.W))

  tag := io.address(ADDRESS_BITS, 0)

  // Stats
  // val error = RegInit(UInt(10.W), 0.U)

  when(io.update && io.controlSignals.branch && !io.setup) {
    when(io.taken && (history(tag) < 3.U)) {
      printf("UPDATE  TRUE: %x -> %x : %x\n", io.address, io.target, history(tag))
      history(tag) := history(tag) + 1.U
    }.elsewhen(!io.taken && (history(tag) > 0.U)) {
      printf("UPDATE FALSE: %x -> %x : %x\n", io.address, io.target, history(tag))
      history(tag) := history(tag) - 1.U
    }
    addresses(tag) := io.address
    targets(tag) := io.target
  }
  

  val tagPrediction = Wire(UInt(ADDRESS_BITS.W))
  tagPrediction := io.addressPrediction(ADDRESS_BITS, 0)

  when((addresses(tagPrediction) === io.addressPrediction) && addresses(tagPrediction) =/= 0.U) {
    io.prediction := true.B
    io.targetPrediction := Mux(history(tagPrediction) >= 2.U, targets(tagPrediction), io.addressPrediction + 4.U)
  }.otherwise{
    io.prediction := false.B
    io.targetPrediction := 0.U
  }
}