package xyz.hyperreal.yode

import scala.collection.mutable
import scala.scalanative.native._
//import scala.scalanative.posix.netinet.in.sockaddr_in

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets

import xyz.hyperreal.yola

object Main extends App {

  case class Options(file: Option[Path] = None, eval: Option[String] = None, print: Option[String] = None)

  private val parser = new scopt.OptionParser[Options]("yode") {
    head("Yode", "v0.1.0")
    opt[String]('e', "eval")
      .text("execute program <script>")
      .valueName("<script>")
      .action((f, c) => c.copy(eval = Some(f)))
    help("help").text("print this usage text").abbr("h")
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
          success)
      .action((f, c) => c.copy(file = Some(Paths.get(f))))
      .text("load and execute program from <file>")
  }

  var handles           = new mutable.HashMap[Long, yola.FunctionAST]
  implicit val toplevel = new yola.Scope(null)

  def uvCallback(handle: Ptr[uv.TimerHandle]): Unit = {
    yola.Interpreter.call(null, handles(handle.cast[Long]), null, List(HandleWrapper(handle)))
  }

  val uvCallbackPtr = CFunctionPtr.fromFunction1(uvCallback)
  val loop          = uv.defaultLoop()

  toplevel.vars("console") = Map("log" -> ((args: List[Any]) => println(args map yola.display mkString ", ")))
  toplevel.vars("setInterval") = (args: List[Any]) => {
    val timerHandle = stdlib.malloc(uv.handleSize(uvConstants.TIMER_HANDLE)).cast[Ptr[uv.TimerHandle]]

    uv.timerInit(loop, timerHandle)
    handles(timerHandle.cast[Long]) = args.head.asInstanceOf[yola.FunctionAST]
    uv.timerStart(
      timerHandle,
      uvCallbackPtr,
      args.tail.head.asInstanceOf[Int],
      args.tail.head.asInstanceOf[Int]
    )

    HandleWrapper(timerHandle)
  }
  toplevel.vars("clearInterval") = (args: List[Any]) =>
    args.head match {
      case HandleWrapper(handle) =>
        uv.timerStop(handle)
        stdlib.free(handle.cast[Ptr[Byte]])
  }
  toplevel.vars("setIdle") = (args: List[Any]) => {
    val idleHandle = stdlib.malloc(uv.handleSize(uvConstants.TIMER_HANDLE)).cast[Ptr[uv.IdleHandle]]

    uv.idleInit(loop, idleHandle)
    handles(idleHandle.cast[Long]) = args.head.asInstanceOf[yola.FunctionAST]
    uv.idleStart(idleHandle, uvCallbackPtr)

    HandleWrapper(idleHandle)
  }
  toplevel.vars("clearIdle") = (args: List[Any]) =>
    args.head match {
      case HandleWrapper(handle) =>
        uv.idleStop(handle)
        stdlib.free(handle.cast[Ptr[Byte]])
  }

  parser.parse(args, Options()) match {
    case Some(options) =>
      options match {
        case Options(None, None, None)         => println("no REPL yet")
        case Options(Some(path), None, None)   => run(new String(Files.readAllBytes(path), StandardCharsets.UTF_8))
        case Options(None, Some(script), None) => run(script)
        case Options(None, None, Some(script)) => println(s"${Console.YELLOW}${run(script)}${Console.RESET}")
        case _                                 => parser.showUsageAsError
      }
    case None => System.exit(1)
  }

  uv.run(loop, uvConstants.RUN_DEFAULT)

  def run(script: String) = {
    val parser = new yola.YParser
    yola.Interpreter(parser.parseFromString(script, parser.source))
  }

}

case class HandleWrapper(handle: Ptr[uv.Handle])
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
