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
    val immediate = Input(SInt(32.W))

    val aluResult = Output(UInt(32.W))

    val controlSignals = Input(new ControlSignals)
    val branchType     = Input(UInt(3.W))
    val op1Select      = Input(UInt(1.W))
    val op2Select      = Input(UInt(1.W))
    val ALUop          = Input(UInt(4.W))

    val comparator = Output(Bool())
  })


  val ALU = Module(new ALU).io
  val Comparator = Module(new Comparator).io

  ALU.aluOp := io.ALUop
  ALU.op1 := MuxLookup(io.op1Select, 0.U, Array(
    Op1Select.rs1 -> io.RegA,
    Op1Select.PC -> io.PC,
    Op1Select.DC -> 0.U
  ))
  ALU.op2 := MuxLookup(io.op2Select, 0.U, Array(
    Op2Select.rs2 -> io.RegB,
    Op2Select.imm -> io.immediate.asUInt(),
    Op2Select.DC -> 0.U
  ))
  // Jump logic. TODO: ask about + 4?? shouldn't we already have the PC+4 in
  io.aluResult := Mux(io.controlSignals.jump, io.PC + 4.U, ALU.aluResult)

  io.PCOut := (io.PC.asSInt() + io.immediate).asUInt()

  // Branch comparator
  Comparator.op1 := io.RegA
  Comparator.op2 := io.RegB
  Comparator.branchType := io.branchType
  io.comparator := Comparator.result

  // printf("PC: 0x%x | ", io.PC)
}