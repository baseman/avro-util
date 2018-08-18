package co.remotectrl.avro.util.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ConfigurationBuilder
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import org.reflections.util.ClasspathHelper
import org.reflections.util.FilterBuilder

@CacheableTask
open class AvroTask : SourceTask() {

    @Input
    var destinationDir: File = File("${this.project.buildDir}/outputs/avsc")

    @Input
    var packagePath: String? = null

    @OutputDirectory
    protected fun getOutputDirectory(): File {
        return destinationDir
    }

    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree {
        return super.getSource()
    }

    @TaskAction()
    fun <T> generate() {

        System.out.println("***Generating avro file...")

        val classLoader = URLClassLoader(this.getDirectoryUrls(), null)
        System.out.println("***retrieved class loader")

        val config = getConfig(classLoader)
        System.out.println("***retrieved source config")

        val reflections = Reflections(config)
        System.out.println("***retrieved reflections")



//        val aggregates = reflections.getStore().keySet()
        val aggregates = reflections.getSubTypesOf(Any::class.java)
//        val aggregates = reflections.getSubTypesOf(Object::class.java)
//        //todo: change String to Aggregate and PlayEvent<Aggregate>

        System.out.println("***aggregates count: [${aggregates.size}] for package: [$packagePath]")

        aggregates.forEach {

            System.out.println("***found subtype: [${it.toString()}]")
//            val avroStr = AvroSerialization.getAvro(it).toString(true)

//            File(destinationDir.absolutePath + it.getName() + ".avsc").printWriter().use { out ->
//                out.print(avroStr)
//            }
        }

        System.out.println("***Generated avro files.")

    }

    private fun getConfig(classLoader: ClassLoader): ConfigurationBuilder {
        val config = ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader(classLoader))
        //                .setScanners(SubTypesScanner(false), ResourcesScanner())

        if (!packagePath.isNullOrBlank()){
            System.out.println("***looking in package [$packagePath]")
            config.filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packagePath)))
        }

        config.addClassLoader(classLoader)
        config.setScanners(SubTypesScanner(), TypeAnnotationsScanner())

        return config
    }

    private fun getDirectoryUrls(): Array<URL> {

//        return this.classpath.getFiles().map{it.toURI().toURL()}.toTypedArray()

//        return arrayOf((this.project.properties["sourceSets"] as SourceSetContainer).getByName("main").getJava().getOutputDir().toURI().toURL())

//        .getOutput().getClassesDirs().iterator()
//        val sourceIterator = this.getSource().iterator()

//    val sources = (this.project.properties["sourceSets"] as SourceSetContainer).getByName("main").getJava().getOutputDir()
//    val ssc = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets

//    project.getConfigurations().getProperties().get("sourceSets").classes.sourceSets
//    project.sourceSets.main.output.classesDir.toUri().toUrl()

        val urls = mutableSetOf<URL>()

        getSource().forEach{
            val srcDirPath = getDirectoryPathFor(it)
            urls.add(URL(srcDirPath)) //todo: try urls.add(sourceFile.toURL())
            System.out.println("***added uri: [${srcDirPath}]")
        }

        return urls.toTypedArray()
    }

    private fun getDirectoryPathFor(it: File): String {
        val srcUrl = it.toURI().toURL().toString()
        return srcUrl.substring(0, srcUrl.lastIndexOf('/'))
    }
}