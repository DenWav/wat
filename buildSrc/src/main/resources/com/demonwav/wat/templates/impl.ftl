<#-- @ftlvariable name="includes" type="java.util.List<com.demonwav.wat.Struct>" -->
<#-- @ftlvariable name="structs" type="java.util.List<com.demonwav.wat.Struct>" -->
<#-- @ftlvariable name="StringType" type="java.lang.Class" -->
<#-- @ftlvariable name="StructType" type="java.lang.Class" -->
<#-- @ftlvariable name="PointerType" type="java.lang.Class" -->
<#-- @ftlvariable name="UuidType" type="java.lang.Class" -->
// GENERATED CODE - DO NOT EDIT

#include "arrays.h"
#include "uuid.h"
#include "wat.h"
<#list includes as include>
#include "${include.nativeName}.h"
</#list>

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
<#list structs as struct>
<@delete struct />
</#list>

<#list structs as struct>
<@clone struct />
</#list>

<#list structs as struct>
<@reassign struct />
</#list>
<#list structs as struct>
<@copy struct />
</#list>
<#list structs as struct>
<@add struct />
</#list>


<#macro delete struct>
    <#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->

WATEXPORT void WATCALL delete_${struct.nativeName}(struct ${struct.nativeName} **s) {
    if (s == NULL || *s == NULL) {
        return;
    }

    if (--(*s)->ref_count > 0) {
        *s = NULL;
        return;
    }

<#list struct.fields as field>
    <#if StringType.isInstance(field.type) || UuidType.isInstance(field.type)>
    WAT_FREE((*s)->${field.nativeName});
    <#elseif StructType.isInstance(field.type)>
    delete_${field.type.baseNativeName}(&(*s)->${field.nativeName});
    <#elseif PointerType.isInstance(field.type)>
        <#if StructType.isInstance(field.type.baseType)>
    const int length = (*s)->length;
    for (int i = 0; i < length; i++) {
        ${field.type.baseType.nativeName} l = (*s)->${field.nativeName}[i];
        delete_${field.type.baseType.baseNativeName}(&l);
    }
    WAT_FREE((*s)->${field.nativeName});
        <#elseif StringType.isInstance(field.type.baseType) || UuidType.isInstance(field.type.baseType)>
    const int length = (*s)->length;
    for (int i = 0; i < length; i++) {
        ${field.type.baseType.nativeName} l = (*s)->${field.nativeName}[i];
        WAT_FREE(l);
    }
    WAT_FREE((*s)->${field.nativeName});
        <#else>
    WAT_FREE((*s)->${field.nativeName});
        </#if>
    </#if>
</#list>

    WAT_FREE(*s);
    *s = NULL;
}
</#macro>

<#macro clone struct>
    <#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->
wat_clone(${struct.nativeName})
</#macro>

<#macro reassign struct>
    <#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->
wat_reassign(${struct.nativeName})
</#macro>

<#macro copy struct>
    <#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->

WATEXPORT bool WATCALL copy_${struct.nativeName}(struct ${struct.nativeName} **dest, struct ${struct.nativeName} *src) {
    if (dest == NULL) {
        return false;
    }

    if (src == NULL) {
        *dest = NULL;
        return true;
    }

    *dest = WALLOC(struct ${struct.nativeName}, 1);
    if (*dest == NULL) {
        return false;
    }

    bool status = true;
    (*dest)->ref_count = 1;

    <#if struct.nativeName?ends_with("array")>
    const int length = src->length;
        <#assign arrayType = "">
        <#assign baseType = "">
        <#assign isStruct = false>
        <#list struct.fields as field>
            <#assign modifier = "">
            <#if field.nativeName = "array">
                <#if StructType.isInstance(field.type.baseType)>
                    <#assign baseType = field.type.baseType.baseNativeName>
                    <#assign arrayType = "struct ${baseType}">
                    <#assign isStruct = true>
                <#else>
                    <#assign baseType = field.type.baseType.baseNativeName>
                    <#assign arrayType = field.type.baseType.baseNativeName>
                </#if>
                <#break>
            </#if>
        </#list>
    (*dest)->alloc = length;
    (*dest)->length = length;
    (*dest)->array = WALLOC(${arrayType}, length);
    if ((*dest)->array != NULL) {
        for (int i = 0; i < length; i++) {
        <#if isStruct>
            copy_${baseType}(&(*dest)->array[i], src->array[i]);
        <#else>
            (*dest)->array[i] = src->array[i];
        </#if>
        }
    } else {
        status = false;
    }
    <#else>
        <#list struct.fields as field>
            <#if StringType.isInstance(field.type)>
    status &= wat_strcpy(&(*dest)->${field.nativeName}, src->${field.nativeName});
            <#elseif StructType.isInstance(field.type) || UuidType.isInstance(field.type)>
    status &= copy_${field.type.baseNativeName}(&(*dest)->${field.nativeName}, src->${field.nativeName});
            <#else>
    (*dest)->${field.nativeName} = src->${field.nativeName};
            </#if>
        </#list>
    </#if>

    if (!status) {
        delete_${struct.nativeName}(dest);
    }

    return status;
}
</#macro>

<#macro add struct>
    <#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->
    <#assign isArray = false>
    <#list struct.fields as field>
        <#if field.nativeName == "array">
            <#assign arrayType = field.type.baseType>
            <#assign isArray = true>
        </#if>
    </#list>
    <#if !isArray>
        <#return>
    </#if>

    <#assign isPointer = arrayType.nativeName?ends_with("*")>
    <#assign paddedArrayType = arrayType.nativeName>
    <#if isPointer>
WATEXPORT bool WATCALL add_${struct.nativeName}_entry(struct ${struct.nativeName} *s, ${arrayType.nativeName}entry, int type) {
    <#else>
        <#assign paddedArrayType = "${arrayType.nativeName} ">
WATEXPORT bool WATCALL add_${struct.nativeName}_entry(struct ${struct.nativeName} *s, ${arrayType.nativeName} entry) {
    </#if>
    if (s == NULL) {
        return false;
    }
    <#if isPointer>

    if (entry == NULL) {
        return true;
    }
    </#if>

    if (s->alloc < s->length + 1) {
        const int new_alloc = s->alloc * 2;
        ${paddedArrayType}*new_array = WALLOC(${arrayType.nativeName}, new_alloc);
        if (new_array == NULL) {
            return false;
        }

    <#if isPointer>
        int cleanup = -1;
    </#if>
        for (int i = 0; i < s->length; i++) {
    <#if StringType.isInstance(arrayType)>
        <#assign copy = "wat_strcpy">
    <#else>
        <#assign copy = "copy_${arrayType.baseNativeName}">
    </#if>
    <#if isPointer>
            if (!${copy}(&new_array[i], s->array[i])) {
                cleanup = i;
                break;
            }
    <#else>
            new_array[i] = s->array[i];
    </#if>
        }
    <#if isPointer>

        <#if StringType.isInstance(arrayType)>
            <#assign delete = "WAT_FREE">
        <#else>
            <#assign delete = "delete_${arrayType.baseNativeName}">
        </#if>
        if (cleanup != -1) {
            for (int i = 0; i < cleanup; i++) {
                ${delete}(&new_array[i]);
            }
            return false;
        }

        for (int i = 0; i < s->length; i++) {
            ${delete}(&s->array[i]);
        }
    </#if>

        WAT_FREE(s->array);

        s->alloc = new_alloc;
        s->array = new_array;
    }

    <#if isPointer && !StringType.isInstance(arrayType)>
    ${arrayType.nativeName}ptr;
    if (type == WAT_CLONE) {
        ptr = clone_${arrayType.baseNativeName}(entry);
    } else if (type == WAT_COPY) {
        if (!copy_${arrayType.baseNativeName}(&ptr, entry)) {
            return false;
        }
    } else {
        return false;
    }

    s->array[s->length++] = ptr;
    <#else>
    s->array[s->length++] = entry;
    </#if>

    return true;
}
</#macro>
