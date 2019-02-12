#ifndef WAT_UTIL_H
#define WAT_UTIL_H

#include "wat.h"

WAT_EXPORT_START
/**
 * Convenience method for `free`ing C-strings created by wat. You should use this rather than calling `free` yourself or
 * using your language to delete these strings, as your allocator may not be the same. Similarly, whenever passing a
 * string to wat, use create_string(char *string) to create a string with the same text that wat can handle.
 * @param[in] string A pointer to the string to delete
 */
WATEXPORT void WATCALL delete_string(char **string);

/**
 * When passing new strings to wat, you should use this function to create strings that wat can handle. This will take
 * the C-string passed to it and make a copy using wat's allocator. When using this function, you should create your
 * string, call this function, then `free` or otherwise delete your string and pass the string you retrieve from this
 * function to wat instead.
 * @param[in] string The string to copy
 * @return The new string, created using wat's allocator
 */
WATEXPORT char * WATCALL create_string(char *string);
WAT_EXPORT_END

#endif //WAT_UTIL_H
