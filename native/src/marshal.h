#ifndef WAT_CHARS_H
#define WAT_CHARS_H

#include "struct/arrays.h"
#include "wat.h"

#include <jni.h>

#include <stdbool.h>

#define MARSHAL_SUCCESS ((unsigned int) 0)
#define MARSHAL_THROWN_EX ((unsigned int) 1)
#define MARSHAL_ALLOC_FAILURE ((unsigned int) 2)
#define MARSHAL_FAILURE ((unsigned int) 4)

unsigned int get_java_string(char **dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_boolean(bool *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_byte(int8_t *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_short(int16_t *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_int(int32_t *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_long(int64_t *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_float(float *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_double(double *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int get_object(jobject *dest, JNIEnv *env, jobject obj, jmethodID method);

unsigned int set_java_string(JNIEnv *env, jobject obj, jmethodID method, const char *val);

unsigned int set_boolean(JNIEnv *env, jobject obj, jmethodID method, bool val);

unsigned int set_byte(JNIEnv *env, jobject obj, jmethodID method, int8_t val);

unsigned int set_short(JNIEnv *env, jobject obj, jmethodID method, int16_t val);

unsigned int set_int(JNIEnv *env, jobject obj, jmethodID method, int32_t val);

unsigned int set_long(JNIEnv *env, jobject obj, jmethodID method, int64_t val);

unsigned int set_float(JNIEnv *env, jobject obj, jmethodID method, float val);

unsigned int set_double(JNIEnv *env, jobject obj, jmethodID method, double val);

unsigned int set_object(JNIEnv *env, jobject obj, jmethodID method, jobject val);

#endif // WAT_CHARS_H
