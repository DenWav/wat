#include "jni/com_demonwav_wat_bind_NativeInit.h"

#include "logger.h"
#include "event/player_join_event.h"
#include "exception.h"
#include "jni/jni_decl.h"

#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
    #include <Windows.h>
#else
    #include <dlfcn.h>
#endif // _WIN32

static jint JNI_VERSION = JNI_VERSION_10;

#ifdef _WIN32
	#define LIB_TYPE HMODULE
	#define LOAD_LIBRARY(path) LoadLibrary(TEXT(path))
	#define FIND_SYMBOL(lib, func_name) GetProcAddress(lib, func_name)
	#define CLOSE_LIBRARY(lib) FreeLibrary(lib)
#else
	#define LIB_TYPE void *
	#define LOAD_LIBRARY(path) dlopen(path, RTLD_NOW)
	#define FIND_SYMBOL(lib, func_name) dlsym(lib, func_name)
	#define CLOSE_LIBRARY(lib) dlclose(lib)
#endif

static int libs_count = 0;
static LIB_TYPE *libs;

typedef void (WATCALL *simple_function)();

JNIEXPORT void JNICALL Java_com_demonwav_wat_bind_NativeInit_init(JNIEnv *env, jclass class, jobjectArray paths) {
    jsize length = (*env)->GetArrayLength(env, paths);

    libs_count = length;
    libs = WALLOC(LIB_TYPE, libs_count);

    for (int i = 0; i < length; i++) {
        jobject path = (*env)->GetObjectArrayElement(env, paths, i);
        const char *lib_path = (*env)->GetStringUTFChars(env, path, NULL);

        bukkit_log_info("Loading native plugin: %s", lib_path);

		LIB_TYPE lib = LOAD_LIBRARY(lib_path);

        libs[i] = lib;
        if (lib != NULL) {
            bukkit_log_info("Native plugin %s loaded successfully", lib_path);

            simple_function func = (simple_function) FIND_SYMBOL(lib, WAT_INIT_NAME);

            if (func != NULL) {
                bukkit_log_info("Calling init function for native plugin %s", lib_path);
                func();
            } else {
                bukkit_log_warning("No init function for native plugin %s found (%s), skipping", lib_path, dlerror());
            }
        } else {
            bukkit_log_warning("Native plugin %s failed to load (%s)", lib_path, dlerror());
        }
    }
}

JNIEXPORT void JNICALL Java_com_demonwav_wat_bind_NativeInit_teardown(JNIEnv *env, jclass class) {
    for (int i = 0; i < libs_count; i++) {
		LIB_TYPE lib = libs[i];
		if (lib == NULL) {
		    continue;
		}

        simple_function func = (simple_function) FIND_SYMBOL(lib, WAT_CLOSE_NAME);

        if (func != NULL) {
            func();
        }

        CLOSE_LIBRARY(lib);
    }

    WAT_FREE(libs);
    libs = NULL;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION) != JNI_OK) {
        return JNI_ERR;
    }

    setup_jni_reference(env);

    setup_player_join_event_callbacks();

    return JNI_VERSION;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION);

    delete_player_join_event_callbacks();

    delete_jni_reference();
}
