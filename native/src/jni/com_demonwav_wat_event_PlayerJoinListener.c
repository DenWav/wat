#include "jni/com_demonwav_wat_event_PlayerJoinListener.h"

#include "event/player_join_event.h"
#include "exception.h"
#include "jni/jni_decl.h"
#include "marshal.h"
#include "wat.h"

#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_demonwav_wat_event_PlayerJoinListener_onPlayerJoin0(JNIEnv *env, jclass class, jobject player_obj, jstring join_message) {
    struct player *player;
    int status = load_player_data(&player, player_obj);
    if (throw_from_marshal_status(status)) {
        return;
    }

    const char* text = (*env)->GetStringUTFChars(env, join_message, NULL);
    struct player_join_event *event = WALLOC(struct player_join_event, 1);
    event->player = player;
    event->join_message = text;

    on_player_join(event);
}
