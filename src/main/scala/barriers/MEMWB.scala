package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output


// Milestone 1. Implement EXMEM Barrier. 
//   All signals are delayed by a cycle except data memory read value
//   PC, RegA, RegB, Control Signals(), Immediate Select MUX Result
class MEMWB extends Module {
  val io = IO(new Bundle {
    val instructionIn = Input(new Instruction)
    val instructionOut = Output(new Instruction)

    val controlSignalsIn = Input(new ControlSignals)
    val controlSignalsOut = Output(new ControlSignals)

    val dataMemIn = Input(UInt(32.W))
    val dataMemOut = Output(UInt(32.W))

    val aluResultIn = Input(UInt(32.W))
    val aluResultOut = Output(UInt(32.W))
  })

  // Instruction Register
  val instruction = RegInit(Reg(new Instruction))
  // Wire through register
  instruction := io.instructionIn
  io.instructionOut := instruction.asTypeOf(new Instruction)

  // Immediate Register
  val controlSignals = RegInit(Reg(new ControlSignals))
  // Wire through register
  controlSignals := io.controlSignalsIn
  io.controlSignalsOut := controlSignals.asTypeOf(new ControlSignals)

  // DataMem 
  io.dataMemOut := io.dataMemIn

  // AluResult Register
  val aluResult = RegInit(UInt(32.W), 0.U)
  // Wire through register
  aluResult := io.aluResultIn
  io.aluResultOut := aluResult
}