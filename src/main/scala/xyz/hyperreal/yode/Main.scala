package xyz.hyperreal.yode

import xyz.hyperreal.yode.uv.TcpHandle

import scala.scalanative.native._
import scala.scalanative.posix.netinet.in.sockaddr_in

object Main extends App {

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

}
