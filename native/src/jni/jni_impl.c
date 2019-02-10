#include "jni/jni_decl.h"

#include "exception.h"
#include "logger.h"
#include "marshal.h"
#include "struct/arrays.h"
#include "wat.h"

#include <jni.h>

#include <stdlib.h>
#include <string.h>

static JNIEnv *jenv;

void setup_jni_reference(JNIEnv *env) {
    setup_uuid_reference(env);
    setup_logger_reference((void**) env);
    setup_block_reference(env);
    setup_chunk_reference(env);
    setup_entity_reference(env);
    setup_location_reference(env);
    setup_player_reference(env);
    setup_vector_reference(env);
    setup_world_reference(env);
    setup_exception_reference(env);
}

void delete_jni_reference() {
    delete_uuid_reference();
    delete_logger_reference();
    delete_block_reference();
    delete_chunk_reference();
    delete_entity_reference();
    delete_location_reference();
    delete_player_reference();
    delete_vector_reference();
    delete_world_reference();
    delete_exception_reference();
}

unsigned int load_string_array_data(struct string_array **dest, jobject obj) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (obj == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    *dest = WALLOC(struct string_array, 1);
    if (*dest == NULL) {
        return MARSHAL_ALLOC_FAILURE;
    }

    const jsize length = (*jenv)->GetArrayLength(jenv, obj);

    (*dest)->array = WALLOC(struct native_name *, length);
    if ((*dest)->array == NULL) {
        WAT_FREE(*dest);
        *dest = NULL;
        return MARSHAL_ALLOC_FAILURE;
    }

    (*dest)->alloc = length;
    (*dest)->length = length;

    unsigned int status = MARSHAL_SUCCESS;
    for (int i = 0; i < length; i++) {
        jobject element = (*jenv)->GetObjectArrayElement(jenv, obj, i);
        const char *text = (*jenv)->GetStringUTFChars(jenv, element, NULL);
        if (text == NULL) {
            status |= MARSHAL_FAILURE;
        }
        if (!wat_strcpy(&(*dest)->array[i], text)) {
            status |= MARSHAL_FAILURE;
        }
        (*jenv)->ReleaseStringUTFChars(jenv, element, text);
    }

    if (status != MARSHAL_SUCCESS) {
        delete_string_array(dest);
    }

    return status;
}

unsigned int create_string_array_java(jobject *dest, struct string_array *src) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (src == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    const int length = src->length;

    jclass string_class = (*jenv)->FindClass(jenv, "java/lang/String");
    *dest = (*jenv)->NewObjectArray(jenv, length, string_class, NULL);
    (*jenv)->DeleteLocalRef(jenv, string_class);

    if ((*jenv)->ExceptionCheck(jenv)) {
        if (*dest == NULL) {
            (*jenv)->DeleteLocalRef(jenv, *dest);
            return MARSHAL_THROWN_EX;
        }
    }

    if (*dest == NULL) {
        return MARSHAL_ALLOC_FAILURE;
    }

    unsigned int status = MARSHAL_SUCCESS;
    for (int i = 0; i < length; i++) {
        if (src->array[i] == NULL) {
            (*jenv)->SetObjectArrayElement(jenv, *dest, i, NULL);
        } else {
            jstring element = (*jenv)->NewStringUTF(jenv, src->array[i]);
            if ((*jenv)->ExceptionCheck(jenv)) {
                status |= MARSHAL_THROWN_EX;
            } else {
                (*jenv)->SetObjectArrayElement(jenv, *dest, i, element);
            }
            (*jenv)->DeleteLocalRef(jenv, element);
        }
    }

    if (status != MARSHAL_SUCCESS) {
        (*jenv)->DeleteLocalRef(jenv, *dest);
        *dest = NULL;
    }

    return status;
}
