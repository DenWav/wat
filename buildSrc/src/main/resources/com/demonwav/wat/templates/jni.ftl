<#-- @ftlvariable name="projectName" type="String" -->
<#-- @ftlvariable name="fileName" type="String" -->
<#-- @ftlvariable name="structs" type="java.util.List<com.demonwav.wat.Struct>" -->
<#-- @ftlvariable name="loadStructs" type="java.util.List<com.demonwav.wat.Struct>" -->
<#assign defName = "__${projectName?upper_case}_${fileName?upper_case}_H__">
// GENERATED CODE - DO NOT EDIT
#ifndef ${defName}
#define ${defName}

#include "uuid.h"
<#list structs as struct>
#include "struct/${struct.nativeName}.h"
</#list>

#include <jni.h>

void setup_jni_reference(JNIEnv *env);
void delete_jni_reference();

void setup_uuid_reference(JNIEnv *env);
void delete_uuid_reference();

unsigned int load_uuid_data(uuid **dest, jobject obj);
unsigned int create_uuid_java(jobject *dest, uuid *src);
<#list structs as struct>

void setup_${struct.nativeName}_reference(JNIEnv *env);
void delete_${struct.nativeName}_reference();
</#list>
<#list loadStructs as load>

unsigned int load_${load.nativeName}_data(struct ${load.nativeName} **dest, jobject obj);
unsigned int create_${load.nativeName}_java(jobject *dest, struct ${load.nativeName} *src);
</#list>

#define CREATE_JAVA_ARRAY(native_name)                                                                \
    unsigned int create_##native_name##_array_java(jobject *dest, struct native_name##_array *src) {  \
        if (dest == NULL) {                                                                           \
            return MARSHAL_FAILURE;                                                                   \
        }                                                                                             \
                                                                                                      \
        if (src == NULL) {                                                                            \
            *dest = NULL;                                                                             \
            return MARSHAL_SUCCESS;                                                                   \
        }                                                                                             \
                                                                                                      \
        const int length = src->length;                                                               \
                                                                                                      \
        *dest = (*jenv)->NewObjectArray(jenv, length, native_name##_java_class, NULL);                \
        if ((*jenv)->ExceptionCheck(jenv)) {                                                          \
            if (*dest == NULL) {                                                                      \
                (*jenv)->DeleteLocalRef(jenv, *dest);                                                 \
                return MARSHAL_THROWN_EX;                                                             \
            }                                                                                         \
        }                                                                                             \
                                                                                                      \
        if (*dest == NULL) {                                                                          \
            return MARSHAL_ALLOC_FAILURE;                                                             \
        }                                                                                             \
                                                                                                      \
        unsigned int status = MARSHAL_SUCCESS;                                                        \
        for (int i = 0; i < length; i++) {                                                            \
            jobject element;                                                                          \
            status |= create_##native_name##_java(&element, src->array[i]);                           \
            (*jenv)->SetObjectArrayElement(jenv, *dest, i, element);                                  \
            (*jenv)->DeleteLocalRef(jenv, element);                                                   \
        }                                                                                             \
                                                                                                      \
        if (status != MARSHAL_SUCCESS) {                                                              \
            (*jenv)->DeleteLocalRef(jenv, *dest);                                                     \
            *dest = NULL;                                                                             \
        }                                                                                             \
                                                                                                      \
        return status;                                                                                \
    }

#define LOAD_ARRAY_DATA(native_name)                                                                  \
    unsigned int load_##native_name##_array_data(struct native_name##_array **dest, jobject obj) {    \
        if (dest == NULL) {                                                                           \
            return MARSHAL_FAILURE;                                                                   \
        }                                                                                             \
                                                                                                      \
        if (obj == NULL) {                                                                            \
            *dest = NULL;                                                                             \
            return MARSHAL_SUCCESS;                                                                   \
        }                                                                                             \
                                                                                                      \
        *dest = WALLOC(struct native_name##_array, 1);                                                \
        if (*dest == NULL) {                                                                          \
            return MARSHAL_ALLOC_FAILURE;                                                             \
        }                                                                                             \
                                                                                                      \
        const jsize length = (*jenv)->GetArrayLength(jenv, obj);                                      \
                                                                                                      \
        (*dest)->array = WALLOC(struct native_name *, length);                                        \
        if ((*dest)->array == NULL) {                                                                 \
            WAT_FREE(*dest);                                                                          \
            *dest = NULL;                                                                             \
            return MARSHAL_ALLOC_FAILURE;                                                             \
        }                                                                                             \
                                                                                                      \
        (*dest)->alloc = length;                                                                      \
        (*dest)->length = length;                                                                     \
                                                                                                      \
        unsigned int status = MARSHAL_SUCCESS;                                                        \
        for (int i = 0; i < length; i++) {                                                            \
            jobject element = (*jenv)->GetObjectArrayElement(jenv, obj, i);                           \
            status |= load_##native_name##_data(&(*dest)->array[i], element);                         \
        }                                                                                             \
                                                                                                      \
        if (status != MARSHAL_SUCCESS) {                                                              \
            delete_##native_name##_array(dest);                                                       \
        }                                                                                             \
                                                                                                      \
        return status;                                                                                \
    }

#endif // ${defName}
