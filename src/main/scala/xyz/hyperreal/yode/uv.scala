package xyz.hyperreal.yode

import scala.scalanative.native.Nat._
import scala.scalanative.native._
import scala.scalanative.posix.netinet.in.sockaddr_in

@link("uv")
@extern
object uv {

  val PIPE_HANDLE    = 7
  val POLL_HANDLE    = 8
  val PREPARE_HANDLE = 9
  val PROCESS_HANDLE = 10
  val TCP_HANDLE     = 12
  val TIMER_HANDLE   = 13
  val TTY_HANDLE     = 14
  val UDP_HANDLE     = 15

  type Loop = Ptr[Byte]

  type Buffer = CStruct2[
    CString, // char* base;
    CSize // size_t len;
  ]

  type TimerHandle = Ptr[Byte]

  type TimerCallback = CFunctionPtr1[Ptr[TimerHandle], Unit]

  type TcpHandle = Ptr[Byte]

  type Write = CStruct12[
    Ptr[Unit], // void* data;
    CInt, // uv_req_type type;
    CArray[Ptr[Unit], _6], // void* reserved[6];
    CFunctionPtr2[Ptr[Unit], CInt, Unit], // uv_write_cb cb; (Ptr[Unit] is actually Ptr[Write])
    Ptr[TcpHandle], // uv_stream_t* send_handle;
    Ptr[TcpHandle], // uv_stream_t* handle;
    CArray[Ptr[Unit], _2], // void* queue[2];
    UInt, // unsigned int write_index;
    Ptr[Buffer], // uv_buf_t* bufs;
    UInt, // unsigned int nbufs;
    CInt, // int error;
    CArray[Buffer, _4] // uv_buf_t bufsml[4];
  ]

  @name("uv_ip4_addr")
  def ip4Addr(ip: CString, port: CInt, addr: Ptr[sockaddr_in]): CInt = extern

  @name("uv_default_loop")
  def defaultLoop(): Ptr[Loop] = extern

  @name("uv_loop_size")
  def loopSize(): CSize = extern

  @name("uv_handle_size")
  def handleSize(h_type: Int): CSize = extern

  @name("uv_tcp_init")
  def tcpInit(loop: Ptr[Loop], handle: Ptr[TcpHandle]): CInt = extern

  @name("uv_tcp_bind")
  def tcpBind(handle: Ptr[TcpHandle], socketAddress: Ptr[sockaddr_in], flags: CUnsignedInt): CInt = extern

  @name("uv_listen")
  def listen(handle: Ptr[TcpHandle],
             connectionBacklog: CInt,
             onTcpConnection: CFunctionPtr2[Ptr[TcpHandle], CInt, Unit]): CInt = extern

  @name("uv_run")
  def run(loop: Ptr[Loop], runMode: CInt): CInt = extern

  @name("uv_accept")
  def accept(handle: Ptr[TcpHandle], clientHandle: Ptr[TcpHandle]): CInt = extern

  @name("uv_read_start")
  def readStart(clientHandle: Ptr[TcpHandle],
                allocateBuffer: CFunctionPtr3[Ptr[TcpHandle], CSize, Ptr[Buffer], Unit],
                onRead: CFunctionPtr3[Ptr[TcpHandle], CSSize, Ptr[Buffer], Unit]): CInt = extern

  @name("uv_write")
  def write(req: Ptr[Write],
            clientHandle: Ptr[TcpHandle],
            buffers: Ptr[Buffer],
            numberOfBuffers: UInt,
            onWritten: CFunctionPtr2[Ptr[Write], CInt, Unit]): CInt = extern

  @name("uv_close")
  def close(clientHandle: Ptr[TcpHandle], callback: CFunctionPtr1[Ptr[TcpHandle], Unit]): CInt = extern

  @name("uv_err_name")
  def errName(errorCode: CInt): CString = extern

  /*
  uv_timer_t
   */

  @name("uv_timer_init")
  def timerInit(loop: Ptr[Loop], handle: Ptr[TimerHandle]): CInt = extern

  @name("uv_timer_start")
  def timerStart(handle: Ptr[TimerHandle], cb: TimerCallback, timeout: ULong, repeat: ULong): CInt = extern

}
