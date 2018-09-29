package com.demonwav.wat

import com.demonwav.wat.grammar.WatLexer
import com.demonwav.wat.grammar.WatParser
import com.demonwav.wat.grammar.WatParserBaseVisitor
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.io.File

fun parse(file: File, pkg: String): List<Struct> {
    val stream = CharStreams.fromPath(file.toPath())
    val lexer = WatLexer(stream)
    val tokenStream = CommonTokenStream(lexer)
    val parser = WatParser(tokenStream)

    parser.addErrorListener(WatParserErrorListener)
    val visitor = WatVisitor(pkg)
    val result = visitor.visitFile(parser.file())

    for (struct in result) {
        if (struct.fields.any { it.type.jvmName == struct.jvmName }) {
            throw RuntimeException("Struct of type ${struct.jvmName} must not contain itself")
        }
    }

    return result
}

private class WatVisitor(private val pkg: String) : WatParserBaseVisitor<Any>() {
    override fun visitFile(ctx: WatParser.FileContext): List<Struct> {
        return ctx.struct().map { visitStruct(it) }
    }

    override fun visitDeclaration(ctx: WatParser.DeclarationContext): Field {
        return Field(ctx.name().text, visitType(ctx.type()))
    }

    override fun visitType(ctx: WatParser.TypeContext): Type {
        val baseType = when {
            ctx.WORD() != null -> {
                BaseStructType(ctx.WORD().text, pkg)
            }
            ctx.PRIMITIVE() != null -> {
                when (ctx.PRIMITIVE().text) {
                    "char" -> Primitive.CHAR
                    "boolean" -> Primitive.BOOL
                    "byte" -> Primitive.BYTE
                    "short" -> Primitive.SHORT
                    "int" -> Primitive.INT
                    "long" -> Primitive.LONG
                    "float" -> Primitive.FLOAT
                    "double" -> Primitive.DOUBLE
                    "String" -> StringType
                    "UUID" -> UuidType
                    else -> throw RuntimeException("Type ${ctx.PRIMITIVE().text} is not a valid primitive type")
                }
            }
            else -> throw RuntimeException("Type was not either a word or primitive")
        }

        return if (ctx.ARRAY().isEmpty()) {
            baseType
        } else {
            var type = baseType
            repeat(ctx.ARRAY().size) {
                type = ArrayType(type, pkg)
            }
            return type
        }
    }

    override fun visitStruct(ctx: WatParser.StructContext): Struct {
        return Struct(ctx.name().text, ctx.declaration().map { visitDeclaration(it) })
    }
}

private object WatParserErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        throw RuntimeException(msg, e)
    }
}
