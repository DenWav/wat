package com.demonwav.wat

import freemarker.ext.beans.BeanModel
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModelException
import java.io.File

val config = Configuration(Configuration.VERSION_2_3_28)

fun write(structs: List<Struct>, jvmDir: File, nativeDir: File, packageName: String, projectName: String) {
    config.setClassForTemplateLoading(object {}.javaClass, "templates")
    config.defaultEncoding = "UTF-8"
    config.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
    config.wrapUncheckedExceptions = true

    val jvmOut = File(jvmDir, packageName.replace('.', '/'))
    val structNativeDir = nativeDir.resolve("struct")
    val jniNativeDir = nativeDir.resolve("jni")

    jvmOut.mkdirs()
    structNativeDir.mkdirs()
    jniNativeDir.mkdirs()

    val deleteStructs = mutableListOf<Struct>()
    for (struct in structs) {
        writeJava(struct, jvmOut, packageName)
        writeNative(listOf(struct), struct.nativeName, structNativeDir, projectName, deleteStructs)
    }

    writeNativeArrays(structs, structNativeDir, projectName, deleteStructs)

    writeImpl(structs, deleteStructs, structNativeDir)

    writeJni(structs, deleteStructs, projectName, "jni_decl", jniNativeDir)

    for (struct in structs) {
        val isInArray = structs.any { it.fields.any { it.type is ArrayType && it.type.depth == 1 && it.type.baseType.baseNativeName == struct.nativeName } }
        writeJniImpl(struct, structs, packageName, isInArray, jniNativeDir)
    }
}

private fun writeJava(struct: Struct, outDir: File, packageName: String) {
    val outFile = File(outDir, struct.jvmName + ".java")

    val template = config.getTemplate("java.ftl")
    outFile.bufferedWriter().use { writer ->
        template.process(mapOf("struct" to struct, "packageName" to packageName), writer)
    }
}

private fun writeNative(
    structs: List<Struct>,
    name: String,
    outDir: File,
    projectName: String,
    deleteStructs: MutableList<Struct>,
    filter: (Type) -> Boolean = { true }
) {
    val includes = getIncludes(structs, filter)
    includes += "arrays.h"

    deleteStructs.addAll(structs)

    val outFile = File(outDir, "$name.h")

    val template = config.getTemplate("structs.ftl")

    outFile.bufferedWriter().use { writer ->
        template.process(
            mapOf(
                "projectName" to projectName,
                "fileName" to name,
                "includes" to includes,
                "structs" to structs
            ),
            writer
        )
    }
}

private fun writeNativeArrays(structs: List<Struct>, nativeDir: File, projectName: String, deleteStructs: MutableList<Struct>) {
    val biggestArrays = structs.asSequence()
        .flatMap { it.fields.asSequence() }
        .map { it.type }
        .mapNotNull { it as? ArrayType }
        .groupingBy { it.baseType }
        .fold({ _, element -> element }) { _, accumulator, element ->
            if (element.depth > accumulator.depth) {
                element
            } else {
                accumulator
            }
        }.values

    val arrayStructs = biggestArrays.asSequence()
        .flatMap { it.types(sequenceOf()) }
        .map {
            Struct(
                "", it.baseNativeName, listOf(
                    Field("", "length", NormalInt),
                    Field("", "alloc", NormalInt),
                    Field("", "array", asPointer(it.type))
                )
            )
        }
        .toList()
        .asReversed() // smallest should come first

    writeNative(arrayStructs, "arrays", nativeDir, projectName, deleteStructs) { type -> type is ArrayType }
}

private fun writeImpl(structs: List<Struct>, deleteStructs: List<Struct>, nativeDir: File) {
    val template = config.getTemplate("impl.ftl")

    val outFile = File(nativeDir, "impl.c")

    outFile.bufferedWriter().use { writer ->
        template.process(
            mapOf(
                "includes" to structs,
                "structs" to deleteStructs,
                "StringType" to StringType::class.java,
                "StructType" to StructType::class.java,
                "PointerType" to PointerType::class.java,
                "UuidType" to UuidType::class.java
            ),
            writer
        )
    }
}

private fun writeJni(structs: List<Struct>, loadStructs: List<Struct>, projectName: String, fileName: String, jniNativeDir: File) {
    val template = config.getTemplate("jni.ftl")

    val outFile = File(jniNativeDir, "$fileName.h")

    outFile.bufferedWriter().use { writer ->
        template.process(
            mapOf(
                "structs" to structs,
                "loadStructs" to loadStructs,
                "projectName" to projectName,
                "fileName" to fileName
            ),
            writer
        )
    }
}

private fun writeJniImpl(struct: Struct, structs: List<Struct>, pkg: String, isInArray: Boolean, jniNativeDir: File) {
    val template = config.getTemplate("jni_impl.ftl")

    val outFile = File(jniNativeDir, "${struct.nativeName}_jni_impl.c")

    outFile.bufferedWriter().use { writer ->
        template.process(
            mapOf(
                "includes" to structs,
                "struct" to struct,
                "package" to pkg,
                "setter" to BaseMethod<Field> {
                    val skip = if (it.type === Primitive.BOOL && it.jvmName.startsWith("is")) {
                        2
                    } else {
                        0
                    }
                    val name = it.jvmName.substring(skip)
                    "set" + name[0].toUpperCase() + name.substring(1)
                },
                "getter" to BaseMethod<Field> {
                    val skip = if (it.type === Primitive.BOOL && it.jvmName.startsWith("is")) {
                        2
                    } else {
                        0
                    }
                    val name = it.jvmName.substring(skip)
                    (if (it.type === Primitive.BOOL) "is" else "get") +
                            name[0].toUpperCase() + name.substring(1)
                },
                "jvmType" to BaseMethod<Field> {
                    when (it.type) {
                        is ArrayType -> repeat("[", it.type.depth) + it.type.baseType.internalName
                        else -> it.type.internalName
                    }
                },
                "isInArray" to isInArray,
                "StringType" to StringType::class.java,
                "StructType" to StructType::class.java,
                "PrimitiveType" to Primitive::class.java,
                "UuidType" to UuidType::class.java
            ),
            writer
        )
    }
}

// Java 11 & the Kotlin stdlib conflict with String#repeat, so this is a way around that...
fun repeat(str: String, times: Int): String {
    val builder = StringBuilder()
    repeat(times) {
        builder.append(str)
    }
    return builder.toString()
}

private fun asPointer(type: Type) = PointerType(type)

private fun ArrayType.types(sequence: Sequence<ArrayType>): Sequence<ArrayType> {
    return sequence + sequenceOf(this) + when (type) {
        is ArrayType -> type.types(sequence)
        else -> sequenceOf()
    }
}

private fun getIncludes(structs: List<Struct>, filter: (Type) -> Boolean): MutableList<String> {
    return structs.asSequence()
        .flatMap { it.fields.asSequence() }
        .map { it.type }
        .filter(filter)
        .map {
            when (it) {
                is ArrayType -> it.baseType
                else -> it
            }
        }.filter { it is StructType }
        .map { it.baseNativeName + ".h" }
        .distinct()
        .sorted()
        .toMutableList()
}

private class BaseMethod<T>(private val func: (T) -> Any) : TemplateMethodModelEx {
    override fun exec(arguments: List<*>): Any {
        if (arguments.size != 1) {
            throw TemplateModelException()
        }

        @Suppress("UNCHECKED_CAST")
        return func((arguments[0] as BeanModel).wrappedObject as T)
    }
}
