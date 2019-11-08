#include "uv.h"
#include "uv/unix.h"
#include <stdio.h>

struct uv_timer_private_s {
  uv_timer_cb timer_cb;
  void* heap_node[3];
  uint64_t timeout;
  uint64_t repeat;
  uint64_t start_id;
//  UV_TIMER_PRIVATE_FIELDS
};

typedef struct uv_handle_s uv_handle_t;
typedef struct uv_timer_private_s uv_timer_private_t;

int main(int argc, char **argv) {

  printf("Size of uv_timer_t = %lu\n", sizeof(uv_timer_t));
  printf("Size of uv_timer_private_t = %lu\n", sizeof(uv_timer_private_t));
  printf("Size of uv_handle_t = %lu\n", sizeof(uv_handle_t));

}
