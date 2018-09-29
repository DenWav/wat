<#-- @ftlvariable name="files" type="java.util.List<String>" -->
#ifndef __WAT_H__
#define __WAT_H__

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

<#list files as file>
${file}

</#list>
#ifdef __cplusplus
}
#endif // __cplusplus
#endif // __WAT_H__
