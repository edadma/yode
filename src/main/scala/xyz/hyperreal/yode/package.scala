package xyz.hyperreal

import scala.scalanative.native._

package object yode {

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
