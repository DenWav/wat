#include "exception.h"
#include "marshal.h"

static JNIEnv *jenv;

static jclass illegal_state_class;

static jclass wat_exception_class;

void throw_illegal_state(const char *message) {
    (*jenv)->ThrowNew(jenv, illegal_state_class, message);
}

void throw_wat_exception(const char *message) {
    (*jenv)->ThrowNew(jenv, wat_exception_class, message);
}

void throw_generic(const char *class, const char *message) {
    jclass exc = (*jenv)->FindClass(jenv, class);
    (*jenv)->ThrowNew(jenv, exc, message);
    (*jenv)->DeleteLocalRef(jenv, exc);
}

void setup_exception_reference(JNIEnv *env) {
    jenv = env;
    jclass local_illegal_state_class = (*env)->FindClass(env, "java/lang/IllegalStateException");
    illegal_state_class = (*env)->NewGlobalRef(env, local_illegal_state_class);
    (*env)->DeleteLocalRef(env, local_illegal_state_class);

    jclass local_wat_exception_class = (*env)->FindClass(env, "com/demonwav/wat/exception/WatException");
    wat_exception_class = (*env)->NewGlobalRef(env, local_wat_exception_class);
    (*env)->DeleteLocalRef(env, local_wat_exception_class);
}

void delete_exception_reference() {
    (*jenv)->DeleteGlobalRef(jenv, illegal_state_class);
}

bool throw_from_marshal_status(int status) {
    if ((status & MARSHAL_THROWN_EX) != 0) {
        return true;
    }

    if ((status & MARSHAL_FAILURE) != 0) {
        throw_wat_exception("Generic failure occurred while marshalling data");
        return true;
    } else if ((status & MARSHAL_FAILURE) != 0) {
        throw_wat_exception("Failed to allocate memory while marshalling data");
        return true;
    }

    return false;
}
