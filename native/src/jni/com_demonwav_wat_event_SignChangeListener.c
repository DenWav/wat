#include "jni/com_demonwav_wat_event_SignChangeListener.h"

#include "event/sign_change_event.h"
#include "exception.h"
#include "jni/jni_decl.h"
#include "marshal.h"
#include "struct/sign_change_event.h"
#include "wat.h"

#include <stdlib.h>

JNIEXPORT jobject JNICALL Java_com_demonwav_wat_event_SignChangeListener_onSignChange0(JNIEnv *env, jclass class, jobject sign_change_event_obj) {
    struct sign_change_event *event;
    unsigned int status = load_sign_change_event_data(&event, sign_change_event_obj);
    if (throw_from_marshal_status(status)) {
        return NULL;
    }

    on_sign_change(event);

    jobject res;
    status = create_sign_change_event_java(&res, event);
    if (throw_from_marshal_status(status)) {
        return NULL;
    }

    return res;
}
