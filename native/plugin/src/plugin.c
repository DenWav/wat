#include <wat.h>

void WATCALL join_callback(struct player_join_event *event) {
    bukkit_log_info("Join message: %s", event->join_message);
}

WATEXPORT void WATCALL WAT_INIT() {
    bukkit_log_info("Init plugin");
    register_player_join_callback(join_callback);
}

WATEXPORT void WATCALL WAT_CLOSE() {
    bukkit_log_info("Close plugin");
}
