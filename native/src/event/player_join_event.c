#include "player_join_event.h"

#include "wat.h"

#include <stdlib.h>

static player_join_callback *callbacks;
static int callback_count = 0;
static int callback_alloc = 0;

static void resize() {
    callback_alloc *= 2;
    player_join_callback *new_callbacks = WALLOC(player_join_callback, callback_alloc);
    for (int i = 0; i < callback_count; i++) {
        new_callbacks[i] = callbacks[i];
    }

    WAT_FREE(callbacks);

    callbacks = new_callbacks;
}

WATEXPORT void register_player_join_callback(player_join_callback callback) {
    if (callback_count == callback_alloc) {
        resize();
    }

    callbacks[callback_count++] = callback;
}

void on_player_join(struct player_join_event *event) {
    for (int i = 0; i < callback_count; i++) {
        callbacks[i](event);
    }
}

void setup_player_join_event() {
    callbacks = WALLOC(player_join_callback, 10);
}

void delete_player_join_event() {
    WAT_FREE(callbacks);
}
