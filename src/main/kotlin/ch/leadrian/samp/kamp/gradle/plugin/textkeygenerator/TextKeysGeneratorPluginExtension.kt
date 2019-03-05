package ch.leadrian.samp.kamp.gradle.plugin.textkeygenerator

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

open class TextKeysGeneratorPluginExtension {

    var className: String = "TextKeys"

    var packageNames: Set<String> = HashSet()

    fun packageName(value: String) {
        packageNames += value
    }

    fun packageNames(vararg values: String) {
        packageNames += values
    }

    var charset: Charset = StandardCharsets.ISO_8859_1
}
