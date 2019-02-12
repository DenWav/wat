#include "logger.h"

#include "wat.h"

#include <jni.h>
#include <stdlib.h>

static JNIEnv *jenv;

static jclass java_util_logging_logger;

static jobject logger;

static jmethodID fine_method;
static jmethodID info_method;
static jmethodID warning_method;
static jmethodID severe_method;

#ifdef _WIN32
    #define FORMAT_LENGTH(fmt, args) _vscprintf(fmt, args) + 1
    #define FORMAT(text, size, fmt, args) vsnprintf_s(text, (size_t) size, (size_t) size, fmt, args)
#else
    #define FORMAT_LENGTH(fmt, args) vsnprintf(NULL, 0, fmt, args) + 1
    #define FORMAT(text, size, fmt, args) vsnprintf(text, (size_t) size, fmt, args)
#endif

static inline void bukkit_log(jmethodID method, const char *fmt, va_list args) {
    va_list copy;
    va_copy(copy, args);
    int size = FORMAT_LENGTH(fmt, args);
    va_end(args);
    char *text = WALLOC(char, size);
    FORMAT(text, size, fmt, copy);
    va_end(copy);
    jobject string = (*jenv)->NewStringUTF(jenv, text);
    WAT_FREE(text);
    (*jenv)->CallVoidMethod(jenv, logger, method, string);
    (*jenv)->DeleteLocalRef(jenv, string);
}

WATEXPORT void WATCALL bukkit_log_fine(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    bukkit_log(fine_method, fmt, args);
}

WATEXPORT void WATCALL bukkit_log_info(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    bukkit_log(info_method, fmt, args);
}

WATEXPORT void WATCALL bukkit_log_warning(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    bukkit_log(warning_method, fmt, args);
}

WATEXPORT void WATCALL bukkit_log_severe(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    bukkit_log(severe_method, fmt, args);
}

void init_logger_instance(void *logger_obj) {
    logger = (*jenv)->NewGlobalRef(jenv, logger_obj);
}

void close_logger_instance() {
    (*jenv)->DeleteGlobalRef(jenv, logger);
}

void setup_logger_reference(JNIEnv *env) {
    jenv = (JNIEnv*) env;

    jclass temp_java_util_logging_logger = (*jenv)->FindClass(jenv, "java/util/logging/Logger");
    java_util_logging_logger = (*jenv)->NewGlobalRef(jenv, temp_java_util_logging_logger);
    (*jenv)->DeleteLocalRef(jenv, temp_java_util_logging_logger);

    fine_method = (*jenv)->GetMethodID(jenv, java_util_logging_logger, "fine", "(Ljava/lang/String;)V");
    info_method = (*jenv)->GetMethodID(jenv, java_util_logging_logger, "info", "(Ljava/lang/String;)V");
    warning_method = (*jenv)->GetMethodID(jenv, java_util_logging_logger, "warning", "(Ljava/lang/String;)V");
    severe_method = (*jenv)->GetMethodID(jenv, java_util_logging_logger, "severe", "(Ljava/lang/String;)V");
}

void delete_logger_reference() {
    (*jenv)->DeleteGlobalRef(jenv, java_util_logging_logger);
}

