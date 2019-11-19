package xyz.hyperreal

import scala.collection.mutable
import scala.scalanative.native._

package object yode {

  var handles       = new mutable.HashMap[Long, yola.FunctionExpressionAST]
  val uvCallbackPtr = CFunctionPtr.fromFunction1(uvCallback)
  val loop          = uv.defaultLoop()
  val interp        = new yola.Interpreter(null)

  case class HandleWrapper(handle: Ptr[uv.Handle])

  def uvCallback(handle: Ptr[uv.TimerHandle]): Unit = {
    interp.call(null, handles(handle.cast[Long]), null, List(HandleWrapper(handle)))
  }

  def illegalArguments(name: String, args: List[Any], expected: Int) = {
    printError(s"Illegal arguments for function $name(), got ${args.length}, expected $expected")
  }

  def bailOnError(f: => CInt) = {
    val result = f
    if (result < 0) {
      val errorName = uv.errName(result)
      printError(s"Failed: $result ${fromCString(errorName)}")
    }
  }

  def printError(s: String) = {
    println(s)
    sys.exit(1)
  }
}
