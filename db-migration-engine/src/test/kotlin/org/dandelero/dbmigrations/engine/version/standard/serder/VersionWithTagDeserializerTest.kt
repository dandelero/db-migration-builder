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

import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.engine.version.alpha
import org.dandelero.dbmigrations.engine.version.beta
import org.dandelero.dbmigrations.engine.version.rc
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersion
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersionDeserializer
import org.dandelero.dbmigrations.engine.version.standard.PreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.VersionWithTag
import org.dandelero.dbmigrations.engine.version.standard.tag.DefaultPreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.tag.PreReleaseTagEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests for the [VersionWithTagDeserializer] implementation.
 * <br />
 * Created at: 1/11/19 5:00 pm
 * @author dandelero
 */
class VersionWithTagDeserializerTest {

    /**
     * The version prefix.
     */
    private val prefix = "r"

    /**
     * The version prefix separator.
     */
    private val prefixSeparator = ":"

    /**
     * The separator of the tag from the version.
     */
    private val tagSeparator = "-"

    /**
     * The digit separator string.
     */
    private val digitSeparator = "."

    /**
     * The tag sequence separator string.
     */
    private val tagSequenceSeparator = "_"

    /**
     * The number scheme deserializer instance.
     */
    private val numberSchemeDeserializer = FourDigitVersionDeserializer(digitSeparator = digitSeparator)

    /**
     * The version tag deserializer instance.
     */
    private val versionTagDeserializer = DefaultPreReleaseTagDeserializer(tagSequenceSeparator = tagSequenceSeparator)

    /**
     * The deserializer under test.
     */
    private val deserializer = VersionWithTagDeserializer(prefix = prefix, prefixSeparator = prefixSeparator,
            tagSeparator = tagSeparator,
            versionDeserializer = numberSchemeDeserializer, preReleaseTagDeserializer = versionTagDeserializer)

    /**
     * Tests the extreme casse where the same char is used for all delimiters/separators.
     */
    @Test
    fun serializerWithSameDelimitersTest() {
        val thisNumberSchemeDeserializer = FourDigitVersionDeserializer(digitSeparator = digitSeparator)
        val thisVersionTagDeserializer = DefaultPreReleaseTagDeserializer(tagSequenceSeparator = digitSeparator)
        val thisDeserializer = VersionWithTagDeserializer(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, versionDeserializer = thisNumberSchemeDeserializer,
                preReleaseTagDeserializer = thisVersionTagDeserializer)

        // v.1.2
        var currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2),
                tag = null)
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2"))

        // v.1.2.alpha.2
        currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2),
                tag = DefaultPreReleaseTag(PreReleaseTagEnum.ALPHA, 2, digitSeparator))
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2.alpha.2"))

        // v.1.2.3
        currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2, 3),
                tag = null)
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2.3"))

        // v.1.2.3.alpha.2
        currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2, 3),
                tag = DefaultPreReleaseTag(PreReleaseTagEnum.ALPHA, 2, digitSeparator))
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2.3.alpha.2"))

        // v.1.2.2.3
        currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2, 2, 3),
                tag = null)
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2.2.3"))

        // v.1.2.2.3.alpha.2
        currentValue = VersionWithTag(prefix = "v", prefixSeparator = digitSeparator,
                tagSeparator = digitSeparator, version = FourDigitVersion(digitSeparator, 1, 2, 2, 3),
                tag = DefaultPreReleaseTag(PreReleaseTagEnum.ALPHA, 2, digitSeparator))
        assertEquals(currentValue, thisDeserializer.deserialize("v.1.2.2.3.alpha.2"))
    }

    @Test
    fun simpleSerializationTest() {
        assertEquals(createScheme(FourDigitVersion(digitSeparator, 1, 2)),
                deserializer.deserialize("r:1.2"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 2, 0),
                alpha(1, "_")),
                deserializer.deserialize("r:2.0-alpha_1"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 1, 2, 3)),
                deserializer.deserialize("r:1.2.3"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 1, 2, 3, 4332)),
                deserializer.deserialize("r:1.2.3.4332"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 1, 2, 3, 4332),
                alpha(1092, tagSequenceSeparator)),
                deserializer.deserialize("r:1.2.3.4332-alpha_1092"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 1938, 2, 93, 4332),
                beta(12, tagSequenceSeparator)),
                deserializer.deserialize("r:1938.2.93.4332-beta_12"))

        assertEquals(createScheme(FourDigitVersion(digitSeparator, 9999, 9998, 9997, 9996),
                rc(9995, tagSequenceSeparator)),
                deserializer.deserialize("r:9999.9998.9997.9996-rc_9995"))
    }

    @Test
    fun invalidVersionsDeserializationTest() {
        // Invalid prefixes.
        assertNull(deserializer.deserialize("x:1.2.3.4332-alpha_1092"))
        assertNull(deserializer.deserialize("r-1.2.3.4332-alpha_1092"))
        assertNull(deserializer.deserialize("r1.2.3.4332-alpha_1092"))

        // Invalid suffixes
        assertNull(deserializer.deserialize("r:1.2.3.4332-alpha_1092 "))
        assertNull(deserializer.deserialize("r:1.2.3.4332-alpha_1092 3"))
        assertNull(deserializer.deserialize("r:1.2.3.4332-alpha_1092x"))

        // Invalid tag separator
        assertNull(deserializer.deserialize("r:1.2.3.4332alpha_1092"))
        assertNull(deserializer.deserialize("r:1.2.3.4332xalpha_1092"))
        assertNull(deserializer.deserialize("r:1.2.3.4332_alpha_1092"))
    }

    private fun createScheme(version: Version): VersionWithTag {
        return VersionWithTag(prefix = prefix, prefixSeparator = prefixSeparator,
                tagSeparator = tagSeparator, version = version)
    }

    private fun createScheme(version: Version, tag: PreReleaseTag): VersionWithTag {
        return VersionWithTag(prefix = prefix, prefixSeparator = prefixSeparator,
                tagSeparator = tagSeparator, version = version, tag = tag)
    }
}