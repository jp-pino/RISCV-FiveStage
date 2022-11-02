package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.{ BitPat, Cat }

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val PC = Output(UInt())
      val instruction = Output(new Instruction)

      // Stall
      val stall = Input(Bool())

      // Control hazards
      val squash = Input(Bool())

      // From EXMEM
      val EXMEMPC = Input(UInt(32.W))
      val EXMEMcontrolSignals = Input(new ControlSignals)
      val EXMEMbranchType = Input(UInt(3.W))
      val EXMEMcomparator = Input(Bool())
    })

  val IMEM = Module(new IMEM)
  val PC   = RegInit(UInt(32.W), 0.U)
  val PCOld = RegInit(UInt(32.W), 0.U)


  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  /**
    * TODO: Your code here.
    * 
    * You should expand on or rewrite the code below.
    */
  PCOld := io.PC
  io.PC := Mux(io.stall, PCOld, PC)
  IMEM.io.instructionAddress := io.PC

  // Add stalling functionality
  val branchOrJump = Wire(Bool())
  branchOrJump := ((io.EXMEMcontrolSignals.branch && io.EXMEMcomparator) || io.EXMEMcontrolSignals.jump) 
  val notSameAddress = Wire(Bool())
  notSameAddress := (io.EXMEMPC =/= io.PC)
  
  
  PC := Mux(branchOrJump, io.EXMEMPC, Mux(io.stall, PC, PC + 4.U))
  io.instruction := Mux(io.squash, Instruction.NOP, IMEM.io.instruction.asTypeOf(new Instruction))


  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    PC := 0.U
    io.instruction := Instruction.NOP
  }
}
