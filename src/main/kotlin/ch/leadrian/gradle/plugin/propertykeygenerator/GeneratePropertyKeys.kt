package ch.leadrian.gradle.plugin.propertykeygenerator

import ch.leadrian.gradle.plugin.propertykeygenerator.model.PropertyKey
import ch.leadrian.gradle.plugin.propertykeygenerator.model.PropertyKeyTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

open class GeneratePropertyKeys : DefaultTask(), PropertyKeyGenerationSpec {

    @get:Input
    override lateinit var bundleName: String

    @get:[Optional Input]
    override var className: String? = null

    @get:Input
    override var resourceBundleNameCaseFormat: Any = PropertyKeyGenerationSpec.DEFAULT_RESOURCE_BUNDLE_CASE_FORMAT

    @get:Input
    override lateinit var packageName: String

    @get:[Optional Input]
    override var pattern: String? = null

    @get:[Optional Nested]
    override var wrapperClass: WrapperClassConfiguration? = null

    @get:Input
    override var pathVariableName: String = PropertyKeyGenerationSpec.DEFAULT_PATH_VARIABLE_NAME

    @get:OutputDirectory
    internal val outputDirectory: File
        get() = project.buildDir.resolve(PropertyKeyGeneratorPlugin.GENERATED_SOURCE_DIRECTORY)

    @get:InputFiles
    internal val inputFiles: List<File>
        get() = PropertiesFilesResolver.resolve(ResourceDirectoriesResolver.resolve(project), this)

    @get:OutputFile
    internal val outputFile: File
        get() {
            return outputDirectory
                    .resolve(packageName.replace('.', File.separatorChar))
                    .resolve("${PropertyKeysClassNameResolver.resolve(this)}.java")
        }

    @TaskAction
    fun generatePropertyKeys() {
        outputFile.parentFile.mkdirs()
        val propertyKeyTree = buildPropertyKeyTree()
        BufferedWriter(FileWriter(outputFile, false)).use { writer ->
            PropertyKeyGenerator(this, propertyKeyTree).generatePropertyKeyRootClass(writer)
        }
    }

    private fun buildPropertyKeyTree(): PropertyKeyTree {
        val propertyKeys = inputFiles.map { it.loadProperties() }
                .flatMap { it.keys }
                .map { PropertyKey(it.toString()) }
                .toSet()
        return PropertyKeyTree.InternalNode().apply { putAll(propertyKeys) }
    }

    fun with(spec: PropertyKeyGenerationSpec) {
        bundleName = spec.bundleName
        className = spec.className
        resourceBundleNameCaseFormat = spec.resourceBundleNameCaseFormat
        packageName = spec.packageName
        pattern = spec.pattern
        wrapperClass = spec.wrapperClass
        pathVariableName = spec.pathVariableName
    }

}
