import com.demonwav.wat.GenerateHeaderTask
import com.demonwav.wat.GenerateTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import java.nio.file.Files
import java.nio.file.Paths
import org.gradle.internal.os.OperatingSystem
import org.gradle.plugins.ide.idea.model.Jdk

plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

val classes by tasks.existing()
val jar by tasks.existing()
val compileJava by tasks.existing<JavaCompile>()
val clean by tasks.existing<Delete>()
val shadowJar by tasks.existing<ShadowJar>()
val build by tasks.existing()
val processResources by tasks.existing<ProcessResources>()

val nativeClassName = "com.demonwav.wat.bind.NativeInit"

configurations {
    register("lombok")
}

sourceSets {
    create("gen") {
        configurations[compileOnlyConfigurationName].extendsFrom(configurations["lombok"])
        configurations[annotationProcessorConfigurationName].extendsFrom(configurations["lombok"])

        of("main") {
            compileClasspath += this@create.output
        }

        of("test") {
            compileClasspath += this@create.output
        }
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    compileOnly("org.bukkit:bukkit:1.13.1-R0.1-SNAPSHOT")
    "lombok"("org.projectlombok:lombok:1.18.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

compileJava {
    // javac handles this now instead of javah
    // Because this happens now, callCmake has to depend on this task
    // don't do this in the above tasks.withType<JavaCompile> because that includes tests
    val nativeIncludes = file("native/include/jni").absolutePath

    doFirst {
        options.compilerArgs = options.compilerArgs + listOf("-h", nativeIncludes)
        println("Generating headers to: $nativeIncludes")
    }
}

idea {
    module {
        generatedSourceDirs.add(file("gen"))
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val generateStructs = tasks.register<GenerateTask>("generateStructs") {
    inputs.file("structs.txt")

    javaOutputDir = file("gen")
    nativeOutputDir = file("native/include")

    packageName = "com.demonwav.wat.bind.struct"
}

sourceSets.of("gen") { java.srcDir(generateStructs) }

fun createCmakeTask(name: String, debug: Boolean = false) = tasks.register<Exec>(osify(name)) {
    val rootDir = "native"
    val buildDir = if (debug) "$rootDir/build-dbg" else "$rootDir/build"

    setupEnv(environment)

    commandLine = if (OperatingSystem.current().isWindows) {
        listOf("cmake", "-GVisual Studio 15 2017 Win64", "..")
    } else {
        val type = if (debug) "Debug" else "Release"
        listOf("cmake", "-DCMAKE_BUILD_TYPE=$type", "..")
    }
    workingDir = file(buildDir)

    doFirst {
        // clean
        mkdir(buildDir)
    }

    // Don't know of any real way to make this cacheable
    // But that's okay, CMake will do incremental updates if it already exists
    inputs.files("$rootDir/CMakeLists.txt")

    dependsOn(compileJava, generateStructs)

    doFirst {
        println("Calling command: ${commandLine.joinToString(" ") { "'$it'" }}")
    }
}

fun createGenerateHeaderTask(name: String, debug: Boolean = false, configure: GenerateHeaderTask.() -> Unit) =
    tasks.register<GenerateHeaderTask>(name) {
        headerDirs = project.files("native/src", "native/include")
        outputFile = if (debug) {
            project.file("native/build-dbg/wat.h")
        } else {
            project.file("native/build/wat.h")
        }
        val inputHeaders = headerDirs.asSequence()
                .flatMap { it -> it.walk(FileWalkDirection.TOP_DOWN) }
                .filter { it.name.endsWith(".h") }
                .toList()
        inputs.files(inputHeaders)
        outputs.file(outputFile)

        configure()
    }

fun createBuildNativeTask(name: String, debug: Boolean = false, configure: Exec.() -> Unit) =
    tasks.register<Exec>(osify(name)) {
        val rootDir = "native"
        val buildDir = if (debug) "$rootDir/build-dbg" else "$rootDir/build"

        setupEnv(environment)

        val allFiles = project.fileTree("$rootDir/wat/src").asSequence() + project.fileTree("$rootDir/wat/include")
        val inputFiles = allFiles.filter { it.name.endsWith(".h") || it.name.endsWith(".c") }.toList()

        inputs.files(inputFiles)

        commandLine = if (OperatingSystem.current().isWindows) {
            outputs.file("$buildDir/Release/${project.name}.dll")
            val type = if (debug) "Debug" else "Release"
            listOf("cmd", "/C", "MSBuild.exe", "wat.sln", "/t:Rebuild", "/p:Configuration=$type", "/m", "/clp:ForceConsoleColor")
        } else {
            if (OperatingSystem.current().isMacOsX) {
                outputs.file("$buildDir/lib${project.name}.dylib")
            } else {
                outputs.file("$buildDir/lib${project.name}.so")
            }
            val jCount = Runtime.getRuntime().availableProcessors() + 1
            listOf("make", "-j$jCount")
        }

        doFirst {
            println("Calling build command: $executable ${args!!.joinToString(" ") { "'$it'" }}")
        }

        workingDir = file(buildDir)

        configure()
    }

val callCmakeTask = createCmakeTask("callCmake")
val callCmakeDebugTask = createCmakeTask("callCmake-debug", true)

val generateHeaderTask = createGenerateHeaderTask("generateHeader") { dependsOn(callCmakeTask) }
val generateHeaderDebugTask = createGenerateHeaderTask("generateHeader-debug", true) { dependsOn(callCmakeDebugTask) }

val nativeTask = createBuildNativeTask("buildNative") { dependsOn(generateHeaderTask) }
val nativeDebugTask = createBuildNativeTask("buildNative-debug", true) { dependsOn(generateHeaderDebugTask) }

shadowJar {
    baseName = "wat"
    classifier = ""
    version = ""
    from(nativeTask, sourceSets["gen"].output)

    artifacts.add("archives", this)
}

val shadowJarDebug = tasks.register<ShadowJar>("shadowJarDebug") {
    archiveName = "wat-debug.jar"
    from(nativeDebugTask, sourceSets["gen"].output, sourceSets["main"].output)
    configurations = listOf(project.configurations["runtime"])
}

tasks.register("buildDebug") {
    group = "build"
    description = "Assembles and tests this project with debug native code."
    dependsOn(shadowJarDebug)
}

processResources {
    from(sourceSets["main"].resources.srcDirs) {
        filter(mapOf("tokens" to mapOf("version" to version)), ReplaceTokens::class.java)
    }
}

clean {
    delete(generateStructs, "native/build", "native/build-dbg", "native/include", "buildSrc/gen", "buildSrc/build")
}

tasks.withType<JavaCompile>().configureEach { dependsOn(generateStructs) }

fun setupEnv(env: MutableMap<Any?, Any?>) {
    val file = file("env.txt")
    if (!file.exists()) {
        return
    }
    for (line in file.readLines()) {
        val index = line.indexOf("=")
        val key = line.substring(0, index)
        val value = line.substring(index + 1)
        env[key] = value
    }
}

fun osify(name: String): String {
    val os = OperatingSystem.current()
    return "$name-" + when {
        os.isLinux -> "linux"
        os.isWindows -> "windows"
        os.isMacOsX -> "macos"
        else -> throw RuntimeException("Unsupported OS: $os")
    }
}

inline fun <reified T : Task> TaskContainer.existing() = existing(T::class)
inline fun <reified T : Task> TaskContainer.register(name: String, configuration: Action<in T>) = register(name, T::class, configuration)
inline fun <reified T> NamedDomainObjectContainer<T>.of(name: String, noinline configuration: T.() -> Unit) =
    named(name).apply {
        configure(configuration)
    }
