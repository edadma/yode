package xyz.hyperreal.yode

//import scala.scalanative.native._

import xyz.hyperreal.yola

object REPL {

  def apply() = {
    val scope        = new yola.Scope(global)
    var line: String = null

    println(
      s"""
         |Yode v$VERSION REPL
         |
         |Type .help to see a list of commands.
         |Type Ctrl-C to exit.
        """.stripMargin
    )

    while ({ line = io.StdIn.readLine("> "); line ne null }) try {
      {
        line.trim split "\\s+" toList match {
          case List("") | Nil =>
          case (".declarations" | ".de") :: _ =>
            println(scope.vars map { case (k, v) => s"$k = $v" } mkString "\n")
          case (".delete" | ".d") :: decl :: _ =>
            if (scope.vars contains decl)
              scope.vars -= decl
            else
              println(s"declaration '$decl' not found")
          case (".help" | ".h") :: _ =>
            println(
              s"""
                 |.declarations/.de     print current declarations
                 |.delete/.d <decl>     delete declaration <decl>
                 |.help/.h              print list of commands
              """.stripMargin
            )
          case _ => println(run(line, scope))
        }
      }
    } catch {
      case e: Exception => println(e.getMessage)
    }

    println
  }

  def run(script: String, scope: yola.Scope) = {
    val parser = new yola.YParser

    interp(parser.parseFromString(script, parser.source))(scope)
  }
}

//Zone { implicit z =>
// val ttyIn  = alloc[uv.TTYHandle](uv.handleSize(uvConst.TTY_HANDLE))
// val ttyOut = alloc[uv.TTYHandle](uv.handleSize(uvConst.TTY_HANDLE))

// uv.ttyInit(loop, ttyOut, uvConst.STDOUT_FILENO, 0)
// uv.ttySetMode(ttyOut, uvConst.TTY_MODE_NORMAL)
