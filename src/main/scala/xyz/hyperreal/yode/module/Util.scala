package xyz.hyperreal.yode.module

import xyz.hyperreal.yode._

import scala.scalanative.native._

object Util {

  val exports =
    Map(
      "hrTime" -> ((args: List[Any]) =>
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
        }),
      "upTime" -> ((args: List[Any]) =>
        args match {
          case Nil =>
            val uptime = stackalloc[CDouble]

            bailOnError(uv.upTime(uptime))
            (!uptime).cast[Double]
          case _ => illegalArguments("upTime", args, 0)
        })
    )

}
