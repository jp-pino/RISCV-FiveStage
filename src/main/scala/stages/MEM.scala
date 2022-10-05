package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val PC = Input(UInt(32.W))
      val instruction = Input(new Instruction)
      val RegB = Input(UInt(32.W))
      val aluResult = Input(UInt(32.W))
      val controlSignals = Input(new ControlSignals)
      val branchType = Input(UInt(3.W))

      val dataOut = Output(UInt(32.W))
    })


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates


  /**
    * Your code here.
    */
  DMEM.io.dataIn      := io.RegB
  DMEM.io.dataAddress := io.aluResult
  DMEM.io.writeEnable := io.controlSignals.memWrite
  io.dataOut := DMEM.io.dataOut
}
