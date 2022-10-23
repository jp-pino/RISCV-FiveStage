package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup


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
      val PC = Input(UInt(32.W))
      
      val immediate = Output(SInt(32.W))
      val RegA = Output(UInt(32.W))
      val RegB = Output(UInt(32.W))

      val controlSignals = Output(new ControlSignals)
      val branchType     = Output(UInt(3.W))
      val op1Select      = Output(UInt(1.W))
      val op2Select      = Output(UInt(1.W))
      val ALUop          = Output(UInt(4.W))

      // WB Stage
      val WBdata = Input(UInt(32.W))
      val WBinstruction = Input(new Instruction)
      val WBcontrolSignals = Input(new ControlSignals)
  
      // Stall
      val EXinstruction = Input(new Instruction)
      val EXcontrolSignals = Input(new ControlSignals)
      val stall = Output(Bool())
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
  // WB: connect to wb control signals
  registers.io.writeEnable  := io.WBcontrolSignals.regWrite
  registers.io.writeAddress := io.WBinstruction.registerRd 
  registers.io.writeData    := io.WBdata
  // printf("ALU RES[%d]: 0x%x | REG A[%d]: 0x%x | REG B[%d]: 0x%x\n", io.WBinstruction.registerRd, io.WBdata, io.instruction.registerRs1, registers.io.readData1, io.instruction.registerRs2, registers.io.readData2) 

  // Milestone 1. Connect register outputs to RegA and RegB wires
  // Milestone 3. Forward to save one cycle?
  io.RegA := Mux(io.instruction.registerRs1 === io.WBinstruction.registerRd, io.WBdata, registers.io.readData1)
  io.RegB := Mux(io.instruction.registerRs2 === io.WBinstruction.registerRd, io.WBdata, registers.io.readData2)

  // Milestone 1. Connect Decoder to instruction signal
  // Create a Mux to select the immediate using the immType 
  decoder.instruction := io.instruction
  io.immediate := MuxLookup(decoder.immType, 0.S(32.W), Array(
    ImmFormat.ITYPE -> decoder.instruction.immediateIType,
    ImmFormat.STYPE -> decoder.instruction.immediateSType,
    ImmFormat.BTYPE -> decoder.instruction.immediateBType,
    ImmFormat.UTYPE -> decoder.instruction.immediateUType,
    ImmFormat.JTYPE -> decoder.instruction.immediateJType,
    ImmFormat.SHAMT -> decoder.instruction.immediateZType,
    ImmFormat.DC -> decoder.instruction.immediateZType,
  ))

  // Milestone 3. Stall signal
  // io.stall := false.B
  io.stall := io.EXcontrolSignals.memRead && (io.EXinstruction.registerRs2 === io.instruction.registerRs1 || io.EXinstruction.registerRs2 === io.instruction.registerRs2)

  // Milestone 1. Connect control signals
  io.controlSignals := Mux(io.stall, (0.U).asTypeOf(new ControlSignals), decoder.controlSignals)
  io.branchType := decoder.branchType
  io.op1Select := decoder.op1Select
  io.op2Select := decoder.op2Select
  io.ALUop := decoder.ALUop
  
}
