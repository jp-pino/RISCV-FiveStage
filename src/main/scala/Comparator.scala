package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output
import chisel3.util.MuxLookup


// Milestone 2. Implement Comparator. 
class Comparator extends Module {
  val io = IO(new Bundle {
    val branchType = Input(UInt(3.W))

    val op1 = Input(UInt(32.W))
    val op2 = Input(UInt(32.W))

    val result = Output(Bool())
  })

  
  val OpMap = Array(
    branchType.beq -> (io.op1 === io.op2),
    branchType.neq -> (io.op1 =/= io.op2),
    branchType.gte -> (io.op1.asSInt() >= io.op2.asSInt()),
    branchType.lt -> (io.op1.asSInt() < io.op2.asSInt()),
    branchType.gteu -> (io.op1 >= io.op2),
    branchType.ltu -> (io.op1 < io.op2),
    branchType.jump -> (true.B),
    branchType.link -> (true.B)
  ) 

  // printf("1: 0x%x | 2: 0x%x | RES: %x\n", io.op1, io.op2, io.op1 << io.op2(4, 0))

  // MuxLookup API: https://github.com/freechipsproject/chisel3/wiki/Muxes-and-Input-Selection#muxlookup
  io.result := MuxLookup(io.branchType, 0.U(1.W), OpMap)
}