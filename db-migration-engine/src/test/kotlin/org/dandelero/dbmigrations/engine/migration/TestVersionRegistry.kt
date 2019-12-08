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

import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.dandelero.dbmigrations.engine.version.semver1.Semver1VersionDeserializer
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersionDeserializer
import org.dandelero.dbmigrations.engine.version.standard.serder.DefaultPreReleaseTagDeserializer
import org.dandelero.dbmigrations.engine.version.standard.serder.VersionWithTagDeserializer

/**
 * A set of deserializers to be used for test purposes only.
 * <br />
 * Created at: 22/11/19 7:54 am
 * @author dandelero
 */
object TestVersionRegistry {

    /**
     * @return the deserializer for our version structure.
     */
    fun createDeserializer1(): VersionDeserializer {
        // The version prefix.
        val prefix = "r"

        // The version prefix separator.
        val prefixSeparator = ""

        // The separator of the tag from the version.
        val tagSeparator = "-"

        // The digit separator string.
        val digitSeparator = "."

        // The tag sequence separator string.
        val tagSequenceSeparator = "-"

        // The number version deserializer instance.
        val numberVersionDeserializer = FourDigitVersionDeserializer(digitSeparator = digitSeparator)

        // The pre release tag deserializer instance.
        val preReleaseTagDeserializer = DefaultPreReleaseTagDeserializer(
                tagSequenceSeparator = tagSequenceSeparator)

        return VersionWithTagDeserializer(prefix = prefix, prefixSeparator = prefixSeparator,
                tagSeparator = tagSeparator, versionDeserializer = numberVersionDeserializer,
                preReleaseTagDeserializer = preReleaseTagDeserializer)
    }

    /**
     * @return the deserializer for our version structure.
     */
    fun createDeserializer2(): VersionDeserializer {
        return Semver1VersionDeserializer(digitSeparator = ".", dateSeparator = "+",
                dateFormatString = "yyyyMMddHHmmss")
    }
}