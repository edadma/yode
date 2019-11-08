package xyz.hyperreal.yode

import scala.scalanative.native.Nat._
import scala.scalanative.native._
import scala.scalanative.posix.netinet.in.sockaddr_in

@link("uv")
@extern
object uv {

  class Loop // we don't care what the uv_loop_t type is, as we just need to pass around a pointer to it

  type Buffer = CStruct2[
    CString, // char* base;
    CSize // size_t len;
  ]

  type _100 = Digit[_1, Digit[_0, _0]]

  type TimerHandle = CStruct9[
    // handle fields
    Ptr[Unit], // void* data;
    Ptr[Loop], // uv_loop_t* loop;
    CInt, // uv_handle_type type;
    CFunctionPtr1[Ptr[Unit], Unit], // uv_close_cb close_cb; (Ptr[Unit] is actually Ptr[TcpHandle] but we can't have recursive types)
    CArray[Ptr[Unit], _2], // void* handle_queue[2];
    CArray[Ptr[Unit], _4], // union { int fd; void* reserved[4]; } u;
    Ptr[Unit], // uv_handle_t* next_closing; (Ptr[Unit] is actually Ptr[TcpHandle])
    UInt,      // unsigned int flags;
    // timer private fields
    CArray[Byte, Digit[_5, _6]] // add enough padding to cover a bunch of private fields
  ]

  type TimerCallback = CFunctionPtr1[Ptr[TimerHandle], Unit]

  type TcpHandle = CStruct12[
    Ptr[Unit], // void* data;
    Ptr[Loop], // uv_loop_t* loop;
    CInt, // uv_handle_type type;
    CFunctionPtr1[Ptr[Unit], Unit], // uv_close_cb close_cb; (Ptr[Unit] is actually Ptr[TcpHandle] but we can't have recursive types)
    CArray[Ptr[Unit], _2], // void* handle_queue[2];
    CArray[Ptr[Unit], _4], // union { int fd; void* reserved[4]; } u;
    Ptr[Unit], // uv_handle_t* next_closing; (Ptr[Unit] is actually Ptr[TcpHandle])
    UInt, // unsigned int flags;
    CSize, // size_t write_queue_size;
    CFunctionPtr3[Ptr[Unit], CSize, Ptr[Unit], Unit], // uv_alloc_cb alloc_cb; (Ptr[Unit] is actually Ptr[TcpHandle])
    CFunctionPtr3[Ptr[Unit], CSSize, Ptr[Unit], Unit], // uv_read_cb read_cb; (Ptr[Unit] is actually Ptr[TcpHandle])
    CArray[Byte, _100] // add enough padding to cover a bunch of private fields
  ]

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
  def timerStart(handle: Ptr[TimerHandle], cb: TimerCallback, timeout: CLong, repeat: CLong): CInt = extern

}
