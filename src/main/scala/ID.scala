package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      /**
        * TODO: Your code here.
        */
      val instruction = Input(new Instruction)
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  // Milestone 1. Connect Register addresses to instruction signal (RS1 and RS2)
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  // TODO: Part of future milestone
  registers.io.writeEnable  := false.B 
  registers.io.writeAddress := 0.U     
  registers.io.writeData    := 0.U     

  // Milestone 1. Connect Decoder to instruction signal
  decoder.instruction := io.instruction
  // TODO: decoder.immType goes into my mux
}
