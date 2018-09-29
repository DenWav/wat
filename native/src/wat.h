#ifndef WAT_BASE_H
#define WAT_BASE_H

#include <stdbool.h>
#include <stdint.h>

#define WAT_EXPORT_START
#define WAT_EXPORT_END

#define WALLOC(type, amount) malloc(sizeof(type) * amount)

#define WAT_FREE(ptr) if (ptr != NULL) free(ptr)

WAT_EXPORT_START
#ifdef _WIN32
    #define WATCALL __cdecl
#else
    #define WATCALL
#endif

#ifdef _WIN32
    #define WATEXPORT __declspec(dllexport)
#else
    #define WATEXPORT __attribute__((visibility("default")))
#endif // _WIN32

#define WAT_INIT wat_init
#define WAT_CLOSE wat_close

#define WAT_CLONE 0
#define WAT_COPY 1

/**
 * Define this function in your plugin to be called in onEnable
 */
WATEXPORT void WATCALL WAT_INIT();

/**
 * Define this function in your plugin to be called in onDisable
 */
WATEXPORT void WATCALL WAT_CLOSE();
WAT_EXPORT_END

#define STR(name) #name
#define _STR(name) STR(name)
#define WAT_INIT_NAME _STR(WAT_INIT)
#define WAT_CLOSE_NAME _STR(WAT_CLOSE)

#define wat_clone(type)                                              \
    WATEXPORT struct type * WATCALL clone_##type(struct type *ptr) { \
        if (ptr == NULL) { return NULL; }                            \
        ptr->ref_count++;                                            \
        return ptr;                                                  \
    }

#define wat_reassign(type)                                                            \
    WATEXPORT void WATCALL reassign_##type(struct type **ptr, struct type *newval) {  \
        delete_## type(ptr);                                                          \
        *ptr = newval;                                                                \
    }

bool wat_strcpy(char **dest, const char *src);

#endif //WAT_BASE_H
