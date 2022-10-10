package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output
import chisel3.util.MuxLookup


// Milestone 1. Implement ALU. 
class ALU extends Module {
  val io = IO(new Bundle {
    val aluOp = Input(UInt(4.W))

    val op1 = Input(UInt(32.W))
    val op2 = Input(UInt(32.W))

    val aluResult = Output(UInt(32.W))
  })

  
  val ALUopMap = Array(
    ALUOps.ADD    -> (io.op1 + io.op2),
    ALUOps.SUB    -> (io.op1 - io.op2),
    ALUOps.AND    -> (io.op1 & io.op2),
    ALUOps.OR     -> (io.op1 | io.op2),
    ALUOps.XOR     -> (io.op1 ^ io.op2),
    ALUOps.SLT    -> (io.op1.asSInt() < io.op2.asSInt()),
    ALUOps.SLTU   -> (io.op1 < io.op2),
    ALUOps.SRA    -> ((io.op1.asSInt() >> io.op2(4, 0)).asUInt()),
    ALUOps.SRL    -> (io.op1 >> io.op2(4, 0)),
    ALUOps.SLL    -> (io.op1 << io.op2(4, 0))
  ) 

  // printf("1: 0x%x | 2: 0x%x | RES: %x\n", io.op1, io.op2, io.op1 << io.op2(4, 0))

  // MuxLookup API: https://github.com/freechipsproject/chisel3/wiki/Muxes-and-Input-Selection#muxlookup
  io.aluResult := MuxLookup(io.aluOp, 0.U(32.W), ALUopMap)
}