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
package org.dandelero.dbmigrations.engine.version.semver1

import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.slf4j.LoggerFactory

/**
 * TODO: describe this file.
 * <br />
 * Created at: 20/11/19 10:47 pm
 * @author dandelero
 *
 * @param digitSeparator the digit separator string.
 * @param dateSeparator the date separator string.
 * @param dateFormatString the format of the date string.
 */
class Semver1VersionDeserializer(
    private val digitSeparator: String,
    private val dateSeparator: String,
    private val dateFormatString: String
) : VersionDeserializer {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(Semver1VersionDeserializer::class.java)

    /**
     * The regexp to match the version numbers.
     */
    private val pattern = Pattern.compile("(\\d+)\\$digitSeparator(\\d+)\\$digitSeparator(\\d+)\\$dateSeparator(.+)$")

    /**
     * The date formatter to parse date values.
     */
    private val dateFormat = DateTimeFormatter.ofPattern(dateFormatString)

    /**
     * Deserializes the given version string into a version object.
     * @param versionString the string representation of the version.
     * @return the reconstructed version or null.
     */
    override fun deserialize(versionString: String): Version? {
        val matcher = pattern.matcher(versionString)

        try {
            return if (matcher.find()) {
                val major = matcher.group(1).toIntOrNull()
                val minor = matcher.group(2).toIntOrNull()
                val patch = matcher.group(3).toIntOrNull()
                val dateString = matcher.group(4)

                if (major == null || minor == null || patch == null) {
                    null
                } else {
                    Semver1Version(digitSeparator = digitSeparator,
                            major = major,
                            minor = minor,
                            patch = patch,
                            dateSeparator = dateSeparator,
                            dateFormat = dateFormatString,
                            date = LocalDateTime.parse(dateString, dateFormat))
                }
            } else {
                null
            }
        } catch (e: DateTimeException) {
            logger.warn("Invalid date string in version: $versionString")
            return null
        }
    }
}