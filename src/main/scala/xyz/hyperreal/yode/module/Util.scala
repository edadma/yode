package xyz.hyperreal.yode.module

import xyz.hyperreal.yode._

import scala.scalanative.native._

object Util {
  val BUF_SIZE = 1000

  val exports =
    Map(
      "osUname" -> (
          (args: List[Any]) =>
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
            }
      ),
      "hrTime" -> (
          (args: List[Any]) =>
            args match {
              case Nil => uv.hrTime.asInstanceOf[Long]
//            val h = uv.hrTime.asInstanceOf[Long]
//
//            ((h << 56) |
//               ((h & 0xFF00) << 40) |
//               ((h & 0xFF0000) << 24) |
//               (h & 0xFF000000 << 8) |
//               (h >>> 56) |
//               ((h & 0xFF000000000000L) >>> 40) |
//               ((h & 0xFF0000000000L) >>> 24) |
//               ((h & 0xFF00000000L) >>> 8),
//             h,
//             System.currentTimeMillis)
              case _ => illegalArguments("hrTime", args, 0)
            }
      ),
      "upTime" -> (
          (args: List[Any]) =>
            args match {
              case Nil =>
                val uptime = stackalloc[CDouble]

                bailOnError(uv.upTime(uptime))
                (!uptime).cast[Double]
              case _ => illegalArguments("upTime", args, 0)
            }
      ),
      "osHomedir" -> (
          (args: List[Any]) =>
            args match {
              case Nil =>
                val buffer = stackalloc[CChar](BUF_SIZE)
                val size   = stackalloc[CSize]

                !size = BUF_SIZE
                bailOnError(uv.osHomedir(buffer, size))
                fromCString(buffer.cast[CString])
              case _ => illegalArguments("osHomedir", args, 0)
            }
      ),
      "osGetEnv" -> (
          (args: List[Any]) =>
            args match {
              case List(name: String) =>
                val buffer = stackalloc[CChar](BUF_SIZE)
                val size   = stackalloc[CSize]
                val error  = stackalloc[CChar](BUF_SIZE)

                Zone { implicit z =>
                  !size = BUF_SIZE

                  val code = uv.osGetEnv(toCString(name), buffer, size)

                  if (code < 0)
                    if (fromCString(uv.errNamer(code, error, BUF_SIZE)) == "ENOENT")
                      None
                    else
                      bailOnError(code)
                  else
                    Some(fromCString(buffer.cast[CString]))
                }
              case _ => illegalArguments("osGetEnv", args, 1)
            }
      )
    )
}
