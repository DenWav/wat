<#-- @ftlvariable name="includes" type="java.util.List<demonwav.wat.Struct>" -->
<#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->
<#-- @ftlvariable name="isInArray" type="boolean" -->
<#-- @ftlvariable name="package" type="String" -->
<#-- @ftlvariable name="getter" type="com.demonwav.wat.BaseMethod" -->
<#-- @ftlvariable name="setter" type="com.demonwav.wat.BaseMethod" -->
<#-- @ftlvariable name="jvmType" type="com.demonwav.wat.BaseMethod" -->
<#-- @ftlvariable name="StringType" type="java.lang.Class" -->
<#-- @ftlvariable name="StructType" type="java.lang.Class" -->
<#-- @ftlvariable name="PrimitiveType" type="java.lang.Class" -->
<#-- @ftlvariable name="UuidType" type="java.lang.Class" -->
// GENERATED CODE - DO NOT EDIT

#include "jni/jni_decl.h"

#include "marshal.h"
#include "struct/arrays.h"
#include "wat.h"
<#-- Don't know which ones we'll need or not, so just include all of them.. -->
<#list includes as inc>
#include "struct/${inc.nativeName}.h"
</#list>

#include <jni.h>

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

static JNIEnv *jenv;

// ${package}.${struct.jvmName}
static jclass ${struct.nativeName}_java_class;

static jmethodID ${struct.nativeName}_java_const;

<#list struct.fields as field>
static jmethodID ${struct.nativeName}_java_get_${field.nativeName};
</#list>

<#list struct.fields as field>
static jmethodID ${struct.nativeName}_java_set_${field.nativeName};
</#list>

void setup_${struct.nativeName}_reference(JNIEnv *env) {
    jenv = env;

    // ${struct.jvmName}
    jclass local_${struct.nativeName}_java_class = (*env)->FindClass(env, "${package?replace(".", "/")}/${struct.jvmName}");
    ${struct.nativeName}_java_class = (*env)->NewGlobalRef(env, local_${struct.nativeName}_java_class);
    (*env)->DeleteLocalRef(env, local_${struct.nativeName}_java_class);

    ${struct.nativeName}_java_const = (*env)->GetMethodID(env, ${struct.nativeName}_java_class, "<init>", "()V");

<#list struct.fields as field>
    ${struct.nativeName}_java_get_${field.nativeName} = (*env)->GetMethodID(env, ${struct.nativeName}_java_class, "${getter(field)}", "()${jvmType(field)}");
</#list>

<#list struct.fields as field>
    ${struct.nativeName}_java_set_${field.nativeName} = (*env)->GetMethodID(env, ${struct.nativeName}_java_class, "${setter(field)}", "(${jvmType(field)})V");
</#list>
}

void delete_${struct.nativeName}_reference() {
    (*jenv)->DeleteGlobalRef(jenv, ${struct.nativeName}_java_class);
}

int load_${struct.nativeName}_data(struct ${struct.nativeName} **dest, jobject obj) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (obj == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    *dest = WALLOC(struct ${struct.nativeName}, 1);
    if (*dest == NULL) {
        return MARSHAL_ALLOC_FAILURE;
    }

    (*dest)->ref_count = 1;

<#assign has_set_temp = false>
<#assign max_length = 0>
<#list struct.fields as field>
    <#if StructType.isInstance(field.type) || UuidType.isInstance(field.type)>
        <#if !has_set_temp>
    jobject temp;
            <#assign has_set_temp = true>
        </#if>
    </#if>
    <#if max_length < field.nativeName?length>
        <#assign max_length = field.nativeName?length>
    </#if>
</#list>
    int status = MARSHAL_SUCCESS;
<#list struct.fields as field>
    <#if StringType.isInstance(field.type)>
    /* ${field.nativeName?right_pad(max_length)} */ status |= get_java_string(&(*dest)->${field.nativeName}, jenv, obj, ${struct.nativeName}_java_get_${field.nativeName});
    <#elseif StructType.isInstance(field.type) || UuidType.isInstance(field.type)>
    /* ${field.nativeName?right_pad(max_length)} */ status |= get_object(&temp, jenv, obj, ${struct.nativeName}_java_get_${field.nativeName});
    /* ${""?right_pad(max_length)} */ status |= load_${field.type.baseNativeName}_data(&(*dest)->${field.nativeName}, temp);
    /* ${""?right_pad(max_length)} */ (*jenv)->DeleteLocalRef(jenv, temp);
    <#elseif PrimitiveType.isInstance(field.type)>
    /* ${field.nativeName?right_pad(max_length)} */ status |= get_${field.type.jvmName}(&(*dest)->${field.nativeName}, jenv, obj, ${struct.nativeName}_java_get_${field.nativeName});
    </#if>
</#list>

    if (status != MARSHAL_SUCCESS) {
        delete_${struct.nativeName}(dest);
    }

    return status;
}

int create_${struct.nativeName}_java(jobject *dest, struct ${struct.nativeName} *src) {
    if (dest == NULL) {
        return MARSHAL_FAILURE;
    }

    if (src == NULL) {
        *dest = NULL;
        return MARSHAL_SUCCESS;
    }

    *dest = (*jenv)->NewObject(jenv, ${struct.nativeName}_java_class, ${struct.nativeName}_java_const);
    if ((*jenv)->ExceptionCheck(jenv)) {
        if (*dest != NULL) {
            (*jenv)->DeleteLocalRef(jenv, *dest);
            return MARSHAL_THROWN_EX;
        }
    }

    if (*dest == NULL) {
        return MARSHAL_ALLOC_FAILURE;
    }

<#assign has_set_temp = false>
<#assign max_length = 0>
<#list struct.fields as field>
    <#if StructType.isInstance(field.type) || UuidType.isInstance(field.type)>
        <#if !has_set_temp>
    jobject temp;
            <#assign has_set_temp = true>
        </#if>
    </#if>
    <#if max_length < field.nativeName?length>
        <#assign max_length = field.nativeName?length>
    </#if>
</#list>
    int status = MARSHAL_SUCCESS;
<#list struct.fields as field>
    <#if StringType.isInstance(field.type)>
    /* ${field.nativeName?right_pad(max_length)} */ status |= set_java_string(jenv, *dest, ${struct.nativeName}_java_set_${field.nativeName}, src->${field.nativeName});
    <#elseif StructType.isInstance(field.type) || UuidType.isInstance(field.type)>
    /* ${""?right_pad(max_length)} */ status |= create_${field.type.baseNativeName}_java(&temp, src->${field.nativeName});
    /* ${field.nativeName?right_pad(max_length)} */ status |= set_object(jenv, *dest, ${struct.nativeName}_java_set_${field.nativeName}, temp);
    /* ${""?right_pad(max_length)} */ (*jenv)->DeleteLocalRef(jenv, temp);
    <#elseif PrimitiveType.isInstance(field.type)>
    /* ${field.nativeName?right_pad(max_length)} */ status |= set_${field.type.jvmName}(jenv, *dest, ${struct.nativeName}_java_set_${field.nativeName}, src->${field.nativeName});
    </#if>
</#list>

    if (status != MARSHAL_SUCCESS) {
        (*jenv)->DeleteLocalRef(jenv, *dest);
        *dest = NULL;
    }

    return status;
}
<#if isInArray>

CREATE_JAVA_ARRAY(${struct.nativeName})

LOAD_ARRAY_DATA(${struct.nativeName})
</#if>
