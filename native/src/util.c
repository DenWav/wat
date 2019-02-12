#include "util.h"

#include "wat.h"

#include <stdlib.h>
#include <string.h>

WATEXPORT void WATCALL delete_string(char **string) {
    WAT_FREE(*string);
    *string = NULL;
}

WATEXPORT char * WATCALL create_string(char *string) {
    const size_t length = strlen(string);
    char *result = WALLOC(char, length);
    strcpy(result, string);
    return result;
}
