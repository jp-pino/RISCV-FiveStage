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

  // ControlSignals Register
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


  // printf("=====IDEX=====\n" +
  //   "io.instructionIn: 0x%x\n" +
  //   "io.instructionOut: 0x%x\n" +
  //   "io.PCIn: 0x%x\n" +
  //   "io.PCOut: 0x%x\n" +
  //   "io.RegAIn: 0x%x\n" +
  //   "io.RegAOut: 0x%x\n" +
  //   "io.RegBIn: 0x%x\n" +
  //   "io.RegBOut: 0x%x\n" +
  //   "io.immediateIn: 0x%x\n" +
  //   "io.immediateOut: 0x%x\n" +
  //   "io.controlSignalsIn: 0x%x\n" +
  //   "io.controlSignalsOut: 0x%x\n" +
  //   "io.branchTypeIn: 0x%x\n" +
  //   "io.branchTypeOut: 0x%x\n" +
  //   "io.op1SelectIn: 0x%x\n" +
  //   "io.op1SelectOut: 0x%x\n" +
  //   "io.op2SelectIn: 0x%x\n" +
  //   "io.op2SelectOut: 0x%x\n" +
  //   "io.ALUopIn: 0x%x\n" +
  //   "io.ALUopOut: 0x%x\n",
  //   io.instructionIn.instruction,
  //   io.instructionOut.instruction,
  //   io.PCIn,
  //   io.PCOut,
  //   io.RegAIn,
  //   io.RegAOut,
  //   io.RegBIn,
  //   io.RegBOut,
  //   io.immediateIn,
  //   io.immediateOut,
  //   io.controlSignalsIn.asUInt(),
  //   io.controlSignalsOut.asUInt(),
  //   io.branchTypeIn,
  //   io.branchTypeOut,
  //   io.op1SelectIn,
  //   io.op1SelectOut,
  //   io.op2SelectIn,
  //   io.op2SelectOut,
  //   io.ALUopIn,
  //   io.ALUopOut)
}