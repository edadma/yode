package xyz.hyperreal.yode.modules

import xyz.hyperreal.yode._

import scala.scalanative.native._

object Os {

  val module =
    Map(
      "uname" -> ((args: List[Any]) =>
        args match {
          case Nil =>
            val uname = stackalloc[uv.Utsname]

            bailOnError(uv.osUname(uname))
            Map(
              "sysname" -> fromCString(uname._1.cast[CString]),
              "release" -> fromCString(uname._2.cast[CString]),
              "version" -> fromCString(uname._3.cast[CString]),
              "machine" -> fromCString(uname._4.cast[CString])
            )
          case _ => illegalArguments("osUname", args, 0)
        })
    )

}
