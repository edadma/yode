package xyz.hyperreal.yode

import xyz.hyperreal.yode.uv.Utsname

import scala.scalanative.native.{fromCString, stackalloc, CString}

object Modules {

  val importModule
  val builtin =
    Map(
      "yode" ->
        Map(
          "os" ->
            Map(
              "osUname" -> ((args: List[Any]) =>
                args match {
                  case Nil =>
                    val uname = stackalloc[Utsname]

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
        )
    )

}
