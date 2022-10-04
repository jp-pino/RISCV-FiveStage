package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output


// Milestone 1. Implement Instruction fetch barrier. 
//   PC is delayed by a cycle
//   Instruction is wired directly
class IFBarrier extends Module {
  val io = IO(new Bundle {
    val instructionIn = Input(new Instruction)
    val instructionOut = Output(new Instruction)

    val PCIn = Input(UInt(32.W))
    val PCOut = Output(UInt(32.W))
  })

  val PC = RegInit(UInt(32.W), 0.U)

  // Wire through register
  PC := io.PCIn
  io.PCOut := PC
  
  // Wire directly
  io.instructionOut := io.instructionIn
}