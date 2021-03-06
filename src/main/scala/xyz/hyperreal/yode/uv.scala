package xyz.hyperreal.yode

import scala.scalanative.native.Nat._
import scala.scalanative.native._
import scala.scalanative.posix.netinet.in.sockaddr_in
import java.io.File

@link("uv")
@extern
object uv {
  /*
  uv_loop_t
   */
  type Loop = Ptr[Byte]

  @name("uv_default_loop")
  def defaultLoop(): Ptr[Loop] = extern

  @name("uv_run")
  def run(loop: Ptr[Loop], runMode: CInt): CInt = extern

  @name("uv_loop_size")
  def loopSize(): CSize = extern

  /*
  uv_tty_t
   */

  type TTYHandle = Ptr[Byte]

  @name("uv_tty_init")
  def ttyInit(loop: Ptr[Loop], handle: Ptr[TTYHandle], file: uv.FileHandle, readable: CInt): CInt = extern

  @name("uv_tty_set_mode")
  def ttySetMode(handle: Ptr[TTYHandle], mode: CInt): CInt = extern

  @name("uv_tty_reset_mode")
  def ttyResetMode(): CInt = extern

  /*
  uv_tcp_t
   */

  type TcpHandle = Ptr[Byte]

  @name("uv_tcp_init")
  def tcpInit(loop: Ptr[Loop], handle: Ptr[TcpHandle]): CInt = extern

  @name("uv_tcp_bind")
  def tcpBind(handle: Ptr[TcpHandle], socketAddress: Ptr[sockaddr_in], flags: CUnsignedInt): CInt = extern

  /*
  uv_stream_t
   */

  type StreamHandle = Ptr[Byte]

  type Write = CStruct12[
    Ptr[Unit],                            // void* data;
    CInt,                                 // uv_req_type type;
    CArray[Ptr[Unit], _6],                // void* reserved[6];
    CFunctionPtr2[Ptr[Unit], CInt, Unit], // uv_write_cb cb; (Ptr[Unit] is actually Ptr[Write])
    Ptr[TcpHandle],                       // uv_stream_t* send_handle;
    Ptr[TcpHandle],                       // uv_stream_t* handle;
    CArray[Ptr[Unit], _2],                // void* queue[2];
    UInt,                                 // unsigned int write_index;
    Ptr[Buffer],                          // uv_buf_t* bufs;
    UInt,                                 // unsigned int nbufs;
    CInt,                                 // int error;
    CArray[Buffer, _4]                    // uv_buf_t bufsml[4];
  ]

  @name("uv_write")
  def write(
      req: Ptr[Write],
      clientHandle: Ptr[StreamHandle],
      buffers: Ptr[Buffer],
      numberOfBuffers: UInt,
      onWritten: CFunctionPtr2[Ptr[Write], CInt, Unit]
  ): CInt = extern

  @name("uv_listen")
  def listen(
      handle: Ptr[StreamHandle],
      connectionBacklog: CInt,
      onTcpConnection: CFunctionPtr2[Ptr[StreamHandle], CInt, Unit]
  ): CInt = extern

  @name("uv_accept")
  def accept(handle: Ptr[StreamHandle], clientHandle: Ptr[StreamHandle]): CInt = extern

  @name("uv_read_start")
  def readStart(
      stream: Ptr[StreamHandle],
      allocateBuffer: CFunctionPtr3[Ptr[StreamHandle], CSize, Ptr[Buffer], Unit],
      onRead: CFunctionPtr3[Ptr[StreamHandle], CSSize, Ptr[Buffer], Unit]
  ): CInt = extern

  @name("uv_read_stop")
  def readStop(stream: Ptr[StreamHandle]): CInt = extern

  /*
  uv_handle_t
   */

  type Handle = Long

  @name("uv_handle_size")
  def handleSize(h_type: Int): CSize = extern

  @name("uv_close")
  def close(clientHandle: Ptr[Handle], callback: CFunctionPtr1[Ptr[Handle], Unit]): CInt = extern

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
  uv_idle_t
   */

  type IdleHandle = Long

  type IdleCallback = CFunctionPtr1[Ptr[IdleHandle], Unit]

  @name("uv_idle_init")
  def idleInit(loop: Ptr[Loop], handle: Ptr[IdleHandle]): CInt = extern

  @name("uv_idle_start")
  def idleStart(handle: Ptr[IdleHandle], cb: IdleCallback): CInt = extern

  @name("uv_idle_stop")
  def idleStop(handle: Ptr[IdleHandle]): CInt = extern

  /*
  miscellaneous utilities
   */

  type Buffer = CStruct2[
    CString, // char* base;
    CSize    // size_t len;
  ]

  type FileHandle = CInt

  @name("uv_guess_handle")
  def guessHandle(file: FileHandle): CInt = extern

  @name("uv_ip4_addr")
  def ip4Addr(ip: CString, port: CInt, addr: Ptr[sockaddr_in]): CInt = extern

  @name("uv_hrtime")
  def hrTime(): CUnsignedLong = extern

  type _256 = Digit[_2, Digit[_5, _6]]

  type Utsname = CStruct4[CArray[UByte, _256], CArray[UByte, _256], CArray[UByte, _256], CArray[UByte, _256]]

  @name("uv_os_uname")
  def osUname(utsname: Ptr[Utsname]): CInt = extern

  @name("uv_uptime")
  def upTime(uptime: Ptr[CDouble]): CInt = extern

  @name("uv_os_homedir")
  def osHomedir(buffer: Ptr[CChar], size: Ptr[CSize]): CInt = extern

  @name("uv_os_getenv")
  def osGetEnv(name: Ptr[CChar], buffer: Ptr[CChar], size: Ptr[CSize]): CInt = extern

  /*
  error handling
   */

  @name("uv_err_name")
  def errName(errorCode: CInt): CString = extern

  @name("uv_err_name_r")
  def errNamer(errorCode: CInt, buf: Ptr[CChar], buflen: CSize): CString = extern

  @name("uv_strerror")
  def strError(errorCode: CInt): CString = extern
}

object uvConst {
  val RUN_DEFAULT = 0
  val RUN_ONCE    = 1
  val RUN_NOWAIT  = 2

  val TTY_MODE_NORMAL = 0
  val TTY_MODE_RAW    = 1
  val TTY_MODE_IO     = 2

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

  val STDIN_FILENO  = 0
  val STDOUT_FILENO = 1
  val STDERR_FILENO = 2
}
