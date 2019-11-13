package xyz.hyperreal.yode

import scala.scalanative.native.Nat._
import scala.scalanative.native._
import scala.scalanative.posix.netinet.in.sockaddr_in

@link("uv")
@extern
object uv {

  /*
  event loop
   */
  type Loop = Ptr[Byte]

  @name("uv_default_loop")
  def defaultLoop(): Ptr[Loop] = extern

  @name("uv_run")
  def run(loop: Ptr[Loop], runMode: CInt): CInt = extern

  @name("uv_loop_size")
  def loopSize(): CSize = extern

  type Buffer = CStruct2[
    CString, // char* base;
    CSize // size_t len;
  ]

  /*
  handles
   */
  type Handle = Long

  @name("uv_handle_size")
  def handleSize(h_type: Int): CSize = extern

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

  @name("uv_tcp_init")
  def tcpInit(loop: Ptr[Loop], handle: Ptr[TcpHandle]): CInt = extern

  @name("uv_tcp_bind")
  def tcpBind(handle: Ptr[TcpHandle], socketAddress: Ptr[sockaddr_in], flags: CUnsignedInt): CInt = extern

  @name("uv_listen")
  def listen(handle: Ptr[TcpHandle],
             connectionBacklog: CInt,
             onTcpConnection: CFunctionPtr2[Ptr[TcpHandle], CInt, Unit]): CInt = extern

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

  type TimerHandle = Long

  type TimerCallback = CFunctionPtr1[Ptr[TimerHandle], Unit]

  @name("uv_timer_init")
  def timerInit(loop: Ptr[Loop], handle: Ptr[TimerHandle]): CInt = extern

  @name("uv_timer_start")
  def timerStart(handle: Ptr[TimerHandle], cb: TimerCallback, timeout: Long, repeat: Long): CInt = extern

  @name("uv_timer_stop")
  def timerStop(handle: Ptr[TimerHandle]): CInt = extern

  /*
  idle handle
   */

  type IdleHandle = Long

  type IdleCallback = CFunctionPtr1[Ptr[IdleHandle], Unit]

  @name("uv_idle_init")
  def idleInit(loop: Ptr[Loop], handle: Ptr[IdleHandle]): CInt = extern

  @name("uv_idle_start")
  def idleStart(handle: Ptr[IdleHandle], cb: IdleCallback): CInt = extern

  @name("uv_idle_stop")
  def idleStop(handle: Ptr[IdleHandle]): CInt = extern

}

object uvConstants {

  val RUN_DEFAULT = 0
  val RUN_ONCE    = 1
  val RUN_NOWAIT  = 2

  val ASYNC_HANDLE    = 1
  val CHECK_HANDLE    = 2
  val FS_EVENT_HANDLE = 3
  val FS_POLL_HANDLE  = 4
  val IDLE_HANDLE     = 6
  val PIPE_HANDLE     = 7
  val POLL_HANDLE     = 8
  val PREPARE_HANDLE  = 9
  val PROCESS_HANDLE  = 10
  val STREAM_HANDLE   = 11
  val TCP_HANDLE      = 12
  val TIMER_HANDLE    = 13
  val TTY_HANDLE      = 14
  val UDP_HANDLE      = 15
  val SIGNAL_HANDLE   = 16
  val FILE_HANDLE     = 17

}
