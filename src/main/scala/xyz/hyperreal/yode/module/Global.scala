package xyz.hyperreal.yode.module

import xyz.hyperreal.yode._
import xyz.hyperreal.yola

import scala.scalanative.native._

object Global {
  val prt = (args: List[Any]) => println(args map yola.display mkString ", ")

  val exports =
    Map(
      "println" -> prt,
      "setInterval" -> ((args: List[Any]) => {
        val timerHandle = stdlib.malloc(uv.handleSize(uvConst.TIMER_HANDLE)).cast[Ptr[uv.TimerHandle]]

        uv.timerInit(loop, timerHandle)
        handles(timerHandle.cast[Long]) = args.head.asInstanceOf[yola.FunctionExpressionAST]
        uv.timerStart(
          timerHandle,
          uvCallbackPtr,
          args.tail.head.asInstanceOf[BigDecimal].toLongExact,
          args.tail.head.asInstanceOf[BigDecimal].toLongExact
        )

        HandleWrapper(timerHandle)
      }),
      "clearInterval" -> (
          (args: List[Any]) =>
            args.head match {
              case HandleWrapper(handle) =>
                uv.timerStop(handle)
                stdlib.free(handle.cast[Ptr[Byte]])
            }
        )
    )
}
