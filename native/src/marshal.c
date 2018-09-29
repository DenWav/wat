#include "marshal.h"

#include "exception.h"
#include "struct/arrays.h"
#include "wat.h"

#include <stdbool.h>
#include <stdlib.h>
#include <string.h>

int get_java_string(char **dest, JNIEnv *env, jobject obj, jmethodID method) {
    int res = MARSHAL_SUCCESS;

    jobject temp = (*env)->CallObjectMethod(env, obj, method);
    const char *chars = NULL;
    if ((*env)->ExceptionCheck(env)) {
         res |= MARSHAL_THROWN_EX;
         goto cleanup;
    }

    chars = (*env)->GetStringUTFChars(env, temp, NULL);
    jsize length = (*env)->GetStringLength(env, temp) + 1; // + 1 for null char
	*dest = WALLOC(char, length);
	if (*dest != NULL) {
	    memcpy(*dest, chars, (size_t) length);
        (*dest)[length - 1] = '\0';
	} else {
	    res |= MARSHAL_ALLOC_FAILURE;
	}

    cleanup:
    (*env)->DeleteLocalRef(env, temp);
    if (chars != NULL) {
        (*env)->ReleaseStringUTFChars(env, temp, chars);
    }

    return res;
}

int set_java_string(JNIEnv *env, jobject obj, jmethodID method, const char *val) {
    int res = MARSHAL_SUCCESS;

    jstring temp = (*env)->NewStringUTF(env, val);
    (*env)->CallVoidMethod(env, obj, method, temp);

    if ((*env)->ExceptionCheck(env)) {
        res |= MARSHAL_THROWN_EX;
    }

    (*env)->DeleteLocalRef(env, temp);
    return res;
}

#define GET_PRIMITIVE(type, jvm_type, jni_type, jni_method)                         \
    int get_##jvm_type(type *dest, JNIEnv *env, jobject obj, jmethodID method) {    \
        jni_type res = (*env)->jni_method(env, obj, method);                        \
                                                                                    \
        if ((*env)->ExceptionCheck(env)) {                                          \
            return MARSHAL_THROWN_EX;                                               \
        }                                                                           \
                                                                                    \
        *dest = res;                                                                \
                                                                                    \
        return MARSHAL_SUCCESS;                                                     \
    }

GET_PRIMITIVE(bool, boolean, jboolean, CallBooleanMethod)
GET_PRIMITIVE(int8_t, byte, jbyte, CallByteMethod)
GET_PRIMITIVE(int16_t, short, jshort, CallShortMethod)
GET_PRIMITIVE(int32_t, int, jint, CallIntMethod)
GET_PRIMITIVE(int64_t, long, jlong, CallLongMethod)
GET_PRIMITIVE(float, float, jfloat, CallFloatMethod)
GET_PRIMITIVE(double, double, jdouble, CallDoubleMethod)

#define SET_PRIMITIVE(type, jvm_type, jni_type)                                     \
    int set_##jvm_type(JNIEnv *env, jobject obj, jmethodID method, type val) {      \
        jni_type param = val;                                                       \
        (*env)->CallVoidMethod(env, obj, method, param);                            \
                                                                                    \
        if ((*env)->ExceptionCheck(env)) {                                          \
            return MARSHAL_THROWN_EX;                                               \
        }                                                                           \
                                                                                    \
        return MARSHAL_SUCCESS;                                                     \
    }

SET_PRIMITIVE(bool, boolean, jboolean);
SET_PRIMITIVE(int8_t, byte, jbyte);
SET_PRIMITIVE(int16_t, short, jshort);
SET_PRIMITIVE(int32_t, int, jint);
SET_PRIMITIVE(int64_t, long, jlong);
SET_PRIMITIVE(float, float, jfloat);
SET_PRIMITIVE(double, double, double);

int get_object(jobject *dest, JNIEnv *env, jobject obj, jmethodID method) {
    *dest = (*env)->CallObjectMethod(env, obj, method);

    if ((*env)->ExceptionCheck(env)) {
        (*env)->DeleteLocalRef(env, *dest);
        *dest = NULL;
        return MARSHAL_THROWN_EX;
    }
    return MARSHAL_SUCCESS;
}

int set_object(JNIEnv *env, jobject obj, jmethodID method, jobject val) {
    (*env)->CallVoidMethod(env, obj, method, val);

    if ((*env)->ExceptionCheck(env)) {
        return MARSHAL_THROWN_EX;
    }

    return MARSHAL_SUCCESS;
}
