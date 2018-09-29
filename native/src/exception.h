#ifndef WAT_EXCEPTION_H
#define WAT_EXCEPTION_H

#include <jni.h>
#include <stdbool.h>

void throw_illegal_state(const char *message);

void throw_wat_exception(const char *message);

void throw_generic(const char *class, const char *message);

void setup_exception_reference(JNIEnv *env);
void delete_exception_reference();

bool throw_from_marshal_status(int status);

#endif // WAT_EXCEPTION_H
