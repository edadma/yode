package xyz.hyperreal.yode

import xyz.hyperreal.yola.Scope

import scala.scalanative.native.{fromCString, stackalloc, CString}

object Modules {

  def importModule(module: List[String], name: String, rename: Option[String], scope: Scope): Unit = {
    def find(ms: List[String], map: Map[String, Any]): Unit =
      ms match {
        case Nil =>
          val mod = map.asInstanceOf[Map[String, List[Any] => Any]]

          mod get name match {
            case None => printError(s"member '$name' not found")
            case Some(m) =>
              val mem =
                rename match {
                  case None          => name
                  case Some(newname) => newname
                }

              scope(mem) = m
          }
        case h :: t =>
          map get h match {
            case None    => printError(s"module '$h' not found")
            case Some(m) => find(t, m.asInstanceOf[Map[String, Any]])
          }
      }

    find(module, builtin)
  }

  val builtin =
    Map(
      "yode" ->
        Map(
          "os" ->
            Map(
              "osUname" -> ((args: List[Any]) =>
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
        )
    )

}
