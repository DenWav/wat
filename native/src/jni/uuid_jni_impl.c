#include "jni/jni_decl.h"

#include "marshal.h"
#include "uuid.h"
#include "wat.h"

#include <jni.h>

#include <stdlib.h>

static JNIEnv *jenv;

// java.util.UUID
static jclass uuid_java_class;

static jmethodID uuid_java_const;

static jmethodID uuid_java_get_most_significant_bits;
static jmethodID uuid_java_get_least_significant_bits;

void setup_uuid_reference(JNIEnv *env) {
    jenv = env;

    // java.util.UUID
    jclass local_uuid_java_class = (*env)->FindClass(env, "java/util/UUID");
    uuid_java_class = (*env)->NewGlobalRef(env, local_uuid_java_class);
    (*env)->DeleteLocalRef(env, local_uuid_java_class);

    uuid_java_const = (*env)->GetMethodID(env, uuid_java_class, "<init>", "(JJ)V");

    uuid_java_get_most_significant_bits = (*env)->GetMethodID(env, uuid_java_class, "getMostSignificantBits", "()J");
    uuid_java_get_least_significant_bits = (*env)->GetMethodID(env, uuid_java_class, "getLeastSignificantBits", "()J");
}

void delete_uuid_reference() {
    (*jenv)->DeleteGlobalRef(jenv, uuid_java_class);
}

static inline void to_array(int8_t *bytes, const int64_t data, const int index) {
    for (int i = 0; i < 8; i++) {
        bytes[index + i] = (int8_t)((data >> ((7 - i) * 8)) & 0xFF);
    }
}

static inline int64_t from_array(const int8_t *bytes, const int index) {
    int64_t res = 0;
    for (int i = 0; i < 8; i++) {
        res |= (((int64_t) bytes[index + i]) & 0xFF) << ((7 - i) * 8);
    }
    return res;
}

unsigned int load_uuid_data(uuid **dest, jobject obj) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (obj == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    *dest = WALLOC(uuid, 1);
    if (*dest == NULL) {
        return MARSHAL_ALLOC_FAILURE;
    }

    int status = MARSHAL_SUCCESS;

    int64_t data;

    status |= get_long(&data, jenv, obj, uuid_java_get_most_significant_bits);
    to_array((*dest)->bytes, data, 0);

    status |= get_long(&data, jenv, obj, uuid_java_get_least_significant_bits);
    to_array((*dest)->bytes, data, 8);

    if (status != MARSHAL_SUCCESS) {
        WAT_FREE(dest);
    }

    return status;
}

unsigned int create_uuid_java(jobject *dest, uuid *src) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (src == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    int64_t most_sig = from_array(src->bytes, 0);
    int64_t least_sig = from_array(src->bytes, 8);

    int status = MARSHAL_SUCCESS;

    *dest = (*jenv)->NewObject(jenv, uuid_java_class, uuid_java_const, most_sig, least_sig);

    if ((*jenv)->ExceptionCheck(jenv)) {
        status |= MARSHAL_THROWN_EX;
    }

    if (*dest == NULL) {
        status |= MARSHAL_ALLOC_FAILURE;
    }

    return status;
}
