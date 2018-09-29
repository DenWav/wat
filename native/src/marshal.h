#ifndef WAT_CHARS_H
#define WAT_CHARS_H

#include "struct/arrays.h"
#include "wat.h"

#include <jni.h>

#include <stdbool.h>

#define MARSHAL_SUCCESS 0
#define MARSHAL_THROWN_EX 1
#define MARSHAL_ALLOC_FAILURE 2
#define MARSHAL_FAILURE 4

int get_java_string(char **dest, JNIEnv *env, jobject obj, jmethodID method);

int get_boolean(bool *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_byte(int8_t *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_short(int16_t *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_int(int32_t *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_long(int64_t *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_float(float *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_double(double *dest, JNIEnv *env, jobject obj, jmethodID method);

int get_object(jobject *dest, JNIEnv *env, jobject obj, jmethodID method);

int set_java_string(JNIEnv *env, jobject obj, jmethodID method, const char *val);

int set_boolean(JNIEnv *env, jobject obj, jmethodID method, bool val);

int set_byte(JNIEnv *env, jobject obj, jmethodID method, int8_t val);

int set_short(JNIEnv *env, jobject obj, jmethodID method, int16_t val);

int set_int(JNIEnv *env, jobject obj, jmethodID method, int32_t val);

int set_long(JNIEnv *env, jobject obj, jmethodID method, int64_t val);

int set_float(JNIEnv *env, jobject obj, jmethodID method, float val);

int set_double(JNIEnv *env, jobject obj, jmethodID method, double val);

int set_object(JNIEnv *env, jobject obj, jmethodID method, jobject val);

#endif // WAT_CHARS_H
