package xyz.hyperreal

import scala.scalanative.native._

package object yode {

  def illegalArguments(name: String, args: List[Any], expected: Int): Unit = {
    println(s"Illegal arguments for function $name(), got ${args.length}, expected $expected")
    System.exit(1)
  }

  def bailOnError(f: => CInt): Unit = {
    val result = f
    if (result < 0) {
      val errorName = uv.errName(result)
      println(s"Failed: $result ${fromCString(errorName)}")
      System.exit(1)
    }
  }

}
