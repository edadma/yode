package xyz.hyperreal.yode

//import xyz.hyperreal.yode.uv.TcpHandle

import scala.scalanative.native._
//import scala.scalanative.posix.netinet.in.sockaddr_in

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets

import xyz.hyperreal.yola.{Scope, YolaInterpreter, YolaParser}

object Main extends App {

  case class Options(file: Option[Path] = None)

  private val parser = new scopt.OptionParser[Options]("yode") {
    head("yode", "v0.1.0")
    version("version").text("prints the version").abbr("v")
    help("help").text("prints this usage text").abbr("h")
    arg[String]("<file>...").optional
      .validate(
        f =>
          if (!Files.exists(Paths.get(f)))
            failure(s"file '$f' not found")
          else if (!Files.isRegularFile(Paths.get(f)))
            failure(s"'$f' not a file")
          else if (!Files.isReadable(Paths.get(f)))
            failure(s"file '$f' unreadable")
          else
          success)
      .action((f, c) => c.copy(file = Some(Paths.get(f))))
      .text("loads and executes program from <file>")
  }

  parser.parse(args, Options()) match {
    case Some(options) =>
      options.file match {
        case None => println("no REPL yet")
        case Some(path) =>
          val program           = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
          val parser            = new YolaParser
          val ast               = parser.parseFromString(program, parser.source)
          implicit val toplevel = new Scope

          val loop = uv.defaultLoop()

          toplevel.vars("console") = Map("log" -> ((args: List[Any]) => println(args mkString ", ")))
          toplevel.vars("setInterval") = (args: List[Any]) => {
            val timerHandle   = stdlib.malloc(sizeof[uv.TimerHandle]).cast[Ptr[uv.TimerHandle]]
            val timerCallback = CFunctionPtr.fromFunction1(args.head.asInstanceOf[Ptr[uv.TimerHandle] => Unit])

            uv.timerInit(loop, timerHandle)
            uv.timerStart(
              timerHandle,
              timerCallback,
              args.tail.head.asInstanceOf[CLong],
              args.tail.head.asInstanceOf[CLong]
            )

            timerHandle
          }

          YolaInterpreter(ast)
      }
    case None => System.exit(1)
  }

}

/*

  case class ServerConfig(host: String = "127.0.0.1", port: Int = 7000)

  private val parser = new scopt.OptionParser[ServerConfig]("hello-scala-native") {
    opt[String]('h', "host").action((x, c) => c.copy(host = x)).text("The host on which to bind")

    opt[Int]('p', "port").action((x, c) => c.copy(port = x)).text("The port on which to bind")
  }

  private val DefaultRunMode = 0

  parser.parse(args, ServerConfig()) match {
    case Some(serverConfig) => runServer(serverConfig)
    case None               => System.exit(1)
  }

  private def runServer(config: ServerConfig): Unit = Zone { implicit z =>
    val socketAddress = alloc[sockaddr_in]
    uv.ip4Addr(toCString(config.host), config.port, socketAddress)

    val loop = uv.defaultLoop()
    println("Created event loop")

    val tcpHandle = alloc[TcpHandle]
    bailOnError(uv.tcpInit(loop, tcpHandle))
    println("Initialised TCP handle")

    bailOnError(uv.tcpBind(tcpHandle, socketAddress, UInt.MinValue))
    println(s"Bound server to ${config.host}:${config.port}")

    bailOnError(uv.listen(tcpHandle, connectionBacklog = 128, Server.onTcpConnection))
    println("Started listening")

    bailOnError(uv.run(loop, DefaultRunMode))
    println("Started event loop")
  }

 */
