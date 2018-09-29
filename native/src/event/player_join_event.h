#ifndef WAT_PLAYER_JOIN_EVENT_H
#define WAT_PLAYER_JOIN_EVENT_H

#include "wat.h"

WAT_EXPORT_START
struct player_join_event {
    struct player *player;
    const char *join_message;
};

typedef void (WATCALL *player_join_callback)(struct player_join_event *event);

WATEXPORT void WATCALL register_player_join_callback(player_join_callback callback);
WAT_EXPORT_END

void setup_player_join_event();

void delete_player_join_event();

void on_player_join(struct player_join_event *event);

#endif // WAT_PLAYER_JOIN_EVENT_H
