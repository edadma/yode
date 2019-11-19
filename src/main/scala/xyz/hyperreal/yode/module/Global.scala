package xyz.hyperreal.yode.module

import xyz.hyperreal.yode._
import xyz.hyperreal.yola

import scala.scalanative.native._

object Global {

  val prt = (args: List[Any]) => println(args map yola.display mkString ", ")

  val exports =
    Map(
      "println" -> prt,
      "console" -> Map("log" -> prt)
    )

}
