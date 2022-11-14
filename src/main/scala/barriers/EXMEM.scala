package FiveStage
import chisel3._
import chisel3.core.Input
import chisel3.core.Output


// Milestone 1. Implement EXMEM Barrier. 
//   All signals are delayed by a cycle except data memory read value
//   PC, RegA, RegB, Control Signals(), Immediate Select MUX Result
class EXMEM extends Module {
  val io = IO(new Bundle {
    val PCIn = Input(UInt(32.W))
    val PCOut = Output(UInt(32.W))

    val instructionIn = Input(new Instruction)
    val instructionOut = Output(new Instruction)

    val aluResultIn = Input(UInt(32.W))
    val aluResultOut = Output(UInt(32.W))

    val RegBIn = Input(UInt(32.W))
    val RegBOut = Output(UInt(32.W))

    val controlSignalsIn = Input(new ControlSignals)
    val controlSignalsOut = Output(new ControlSignals)

    val branchTypeIn = Input(UInt(3.W))
    val branchTypeOut = Output(UInt(3.W))

    val comparatorIn = Input(Bool())
    val comparatorOut = Output(Bool())

    val mispredictIn = Input(Bool())
    val mispredictOut = Output(Bool())
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

  // RegB Register
  val RegB = RegInit(UInt(32.W), 0.U)
  // Wire through register
  RegB := io.RegBIn
  io.RegBOut := RegB

  // AluResult Register
  val aluResult = RegInit(UInt(32.W), 0.U)
  // Wire through register
  aluResult := io.aluResultIn
  io.aluResultOut := aluResult

  // Immediate Register
  val controlSignals = RegInit(Reg(new ControlSignals))
  // Wire through register
  controlSignals := io.controlSignalsIn
  io.controlSignalsOut := controlSignals.asTypeOf(new ControlSignals)

  // BranchType Register
  val branchType = RegInit(UInt(3.W), 0.U)
  // Wire through register
  branchType := io.branchTypeIn
  io.branchTypeOut := branchType

  // Comparator Register
  val comparator = RegInit(Bool(), false.B)
  // Wire through register
  comparator := io.comparatorIn
  io.comparatorOut := comparator

  // Mispredict Register
  val mispredict = RegInit(Bool(), false.B)
  // Wire through register
  mispredict := io.mispredictIn
  io.mispredictOut := mispredict
}