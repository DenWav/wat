#ifndef WAT_LOGGER_H
#define WAT_LOGGER_H

#include "wat.h"

#include <jni.h>
#include <stdarg.h>

WAT_EXPORT_START
WATEXPORT void WATCALL bukkit_log_fine(const char *fmt, ...);
WATEXPORT void WATCALL bukkit_log_info(const char *fmt, ...);
WATEXPORT void WATCALL bukkit_log_warning(const char *fmt, ...);
WATEXPORT void WATCALL bukkit_log_severe(const char *fmt, ...);
WAT_EXPORT_END

void init_logger_instance(void *logger_obj);
void close_logger_instance();

void setup_logger_reference(JNIEnv *env);
void delete_logger_reference();

#endif // WAT_LOGGER_H
