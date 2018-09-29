package com.demonwav.wat

import com.google.common.graph.ElementOrder
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Traverser
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import kotlin.streams.asSequence

open class GenerateTask : DefaultTask() {

    init {
        group = "generate"
        description = "Generates structs and marshalling code for ${project.name}"
    }

    @get:Input
    lateinit var packageName: String

    @get:OutputDirectory
    lateinit var javaOutputDir: File

    lateinit var nativeOutputDir: File

    @TaskAction
    fun run() {
        for (file in inputs.files) {
            val structs = parse(file, packageName)

            write(structs, javaOutputDir, nativeOutputDir, packageName, project.name)
        }
    }
}

open class GenerateHeaderTask : DefaultTask() {

    private val includePattern = Regex("\\s*#include \"((?:[/\\w])+\\.h)\"\\s*")

    init {
        group = "generate"
        description = "Generates the client '${project.name}.h' header file"
    }

    lateinit var headerDirs: FileCollection

    lateinit var outputFile: File

    @TaskAction
    fun run() {
        val nodes = mutableMapOf<String, Node>()

        for (file in headerDirs) {
            val dirPath = file.toPath()
            Files.find(dirPath, 5, { path, _ -> path.fileName.toString().endsWith(".h") })
                .filter { path -> !path.fileName.toString().startsWith("jni") }
                .filter { path -> !path.fileName.toString().startsWith("com_demonwav") }
                .forEach { path ->
                    val deps = Files.lines(path).asSequence()
                        .mapNotNull { includePattern.matchEntire(it) }
                        .filter { matcher -> matcher.groups.size == 2 }
                        .mapNotNull { matcher -> matcher.groups[1] }
                        .map { groupMatcher ->
                            val value = groupMatcher.value
                            val depFile = path.parent.resolve(value)
                            if (Files.exists(depFile)) {
                                return@map dirPath.relativize(depFile).toString().replace('\\', '/')
                            }
                            return@map value
                        }
                        .toList()

                    val name = dirPath.relativize(path).toString().replace('\\', '/')
                    nodes += name to Node(name, deps)
                }
        }

        val graph = GraphBuilder.directed()
            .allowsSelfLoops(false)
            .nodeOrder(ElementOrder.unordered<Node>())
            .build<Node>()

        val startNodes = mutableListOf<Node>()
        nodes.forEach { _, node ->
            if (node.deps.isEmpty()) {
                startNodes += node
            }
            graph.addNode(node)
            for (dep in node.deps) {
                val depNode = nodes[dep]!!
                graph.putEdge(depNode, node)
            }
        }

        var firstText: String? = null
        val files = mutableListOf<String>()
        outer@for (node in Traverser.forGraph(graph).depthFirstPostOrder(startNodes).reversed()) {
            for (file in headerDirs) {
                val path = file.toPath().resolve(node.name)
                if (!Files.exists(path)) {
                    continue
                }

                val lines = Files.lines(path)
                val sb = StringBuilder("// ${node.name}")
                sb.appendln()
                var collect = false
                for (line in lines) {
                    if (line == "WAT_EXPORT_START") {
                        collect = true
                        continue
                    } else if (line == "WAT_EXPORT_END") {
                        collect = false
                        continue
                    }

                    if (collect) {
                        sb.appendln(line)
                    }
                }

                if (node.name == "wat.h") {
                    firstText = sb.toString()
                } else {
                    files += sb.toString()
                }
                continue@outer
            }

            throw Exception("Failed to find file for node: $node")
        }

        val config = Configuration(Configuration.VERSION_2_3_28)
        config.setClassForTemplateLoading(javaClass, "templates")
        config.defaultEncoding = "UTF-8"
        config.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        config.wrapUncheckedExceptions = true
        val template = config.getTemplate("header.ftl")

        outputFile.bufferedWriter().use { writer ->
            template.process(mapOf("files" to listOf(firstText, *files.toTypedArray())), writer)
        }
    }
}

data class Node(val name: String, val deps: List<String>)
