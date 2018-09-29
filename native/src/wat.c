#include "wat.h"

#include <stdlib.h>
#include <string.h>

bool wat_strcpy(char **dest, const char *src) {
    if (dest == NULL) {
        return false;
    }

    if (src == NULL) {
        *dest = NULL;
        return true;
    }

    const size_t length = strlen(src) + 1;
    *dest = WALLOC(char, length);
    if (*dest == NULL) {
        return false;
    }

#ifdef _WIN32
    strcpy_s(*dest, length, src);
#else
    strncpy(*dest, src, length);
#endif // _WIN32

    return true;
}
