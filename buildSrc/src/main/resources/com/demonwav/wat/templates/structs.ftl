<#-- @ftlvariable name="projectName" type="String" -->
<#-- @ftlvariable name="fileName" type="String" -->
<#-- @ftlvariable name="includes" type="java.util.List<String>" -->
<#-- @ftlvariable name="structs" type="java.util.List<com.demonwav.wat.Struct>" -->
<#assign defName = "__${projectName?upper_case}_${fileName?upper_case}_H__">
// GENERATED CODE - DO NOT EDIT
#ifndef ${defName}
#define ${defName}

#include "uuid.h"
#include "wat.h"
<#list includes as include>
    <#if !include?starts_with(fileName)>
#include "${include}"
    </#if>
</#list>

#include <stdbool.h>
#include <stdint.h>

WAT_EXPORT_START
<#list structs as struct>
    <#assign nativeName=struct.nativeName>
    <#assign isArray = false>
// ${nativeName}
struct ${nativeName} {
    int ref_count;
    <#list struct.fields as field>
        <#if field.nativeName == "array">
            <#assign isArray = true>
            <#assign arrayType = field.type.baseType.nativeName>
        </#if>
        <#if field.type.nativeName?ends_with("*")>
    ${field.type.nativeName}${field.nativeName};
        <#else>
    ${field.type.nativeName} ${field.nativeName};
        </#if>
    </#list>
};

/**
 * Delete the given ${struct.nativeName} and set the pointer value to NULL. The pointer given will always be set to NULL
 * after calling this function. It is fine to pass NULL to this function, in which case nothing will happen. This function
 * will decrement the ref_count field and only delete it once it reaches 0. You should call this function as soon as you
 * are finished with a ${struct.nativeName}.
 * @param[in] s The ${struct.nativeName} to delete.
 */
WATEXPORT void WATCALL delete_${nativeName}(struct ${nativeName} **s);

/**
 * Clone the given ${struct.nativeName} and return it back to the caller. This function always returns the same pointer provided.
 * This function increments the ref_count field by 1 before returning. Passing NULL to this function will result in NULL
 * being returned.
 * @param[in] s The ${struct.nativeName} to clone.
 * @return The same ${struct.nativeName}, with the ref_count field incremented.
 */
WATEXPORT struct ${nativeName} * WATCALL clone_${nativeName}(struct ${nativeName} *s);

/**
 * Deep copy the src ${struct.nativeName} to dest. dest will be a deep copy of src (any containing structs will also be deep copied)
 * and its ref_count will be 1 (which is true for all children as well). src's ref_count is not modified. dest must not
 * be NULL. src may be NULL, in which case nothing is copied and dest is set to NULL. This function will return true if
 * the copy succeeds, and false if otherwise (for example, if malloc returns a NULL pointer).
 * @param[out] dest The destination to copy to. Must not be NULL.
 * @param[in] src The source to copy from. May be NULL.
 * @return true if an only if the copy succeeded.
 */
WATEXPORT bool WATCALL copy_${nativeName}(struct ${nativeName} **dest, struct ${nativeName} *src);

/**
 * Reassign a single pointer src to a new value. This is useful if you have a single ${struct.nativeName} pointer and want to do
 * some operation with it that consumes it (after the call it needs to be deleted) and the pointer reassigned to a new
 * value (the result of a function that returns a new ${struct.nativeName}). This function will set the value of src to the value
 * provided by newval and call delete_${struct.nativeName} on the previous value of src.
 * @param[in,out] src The value to delete and reassign with newval. Must not be NULL.
 * @param[in] newval The value to assign to src. May be NULL.
 */
WATEXPORT void WATCALL reassign_${nativeName}(struct ${nativeName} **src, struct ${nativeName} *newval);
    <#if isArray>

        <#if arrayType?ends_with("*")>
/**
 * Add a ${struct.nativeName} to the end of the given array. The array will be resized if necessary. s must not be NULL, false will
 * be returned. entry may be NULL, in which case nothing will happen and true will be returned. false is returned for
 * all other cases where the add fails, for example if the resize fails. type should either be WAT_CLONE or WAT_COPY.
 * @param[in] s The array to add the ${struct.nativeName} to, must not be NULL.
 * @param[in] entry The ${struct.nativeName} to add to the array, may be NULL.
 * @param[in] type Either WAT_CLONE or WAT_COPY to determine how references are managed.
 * @return true if and only if the add succeeds, false in all other cases.
 */
WATEXPORT bool WATCALL add_${nativeName}_entry(struct ${nativeName} *s, ${arrayType}entry, int type);
        <#else>
/**
 * Add a ${struct.nativeName} to the end of the given array. The array will be resized if necessary. s must not be NULL, false will
 * be returned. false is returned for all other cases where the add fails, for example if the resize fails.
 * @param[in] s The array to add the ${struct.nativeName} to, must not be NULL.
 * @param[in] entry The ${struct.nativeName} to add to the array.
 * @return true if and only if the add succeeds, false in all other cases.
 */
WATEXPORT bool WATCALL add_${nativeName}_entry(struct ${nativeName} *s, ${arrayType} entry);
        </#if>
    </#if>
</#list>
WAT_EXPORT_END

#endif // ${defName}
