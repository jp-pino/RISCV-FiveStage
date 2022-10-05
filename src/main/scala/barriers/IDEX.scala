package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output


// Milestone 1. Implement IDEX Barrier. 
//   All signals are delayed by a cycle
//   PC, RegA, RegB, Control Signals(), Immediate Select MUX Result
class IDEX extends Module {
  val io = IO(new Bundle {
    val PCIn = Input(UInt(32.W))
    val PCOut = Output(UInt(32.W))


    val instructionIn = Input(new Instruction)
    val instructionOut = Output(new Instruction)


    val RegAIn = Input(UInt(32.W))
    val RegAOut = Output(UInt(32.W))


    val RegBIn = Input(UInt(32.W))
    val RegBOut = Output(UInt(32.W))

    
    val immediateIn = Input(SInt(32.W))
    val immediateOut = Output(SInt(32.W))

    val controlSignalsIn = Input(new ControlSignals)
    val controlSignalsOut = Output(new ControlSignals)

    val branchTypeIn = Input(UInt(3.W))
    val branchTypeOut = Output(UInt(3.W))

    val op1SelectIn = Input(UInt(1.W))
    val op1SelectOut = Output(UInt(1.W))

    val op2SelectIn = Input(UInt(1.W))
    val op2SelectOut = Output(UInt(1.W))

    val ALUopIn = Input(UInt(4.W))
    val ALUopOut = Output(UInt(4.W))

  })

  // PC Register
  val PC = RegInit(UInt(32.W), 0.U)
  // Wire through register
  PC := io.PCIn
  io.PCOut := PC

  // Instruction Register
  val instruction = RegInit(Reg(new Instruction))
  // Wire through register
  instruction := io.instructionIn
  io.instructionOut := instruction.asTypeOf(new Instruction)

  // RegA Register
  val RegA = RegInit(UInt(32.W), 0.U)
  // Wire through register
  RegA := io.RegAIn
  io.RegAOut := RegA

  // RegB Register
  val RegB = RegInit(UInt(32.W), 0.U)
  // Wire through register
  RegB := io.RegBIn
  io.RegBOut := RegB

  // Immediate Register
  val immediate = RegInit(SInt(32.W), 0.S)
  // Wire through register
  immediate := io.immediateIn
  io.immediateOut := immediate

  // Immediate Register
  val controlSignals = RegInit(Reg(new ControlSignals))
  // Wire through register
  controlSignals := io.controlSignalsIn
  io.controlSignalsOut := controlSignals.asTypeOf(new ControlSignals)

  // BranchType Register
  val branchType = RegInit(UInt(32.W), 0.U)
  // Wire through register
  branchType := io.branchTypeIn
  io.branchTypeOut := branchType

  // Op1Select Register
  val op1Select = RegInit(UInt(32.W), 0.U)
  // Wire through register
  op1Select := io.op1SelectIn
  io.op1SelectOut := op1Select

  // Op2Select Register
  val op2Select = RegInit(UInt(32.W), 0.U)
  // Wire through register
  op2Select := io.op2SelectIn
  io.op2SelectOut := op2Select

  // ALUop Register
  val ALUop = RegInit(UInt(32.W), 0.U)
  // Wire through register
  ALUop := io.ALUopIn
  io.ALUopOut := ALUop
  
}