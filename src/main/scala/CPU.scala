package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFID  = Module(new IFID).io
  val IDEX  = Module(new IDEX).io
  val EXMEM = Module(new EXMEM).io
  val MEMWB = Module(new MEMWB).io


  val IF  = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?)


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */

  //// Milestone 1. Connect ID and IF through IFID Barrier
  
  // IFID Inputs
  // Connect output instrction from IF stage to IFID barrier instrunction input
  IFID.instructionIn := IF.io.instruction
  // Connect output PC from IF stage to IFID barrier PC input
  IFID.PCIn := IF.io.PC

  // ID Inputs
  // Connect instruction output from IFID barrier to ID stage
  ID.io.instruction := IFID.instructionOut
  // Connect PC output from IFID barrier to ID stage
  ID.io.PC := IFID.PCOut

  // IDEX Inputs (from IFID and ID)
  IDEX.PCIn := IFID.PCOut
  IDEX.instructionIn := IFID.instructionOut
  IDEX.RegAIn := ID.io.RegA
  IDEX.RegBIn := ID.io.RegB
  IDEX.immediateIn := ID.io.immediate
  IDEX.controlSignalsIn := ID.io.controlSignals
  IDEX.branchTypeIn := ID.io.branchType
  IDEX.op1SelectIn := ID.io.op1Select
  IDEX.op2SelectIn := ID.io.op2Select
  IDEX.ALUopIn := ID.io.ALUop

  // EX Inputs (from IDEX)
  EX.io.PC := IDEX.PCOut
  EX.io.instruction := IDEX.instructionOut
  EX.io.RegA := IDEX.RegAOut
  EX.io.RegB := IDEX.RegBOut
  EX.io.immediate := IDEX.immediateOut
  EX.io.controlSignals := IDEX.controlSignalsOut
  EX.io.branchType := IDEX.branchTypeOut
  EX.io.op1Select := IDEX.op1SelectOut
  EX.io.op2Select := IDEX.op2SelectOut
  EX.io.ALUop := IDEX.ALUopOut

  // EXMEM Inputs
  EXMEM.PCIn := IDEX.PCOut
  EXMEM.instructionIn := IDEX.instructionOut
  EXMEM.RegBIn := IDEX.RegBOut
  EXMEM.aluResultIn := EX.io.aluResult
  EXMEM.controlSignalsIn := IDEX.controlSignalsOut
  EXMEM.branchTypeIn := IDEX.branchTypeOut
  
  // MEM Inputs
  MEM.io.PC := EXMEM.PCOut
  MEM.io.instruction := EXMEM.instructionOut
  MEM.io.RegB := EXMEM.RegBOut
  MEM.io.aluResult := EXMEM.aluResultOut
  MEM.io.controlSignals := EXMEM.controlSignalsOut
  MEM.io.branchType := EXMEM.branchTypeOut

  // MEMWB Inputs
  MEMWB.instructionIn := EXMEM.instructionOut
  MEMWB.controlSignalsIn := EXMEM.controlSignalsOut
  MEMWB.dataMemIn := MEM.io.dataOut
  MEMWB.aluResultIn := EXMEM.aluResultOut

  // WB is implemented in ID stage
  ID.io.WBaluResultIn := MEMWB.aluResultOut
  ID.io.WBdataMemIn := MEMWB.dataMemOut
  ID.io.WBinstruction := MEMWB.instructionOut
  ID.io.WBcontrolSignals := MEMWB.controlSignalsOut
}
