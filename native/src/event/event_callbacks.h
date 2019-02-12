#ifndef WAT_EVENT_CALLBACKS_H
#define WAT_EVENT_CALLBACKS_H

#include "wat.h"

#define EVENT_CALLBACK(event_name)                                                                              \
    static event_name##_callback *event_name##_callbacks;                                                       \
    static int event_name##_callback_count = 0;                                                                 \
    static int event_name##_callback_alloc = 0;                                                                 \
                                                                                                                \
    static void event_name##_resize() {                                                                         \
        event_name##_callback_alloc *= 2;                                                                       \
        event_name##_callback *new_callbacks = WALLOC(event_name##_callback, event_name##_callback_alloc);      \
        for (int i = 0; i < event_name##_callback_count; i++) {                                                 \
            new_callbacks[i] = event_name##_callbacks[i];                                                       \
        }                                                                                                       \
                                                                                                                \
        WAT_FREE(event_name##_callbacks);                                                                       \
                                                                                                                \
        event_name##_callbacks = new_callbacks;                                                                 \
    }                                                                                                           \
                                                                                                                \
    WATEXPORT void register_##event_name##_callback(event_name##_callback callback) {                           \
        if (event_name##_callback_count == event_name##_callback_alloc) {                                       \
            event_name##_resize();                                                                              \
        }                                                                                                       \
                                                                                                                \
        event_name##_callbacks[event_name##_callback_count++] = callback;                                       \
    }                                                                                                           \
                                                                                                                \
    void setup_##event_name##_event_callbacks() {                                                               \
        event_name##_callbacks = WALLOC(event_name##_callback, 10);                                             \
    }                                                                                                           \
                                                                                                                \
    void delete_##event_name##_event_callbacks() {                                                              \
        WAT_FREE(event_name##_callbacks);                                                                       \
    }                                                                                                           \
                                                                                                                \
    void on_##event_name(struct event_name##_event *event) {                                                    \
        for (int i = 0; i < event_name##_callback_count; i++) {                                                 \
            event_name##_callbacks[i](event);                                                                   \
        }                                                                                                       \
    }

#endif // WAT_EVENT_CALLBACKS_H
