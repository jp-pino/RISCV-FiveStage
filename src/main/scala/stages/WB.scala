package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup


class WriteBack extends MultiIOModule {
  val io = IO(
    new Bundle {
      // WB Stage
      val aluResultIn = Input(UInt(32.W))
      val dataMemIn = Input(UInt(32.W))
      val controlSignals = Input(new ControlSignals)

      val outputData = Output(UInt(32.W))
    }
  )

  io.outputData := Mux(io.controlSignals.memRead, io.dataMemIn, io.aluResultIn)   
}
