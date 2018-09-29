package com.demonwav.wat

sealed class Type(val jvmName: String, val baseNativeName: String, val internalName: String) {
    constructor(name: String, pkg: String) : this(name, toNativeName(name), "L" + pkg.replace('.', '/') + "/" + name + ";")

    open val nativeName = baseNativeName

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Type

        if (jvmName != other.jvmName) return false
        if (baseNativeName != other.baseNativeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jvmName.hashCode()
        result = 31 * result + baseNativeName.hashCode()
        return result
    }
}

sealed class StructType : Type {
    constructor(jvmName: String, nativeName: String, pkg: String) : super(jvmName, nativeName, pkg)
    constructor(name: String, pkg: String) : super(name, pkg)

    override val nativeName: String
        get() = "struct $baseNativeName *"
}

class BaseStructType(name: String, pkg: String) : StructType(name, pkg)

class ArrayType(val type: Type, pkg: String) : StructType(type.jvmName + "[]", toArray(type), pkg) {
    val depth: Int
        get() {
            var depth = 1
            var t = type
            while (t is ArrayType) {
                depth++
                t = t.type
            }
            return depth
        }

    val baseType: Type
        get() {
            var t = type
            while (t is ArrayType) {
                t = t.type
            }
            return t
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArrayType) return false
        if (!super.equals(other)) return false

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

sealed class Primitive(jvmName: String, nativeName: String, internalName: String) : Type(jvmName, nativeName, internalName) {
    object CHAR : Primitive("char", "uint16_t", "C")
    object BOOL : Primitive("boolean", "bool", "Z")
    object BYTE : Primitive("byte", "int8_t", "B")
    object SHORT : Primitive("short", "int16_t", "S")
    object INT : Primitive("int", "int32_t", "I")
    object LONG : Primitive("long", "int64_t", "J")
    object FLOAT : Primitive("float", "float", "F")
    object DOUBLE : Primitive("double", "double", "D")
}

object StringType : Type("String", "char", "Ljava/lang/String;") {
    override val nativeName: String
        get() = "$baseNativeName *"
}

object UuidType : Type("java.util.UUID", "uuid", "Ljava/util/UUID;") {
    override val nativeName: String
        get() = "$baseNativeName *"
}

object NormalInt : Type("int", "int")

// Only a native type
class PointerType(val baseType: Type) : Type("", baseType.nativeName, "") {
    override val nativeName: String
        get() {
            return if (baseNativeName.endsWith("*")) {
                "$baseNativeName*"
            } else {
                "$baseNativeName *"
            }
        }
}

data class Struct(val jvmName: String, val nativeName: String, val fields: List<Field>) {
    constructor(name: String, fields: List<Field>) : this(name, toNativeName(name), fields)
}

data class Field(val jvmName: String, val nativeName: String, val type: Type) {
    constructor(name: String, type: Type) : this(name, toNativeName(name), type)
}

private fun toNativeName(name: String): String {
    val builder = StringBuilder()
    val index = if (name.startsWith("Struct")) {
        6
    } else {
        0
    }

    for (i in index until name.length) {
        val c = name[i]
        if (c.isUpperCase()) {
            builder.append('_').append(c.toLowerCase())
        } else {
            builder.append(c)
        }
    }

    if (builder.startsWith('_')) {
        builder.deleteCharAt(0)
    }

    return builder.toString()
}

private fun toArray(type: Type): String {
    return if (type is ArrayType) {
        val newDepth = type.depth + 1
        "${type.baseType.baseNativeName}_${newDepth}d_array"
    } else if (type is StringType) {
        "string_array"
    } else {
        "${type.baseNativeName}_array"
    }
}
