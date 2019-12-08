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
package org.dandelero.dbmigrations.engine.delta

import java.io.File
import java.util.regex.Pattern
import org.slf4j.LoggerFactory

/**
 * A [DeltaScriptFileBuilder] instance that constructs [DeltaScriptFile] instances using the default file naming
 * scheme.
 * <br />
 * e.g. 0003-create-table.sql
 * <br />
 * Created at: 5/11/19 10:06 pm
 * @author dandelero
 */
class DefaultDeltaScriptFileBuilder : DeltaScriptFileBuilder {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(DefaultDeltaScriptFileBuilder::class.java)

    /**
     * The file name pattern to be used. e.g. 0002-something.sql
     */
    private val pattern = Pattern.compile("^(\\d+)-(.+)\$")

    /**
     * Constructs a [DeltaScriptFile] instance of the given file.
     * @param file the file to be adapted into a change script.
     * @return the change script instance; null if the file cannot be adapted.
     */
    override fun build(file: File): DeltaScriptFile? {
        // Now create matcher object.
        val matcher = pattern.matcher(file.name)

        return if (matcher.find()) {
            val sequenceNumber = matcher.group(1).toIntOrNull()
            if (sequenceNumber == null || sequenceNumber <= 0) {
                logger.warn("Invalid file name format: ${file.name}")
                null
            } else {
                DeltaScriptFile(file, sequenceNumber)
            }
        } else {
            null
        }
    }
}