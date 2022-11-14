package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output
import chisel3.util.MuxLookup


// Milestone 1. Implement Execute Stage. 
//   Add ALU (from its own module)
class Execute extends Module {
  val io = IO(new Bundle {
    val instruction = Input(new Instruction)
    val PC = Input(UInt(32.W))
    val PCOut = Output(UInt(32.W))

    val RegA = Input(UInt(32.W))
    val RegB = Input(UInt(32.W))
    val RegBOut = Output(UInt(32.W))
    val EXMEMVal = Input(UInt(32.W))
    val MEMWBVal = Input(UInt(32.W))

    val EXMEMinstruction = Input(new Instruction)
    val MEMWBinstruction = Input(new Instruction)

    val immediate = Input(SInt(32.W))

    val aluResult = Output(UInt(32.W))

    val controlSignals = Input(new ControlSignals)
    val EXMEMcontrolSignals = Input(new ControlSignals)
    val MEMWBcontrolSignals = Input(new ControlSignals)
    val branchType     = Input(UInt(3.W))
    val op1Select      = Input(UInt(1.W))
    val op2Select      = Input(UInt(1.W))
    val ALUop          = Input(UInt(4.W))

    val comparator = Output(Bool())

    val prediction = Input(UInt(32.W))
    val mispredict = Output(Bool())
  })


  val ALU = Module(new ALU).io
  val Comparator = Module(new Comparator).io


  // Forwarding
  val regSourceA = Wire(UInt(32.W))
  val regSourceB = Wire(UInt(32.W))
  val rs1 = Wire(UInt(32.W))
  val rs2 = Wire(UInt(32.W))

  when(io.EXMEMcontrolSignals.regWrite 
    && io.EXMEMinstruction.registerRd =/= 0.U 
    && io.EXMEMinstruction.registerRd === io.instruction.registerRs1){
    // EX Hazard
    regSourceA := RegSource.EXMEM
  }.elsewhen(io.MEMWBcontrolSignals.regWrite 
    && io.MEMWBinstruction.registerRd =/= 0.U 
    && io.EXMEMinstruction.registerRd =/= io.instruction.registerRs1
    && io.MEMWBinstruction.registerRd === io.instruction.registerRs1){
    // MEM Hazard
    regSourceA := RegSource.MEMWB
  }.otherwise{
    regSourceA := RegSource.IDEX
  }

  when(io.EXMEMcontrolSignals.regWrite 
    && io.EXMEMinstruction.registerRd =/= 0.U 
    && io.EXMEMinstruction.registerRd === io.instruction.registerRs2){
    // EX Hazard
    regSourceB := RegSource.EXMEM
  }.elsewhen(io.MEMWBcontrolSignals.regWrite 
    && io.MEMWBinstruction.registerRd =/= 0.U 
    && io.EXMEMinstruction.registerRd =/= io.instruction.registerRs2
    && io.MEMWBinstruction.registerRd === io.instruction.registerRs2){
    // MEM Hazard
    regSourceB := RegSource.MEMWB
  }.otherwise{
    regSourceB := RegSource.IDEX
  }

  rs1 := MuxLookup(regSourceA, 0.U, Array(
    RegSource.IDEX -> io.RegA,
    RegSource.EXMEM -> io.EXMEMVal,
    RegSource.MEMWB -> io.MEMWBVal
  ))

  rs2 := MuxLookup(regSourceB, 0.U, Array(
    RegSource.IDEX -> io.RegB,
    RegSource.EXMEM -> io.EXMEMVal,
    RegSource.MEMWB -> io.MEMWBVal
  ))

  io.RegBOut := rs2

  // printf("RegA: 0x%x | RegB: 0x%x | EXMEMVal: 0x%x | MEMWBVal: 0x%x\n", io.RegA, io.RegB, io.EXMEMVal, io.MEMWBVal)
  // printf("RS1[%d]: 0x%x | RS2[%d]: 0x%x | RES: 0x%x\n\n  ", regSourceA, rs1, regSourceB, rs2, ALU.aluResult)


  

  // ALU
  ALU.aluOp := io.ALUop
  ALU.op1 := MuxLookup(io.op1Select, 0.U, Array(
    Op1Select.rs1 -> rs1,
    Op1Select.PC -> io.PC,
    Op1Select.DC -> 0.U
  ))  
  ALU.op2 := MuxLookup(io.op2Select, 0.U, Array(
    Op2Select.rs2 -> rs2,
    Op2Select.imm -> io.immediate.asUInt(),
    Op2Select.DC -> 0.U
  ))
  io.aluResult := ALU.aluResult

  // Jump logic
  io.PCOut := Mux(io.branchType === branchType.link, ((rs1.asSInt() + io.immediate).asUInt()) & "hfffffffe".U, (io.PC.asSInt() - 4.S + io.immediate).asUInt()).asUInt() 
  // printf("branchType: %x | imm: %x | PC: %x | pc + imm + mask: %x | pc + imm: %x \n", io.branchType, io.immediate, io.PC, ((io.PC.asSInt() + io.immediate).asUInt()) & "hfffffffe".U, (io.PC.asSInt() + io.immediate).asUInt())
  io.mispredict := (io.PCOut =/= io.prediction) && (io.comparator) && (io.controlSignals.branch /*|| io.controlSignals.jump*/)

  // Branch comparator
  Comparator.op1 := rs1
  Comparator.op2 := rs2
  Comparator.branchType := io.branchType
  io.comparator := Comparator.result
}