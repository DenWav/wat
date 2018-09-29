#include "jni/com_demonwav_wat_bind_NativeLogger.h"

#include "logger.h"

JNIEXPORT void JNICALL Java_com_demonwav_wat_bind_NativeLogger_initLoggerInstance(JNIEnv *env, jclass class, jobject logger_obj) {
    init_logger_instance(logger_obj);
}

JNIEXPORT void JNICALL Java_com_demonwav_wat_bind_NativeLogger_closeLoggerInstance(JNIEnv *env, jclass class) {
    close_logger_instance();
}
