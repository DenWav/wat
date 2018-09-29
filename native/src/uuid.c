#include "uuid.h"

#include "wat.h"

#include <stdlib.h>
#include <string.h>

WATEXPORT bool WATCALL copy_uuid(uuid **dest, uuid *src) {
    if (dest == NULL) {
        return false;
    }

    if (src == NULL) {
        *dest = NULL;
        return true;
    }

    *dest = WALLOC(uuid, 1);
    if (*dest == NULL) {
        return false;
    }

    memcpy((*dest)->bytes, src->bytes, UUID_SIZE);

    return true;
}
