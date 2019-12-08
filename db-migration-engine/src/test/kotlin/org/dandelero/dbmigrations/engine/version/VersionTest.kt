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
package org.dandelero.dbmigrations.engine.version

import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersion
import org.dandelero.dbmigrations.engine.version.standard.PreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.VersionWithTag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A suite of tests around [org.dandelero.db.api.version.Version]s and associated classes.
 * <br />
 * Created at: 31/10/19 10:04 pm
 * @author dandelero
 */
class VersionTest {

    @Test
    fun versionComparisionTest() {
        val v1_0_0 = FourDigitVersion(separator = ".", major = 1, minor = 0, build = 0)
        val v1_0_1 = FourDigitVersion(separator = ".", major = 1, minor = 0, build = 1)
        val v1_0 = FourDigitVersion(separator = ".", major = 1, minor = 0)
        val v1_1 = FourDigitVersion(separator = ".", major = 1, minor = 1)
        assertEquals(-1, v1_0.compareTo(v1_1))
        assertEquals(-1, v1_0_0.compareTo(v1_0_1))
        assertEquals(-1, v1_0.compareTo(v1_0_1))
    }

    @Test
    fun versionTagTests() {
        assertEquals("alpha:1", alpha(1, ":").toString())
        assertEquals("beta:2", beta(2, ":").toString())
        assertEquals("rc:3", rc(3, ":").toString())
    }

    @Test
    fun schemaWithTagTests() {
        val numberScheme: Version = FourDigitVersion("?", 1, 2, 3, 402)
        val tag: PreReleaseTag = alpha(10938, ":")
        val schemeWithTag: Version = VersionWithTag.noPrefix(numberScheme, tag, "_")
        assertEquals("1?2?3?402_alpha:10938", schemeWithTag.toString(), "Incorrect toString() value")
    }
}