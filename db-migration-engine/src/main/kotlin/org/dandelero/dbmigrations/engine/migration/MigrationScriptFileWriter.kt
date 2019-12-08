/*
 * Copyright 2019 Dandelero (dandelero@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.dandelero.dbmigrations.engine.migration

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriter
import org.dandelero.dbmigrations.engine.util.substitutePlaceholders

/**
 * A [MigrationScriptWriter] implementation that writes output to a file.
 * <br />
 * Created at: 29/10/19 9:33 pm
 * @param outputFile the output file to write to.
 * @param fileTemplate the template for composing the overall migration script.
 * @param regularScriptTemplate the template for composing composing individual scripts.
 * @param bidirectionalScriptTemplate the template for composing bidiretional script output.
 * @author dandelero
 */
class MigrationScriptFileWriter(
    private val outputFile: File,
    private val fileTemplate: String,
    private val regularScriptTemplate: String,
    private val bidirectionalScriptTemplate: String
) : MigrationScriptWriter {

    /**
     * Newline value, duh!
     */
    private val newline = "\n"

    /**
     * The output stream currently being used.
     */
    private lateinit var outputStream: OutputStream

    /**
     * The context for writing the file output.
     */
    private lateinit var writerContext: Map<String, Any>

    /**
     * Called when a new set of scripts is about to be processed.
     *
     * @param writerContext contains information about the scripts.
     */
    override fun setup(writerContext: Map<String, Any>) {
        this.outputStream = BufferedOutputStream(FileOutputStream(outputFile))
        this.writerContext = writerContext // Save the file context for use throughout.
        outputStream.write(fileTemplate.substitutePlaceholders(writerContext).toByteArray())
    }

    /**
     * Called when processing is complete and output ought to be flushed.
     */
    override fun finish() {
        // We don't want to catch exceptions - let the client deal with them.
        outputStream.flush()
        outputStream.close()
    }

    /**
     * Called when script processing is about to commence.
     */
    override fun beginRegularScriptProcessing() {
    }

    /**
     * Called when all scripts have been processed and output ought to be flushed.
     */
    override fun finishRegularScriptProcessing() {
    }

    /**
     * Called when processing of bidirectional scripts is about to commence.
     */
    override fun beginBidirectionalScriptProcessing() {
    }

    /**
     * Called when all bidirectional scripts have been processed.
     */
    override fun finishBidirectionalScripts() {
    }

    /**
     * Called when a regular script is to be written.
     *
     * @param scriptContext the script context.
     */
    override fun writeRegularScript(scriptContext: Map<String, Any>) {
        val placeholderValue = scriptContext.getOrDefault(KEY_SCRIPT_CONTENTS, "") as String
        // Apply whitespace prefixes across the script contents to ensure indentation is correct.
        val adjustedScriptContents = applyWhitespacePrefixForLinesOfPlaceholderValue(template = regularScriptTemplate,
                placeholderName = KEY_SCRIPT_CONTENTS,
                placeholderValue = placeholderValue)
        outputStream.write(regularScriptTemplate.substitutePlaceholders(writerContext + scriptContext +
                mapOf(KEY_SCRIPT_CONTENTS to adjustedScriptContents)
        ).toByteArray())
        outputStream.flush()
    }

    /**
     * Called when a bidirectional script is to be written.
     *
     * @param scriptContext the script context.
     */
    override fun writeBidirectionalScript(scriptContext: Map<String, Any>) {
        val placeholderValue = scriptContext.getOrDefault(KEY_SCRIPT_CONTENTS, "") as String
        // Apply whitespace prefixes across the script contents to ensure indentation is correct.
        val adjustedScriptContents = applyWhitespacePrefixForLinesOfPlaceholderValue(template = bidirectionalScriptTemplate,
                placeholderName = KEY_SCRIPT_CONTENTS,
                placeholderValue = placeholderValue)

        outputStream.write(bidirectionalScriptTemplate.substitutePlaceholders(writerContext + scriptContext +
                mapOf(KEY_SCRIPT_CONTENTS to adjustedScriptContents)).toByteArray())
        outputStream.flush()
    }

    /**
     * Prefixes the placeholder value with the whitespace chars that prefix the placeholder name in the template, to ensure that all lines in the substituted
     * placeholder value have the same indentation as specified in the template.
     * <br /> This is used to ensure we get the same whitespace prefix across all lines that form the placeholder value.
     * @param template the template.
     * @param placeholderName the name of the placeholder value to check the whitespace prefix/indentation of.
     * @param placeholderValue the placeholder value that is to be eventually substituted instead of the placeholder name in the template.
     * @return the adjusted placeholder value with the relevant whitespace prefix.
     */
    private fun applyWhitespacePrefixForLinesOfPlaceholderValue(template: String, placeholderName: String, placeholderValue: String): String {
        val prefix = findWhitepsacePrefixInTemplate(template, "$\\{$placeholderName\\}".replace("\\", ""))
        return placeholderValue.split(newline).mapIndexed { i, s ->
            if (i == 0) {
                // Don't apply at index = 0 because it will obtain the indentation from the template when substitution occurs.
                s
            } else {
                "$prefix$s"
            }
        }.joinToString(newline)
    }

    /**
     * Find the whitespace string that prefixes the given placeholder value in the template.
     * @param template the template as a string.
     * @param placeholder the placeholder to be searched for.
     * @return the whitespace prefix that comes before the given placeholder in the template.
     */
    private fun findWhitepsacePrefixInTemplate(template: String, placeholder: String): String {
        val linesWithPlaceholder = template.split(newline).filter { it.contains(placeholder) }

        if (linesWithPlaceholder.size != 1) {
            return ""
        }
        val lineWithPlaceholder = linesWithPlaceholder.first()
        val prefixingWhitespaceCount = lineWithPlaceholder.takeWhile { it.isWhitespace() }.count()

        return if (prefixingWhitespaceCount == 0) {
            ""
        } else {
            lineWithPlaceholder.substring(0, prefixingWhitespaceCount)
        }
    }
}