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
package org.dandelero.dbmigrations.engine.version.standard.serder

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersionDeserializer
import org.dandelero.dbmigrations.engine.version.standard.VersionWithTag
import org.slf4j.LoggerFactory

/**
 * Deserializes instances of [VersionWithTag] from their string representation.
 * <br />
 * Created at: 1/11/19 8:02 am
 * @param prefix an optional prefix for the serialized value.
 * @param prefixSeparator an optional separator between the [prefix] and subsequent values.
 * @param tagSeparator an optional separator between the serialized [version] and the [tag].
 * @param versionDeserializer the deserializer for the version component.
 * @param preReleaseTagDeserializer the deserializer for the tag component.
 * @author dandelero
 */
class VersionWithTagDeserializer(
    private val prefix: String,
    private val prefixSeparator: String,
    private val tagSeparator: String,
    private val versionDeserializer: VersionDeserializer,
    private val preReleaseTagDeserializer: PreReleaseTagDeserializer
) : VersionDeserializer {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(VersionWithTagDeserializer::class.java)

    init {
        if (prefix.isNullOrEmpty() && prefixSeparator.isNotEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("A prefix separator cannot exist without a prefix"))
        }
    }

    /**
     * Deserializes the given version string into a version object.
     * @param versionString the string representation of the version.
     * @return the reconstructed version or null.
     */
    override fun deserialize(versionString: String): Version? {
        if (versionString.isNullOrEmpty()) {
            return null
        }

        var versionToProcess = versionString

        if (prefix.isNotEmpty()) {
            if (!versionToProcess.startsWith("$prefix$prefixSeparator")) {
                logger.debug("'$versionString' does not start with the expected prefix '$prefix$prefixSeparator'")
                return null
            }
            versionToProcess = versionToProcess.substring(prefix.length + prefixSeparator.length)
        }

        // Now split up the number and tag.
        val version = versionDeserializer.deserialize(versionToProcess) ?: return null
        val versionAsString = version.toString()
        if (!versionToProcess.startsWith(versionAsString)) {
            logger.debug("'$versionString' does not start with '$versionAsString', ignoring")
            return null
        }
        versionToProcess = versionToProcess.substring(versionAsString.length)
        if (versionToProcess.isEmpty()) {
            // We only have a version, no tag.
            return VersionWithTag(prefix = prefix, prefixSeparator = prefixSeparator,
                    tagSeparator = tagSeparator, version = version,
                    tag = null)
        }

        // We should have a tag separator and tag.
        if (!versionToProcess.startsWith(tagSeparator)) {
            logger.debug("'$versionString' does use the tag separator: '$tagSeparator'")
            return null
        }
        versionToProcess = versionToProcess.substring(tagSeparator.length)

        val tag = preReleaseTagDeserializer.deserialize(versionToProcess)
        if (tag == null) {
            logger.debug("'$versionString' does not contain a valid tag")
            return tag
        }

        return VersionWithTag(prefix = prefix, prefixSeparator = prefixSeparator,
                tagSeparator = tagSeparator, version = version, tag = tag)
    }

    companion object {

        /**
         * The default prefix according to this scheme.
         */
        private const val defaultPrefix: String = ""

        /**
         * The default prefix separator separator according to this scheme.
         */
        private const val defaultPrefixSeparator: String = ""

        /**
         * The default digit separator according to this scheme.
         */
        private const val defaultDigitSeparator: String = "."

        /**
         * The default tag separator according to this scheme.
         */
        private const val defaultTagSeparator: String = "-"

        /**
         * The default tag and sequence number separator according to this scheme.
         */
        private const val defaultTagSequenceSeparator: String = "-"

        /**
         * Constructs a version serializer.
         * @param prefix the version prefix.
         * @param prefixSeparator the version prefix separator.
         * @param digitSeparator the digit separator string.
         * @param tagSeparator the separator of the tag from the version.
         * @param tagSequenceSeparator the tag sequence separator string.
         * @return the serializer instance.
         */
        fun createDeserializer(
            prefix: String? = null,
            prefixSeparator: String? = null,
            digitSeparator: String? = null,
            tagSeparator: String? = null,
            tagSequenceSeparator: String? = null
        ): VersionDeserializer {

            /**
             * The number version deserializer instance.
             */
            val numberVersionDeserializer = FourDigitVersionDeserializer(digitSeparator = digitSeparator ?: defaultDigitSeparator)

            /**
             * The pre release tag deserializer instance.
             */
            val preReleaseTagDeserializer = DefaultPreReleaseTagDeserializer(tagSequenceSeparator = tagSequenceSeparator ?: defaultTagSequenceSeparator)

            /**
             * The deserializer for our version structure.
             */
            return VersionWithTagDeserializer(prefix = prefix ?: defaultPrefix,
                    prefixSeparator = prefixSeparator ?: defaultPrefixSeparator,
                    tagSeparator = tagSeparator ?: defaultTagSeparator,
                    versionDeserializer = numberVersionDeserializer,
                    preReleaseTagDeserializer = preReleaseTagDeserializer)
        }
    }
}