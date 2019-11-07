package xyz.hyperreal

import scala.scalanative.native.{fromCString, CInt}

package object yode {

  def bailOnError(f: => CInt): Unit = {
    val result = f
    if (result < 0) {
      val errorName = uv.errName(result)
      println(s"Failed: $result ${fromCString(errorName)}")
      System.exit(1)
    }
  }

}
