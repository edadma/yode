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

    while ({ line = io.StdIn.readLine("> "); line ne null }) {
      line.trim match {
        case "" =>
        case ".help" =>
          println(
            s"""
               |.help               print list of commands
              """.stripMargin
          )
        case _ => println(run(line, scope))
      }
    }
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
