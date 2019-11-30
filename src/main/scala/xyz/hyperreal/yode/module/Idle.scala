package xyz.hyperreal.yode.module

import xyz.hyperreal.yode._
import xyz.hyperreal.yola

import scala.scalanative.native._

object Idle {

  val exports =
    Map(
      "setIdle" -> ((args: List[Any]) => {
        val idleHandle = stdlib.malloc(uv.handleSize(uvConst.TIMER_HANDLE)).cast[Ptr[uv.IdleHandle]]

        uv.idleInit(loop, idleHandle)
        handles(idleHandle.cast[Long]) = args.head.asInstanceOf[yola.FunctionExpressionAST]
        uv.idleStart(idleHandle, uvCallbackPtr)

        HandleWrapper(idleHandle)
      }),
      "clearIdle" -> (
          (args: List[Any]) =>
            args.head match {
              case HandleWrapper(handle) =>
                uv.idleStop(handle)
                stdlib.free(handle.cast[Ptr[Byte]])
            }
        )
    )
}
