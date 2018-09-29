#ifndef WAT_UUID_H
#define WAT_UUID_H

#include "wat.h"

#include <stdbool.h>
#include <stdint.h>

WAT_EXPORT_START
#define UUID_SIZE 16

typedef struct {
    int8_t bytes[UUID_SIZE];
} uuid;

/**
 * Copy the data from src into dest. dest must not be NULL, false will be returned. src may be NULL, in which case dest
 * will be set to NULL and true will be returned. Note that uuid is treated more like a c-string in that it's not
 * reference counted. Instances of uuid's are destroyed when their containing struct is destroyed, so to retain one you
 * must first copy it and then manage your instance yourself. The length of a UUID is always 16 bytes.
 * @param dest The uuid to copy to, may not be NULL.
 * @param src The uuid to copy from, may be NULL.
 * @return true if and only if the copy finished successfully. Invalid parameters or malloc failures will return false.
 */
WATEXPORT bool WATCALL copy_uuid(uuid **dest, uuid *src);
WAT_EXPORT_END

#endif //WAT_UUID_H
