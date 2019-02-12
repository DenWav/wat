#ifndef WAT_SIGN_CHANGE_EVENT_H
#define WAT_SIGN_CHANGE_EVENT_H

#include "struct/sign_change_event.h"
#include "wat.h"

WAT_EXPORT_START
typedef void (WATCALL *sign_change_callback)(struct sign_change_event *event);

WATEXPORT void WATCALL register_sign_change_callback(sign_change_callback callback);
WAT_EXPORT_END

void setup_sign_change_event_callbacks();

void delete_sign_change_event_callbacks();

void on_sign_change(struct sign_change_event *event);

#endif //WAT_SIGN_CHANGE_EVENT_H
