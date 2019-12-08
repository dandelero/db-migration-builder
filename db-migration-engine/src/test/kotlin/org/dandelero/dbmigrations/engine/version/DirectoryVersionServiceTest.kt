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

import org.dandelero.dbmigrations.engine.module.NoModuleDirectoryModuleService
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersionDeserializer
import org.dandelero.dbmigrations.engine.version.standard.serder.DefaultPreReleaseTagDeserializer
import org.dandelero.dbmigrations.engine.version.standard.serder.VersionWithTagDeserializer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests for [DirectoryVersionService]
 * <br />
 * Created at: 12/11/19 7:18 pm
 * @author dandelero
 */
class DirectoryVersionServiceTest {

    /**
     * The number version deserializer instance.
     */
    private val numberVersionDeserializer = FourDigitVersionDeserializer(digitSeparator = ".")

    /**
     * The pre release tag deserializer instance.
     */
    private val preReleaseTagDeserializer = DefaultPreReleaseTagDeserializer(tagSequenceSeparator = "-")

    /**
     * The deserializer for our version structure.
     */
    private val deserializer = VersionWithTagDeserializer(prefix = "", prefixSeparator = "",
            tagSeparator = "-", versionDeserializer = numberVersionDeserializer,
            preReleaseTagDeserializer = preReleaseTagDeserializer)

    @Test
    fun invalidVersionTest() {
        val versionService = DirectoryVersionService(VersionServiceSettings(ignoreInvalidVersions = false),
                deserializer)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath(
                "input/invalid-tests/no-modules")
        val moduleService = NoModuleDirectoryModuleService(baseDirectory = directoryWithTestData)
        val defaultModule = moduleService.findModuleByName("")
        assertNotNull(defaultModule, "Default module not found")
        assertNull(versionService.getVersion(defaultModule!!, "1.0.9.2-pre-3"),
                "Invalid version should not be parsed")
    }
}