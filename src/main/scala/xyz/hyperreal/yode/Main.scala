package xyz.hyperreal.yode

import scala.collection.mutable
import scala.scalanative.native._
//import scala.scalanative.posix.netinet.in.sockaddr_in

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets

import xyz.hyperreal.yola

object Main extends App {

  case class Options(
      dir: Option[Path] = None,
      module: Option[String] = None,
      file: Option[Path] = None,
      eval: Option[String] = None,
      print: Option[String] = None
  )

  private val parser = new scopt.OptionParser[Options]("yode") {
    head("Yode", VERSION)
    opt[String]('d', "directory")
      .text("set application directory")
      .valueName("<path>")
      .validate(
        p =>
          if (!Files.exists(Paths.get(p)))
            failure(s"directory '$p' not found")
          else if (!Files.isDirectory(Paths.get(p)))
            failure(s"'$p' not a directory")
          else if (!Files.isReadable(Paths.get(p)))
            failure(s"directory '$p' unreadable")
          else
            success
      )
      .action((p, c) => c.copy(dir = Some(Paths.get(p))))
    opt[String]('e', "eval")
      .text("execute program <script>")
      .valueName("<script>")
      .action((f, c) => c.copy(eval = Some(f)))
    help("help").text("print this usage text").abbr("h")
    opt[String]('m', "module")
      .text("application entry point is <module>")
      .valueName("<module>")
      .action((m, c) => c.copy(eval = Some(m)))
    opt[String]('p', "print")
      .text("execute program <script> and print result")
      .valueName("<script>")
      .action((f, c) => c.copy(print = Some(f)))
    version("version").text("print the version").abbr("v")
    arg[String]("<file>").optional
      .validate(
        f =>
          if (!Files.exists(Paths.get(f)))
            failure(s"file '$f' not found")
          else if (!Files.isRegularFile(Paths.get(f)))
            failure(s"'$f' not a file")
          else if (!Files.isReadable(Paths.get(f)))
            failure(s"file '$f' unreadable")
          else
            success
      )
      .action((f, c) => c.copy(file = Some(Paths.get(f))))
      .text("load and execute program from <file>")
  }

  for ((k, v) <- module.Global.exports)
    global.vars(k) = v

  global.vars("yode") = Map("util" -> module.Util.exports, "idle" -> module.Idle.exports)

  parser.parse(args, Options()) match {
    case Some(options) =>
      options match {
        case Options(None, None, None, None, None) => println("no REPL yet")
        case Options(None, None, Some(path), None, None) =>
          run(read(path))
        case Options(None, None, None, Some(script), None) => run(script)
        case Options(None, None, None, None, Some(script)) =>
          println(s"${Console.YELLOW}${run(script)}${Console.RESET}")
        case Options(None, Some(module), None, None, None)       => load(null, module)
        case Options(Some(path), Some(module), None, None, None) => load(path, module)
        case _                                                   => parser.showUsageAsError
      }
    case None => System.exit(1)
  }

  uv.run(loop, uvConstants.RUN_DEFAULT)

  def read(p: Path) = new String(Files.readAllBytes(p), StandardCharsets.UTF_8)

  def run(script: String) = {
    val parser = new yola.YParser

    interp(parser.parseFromString(script, parser.source))
  }

  def load(dir: Path, mod: String) = {
    def load(dir: Path) = {}

    load(dir)
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
