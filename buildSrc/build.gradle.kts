import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.2.61"
    `kotlin-dsl`
    idea
}

val clean by tasks.existing<Delete>()

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

val grammarPackage = "com.demonwav.wat.grammar"
val antlrSource = "src/main/antlr4"
val antlrDestinationDir = "gen"

configurations {
    register("antlr4Compile")
}

repositories {
    mavenCentral()
}

dependencies {
    val antlrVersion = "4.7.1"
    compile("org.antlr:antlr4-runtime:$antlrVersion")
    "antlr4Compile"("org.antlr:antlr4:$antlrVersion")
    compile("org.freemarker:freemarker:2.3.28")
    compile("com.google.guava:guava:26.0-jre")
}

val antlrOutputDir = tasks.register("antlrOutputDir") {
    mkdir(antlrDestinationDir)
}

val generateGrammarSource = tasks.register<JavaExec>("generateGrammarSource") {
    group = "antlr4"
    description = "Generates Java sources fom ANTLR4 grammars"
    dependsOn(antlrOutputDir)
    inputs.dir(antlrSource)
    outputs.dir(antlrDestinationDir)
    val grammars = fileTree(antlrSource).apply { include("**/*.g4") }.files.map(Any::toString).toTypedArray()
    main = "org.antlr.v4.Tool"
    classpath = configurations["antlr4Compile"]
    val pkg = grammarPackage.replace('.', '/')
    args = listOf("-o", "$antlrDestinationDir/$pkg", "-visitor", "-package", grammarPackage, *grammars)
}

sourceSets.of("main") {
    java.srcDirs(antlrSource, generateGrammarSource)
}

clean { delete(generateGrammarSource) }

idea {
    module {
        generatedSourceDirs.add(file("gen"))
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

inline fun <reified T : Task> TaskContainer.existing() = existing(T::class)
inline fun <reified T : Task> TaskContainer.register(name: String, configuration: Action<in T>) = register(name, T::class, configuration)
inline fun <reified T> NamedDomainObjectContainer<T>.of(name: String, noinline configuration: T.() -> Unit) =
    named(name).apply {
        configure(configuration)
    }
