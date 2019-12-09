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
package org.dandelero.dbmigrations.client.service

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.client.YamlConfigReader
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StandardSchemeServiceBuilderTest {

    /**
     * The yaml config file containing our test config.
     */
    private val yamlConfigFile = TestUtil.getRequiredFileOnClasspath("input/conf/test-strict-config.yaml")

    /**
     * Tests construction of services from the default configuration.
     */
    @Test
    fun defaultVersionSchemeTest() {
        val (generalConfig, databaseEngineConfig, versionSchemeConfig) =
                YamlConfigReader.loadConfigFromFile(configFile = yamlConfigFile, databaseEngine = "mssql", versionSchemeName = "default")

        val service = StandardSchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
        service.loadMigrationScriptSettings()
        service.createDeltaScriptDirectoryService()
        service.createDeltaScriptTemplateLocator()
        service.createDirectoryVersionService()
    }

    /**
     * Tests construction of services from custom configuration.
     */
    @Test
    fun customVersionSchemeTest() {
        val (generalConfig, databaseEngineConfig, versionSchemeConfig) =
                YamlConfigReader.loadConfigFromFile(configFile = yamlConfigFile, databaseEngine = "mysql", versionSchemeName = "project-1")

        val service = StandardSchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
        service.loadMigrationScriptSettings()
        service.createDeltaScriptDirectoryService()
        service.createDeltaScriptTemplateLocator()
        service.createDirectoryVersionService()
    }

    /**
     * Tests construction of services from custom configuration.
     */
    @Test
    fun semver1VersionSchemeTest() {
        val (generalConfig, databaseEngineConfig, versionSchemeConfig) =
                YamlConfigReader.loadConfigFromFile(configFile = yamlConfigFile, databaseEngine = "mysql", versionSchemeName = "project-2")

        val service = Semver1SchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)

        service.loadMigrationScriptSettings()
        service.createDeltaScriptDirectoryService()
        service.createDeltaScriptTemplateLocator()
        service.createDirectoryVersionService()
    }

    /**
     * Tests construction of services from an invalid configuration.
     */
    @Test
    fun nonExistentVersionSchemeTest() {
        assertThrows(ApplicationException::class.java) {
            val (generalConfig, databaseEngineConfig, versionSchemeConfig) =
                    YamlConfigReader.loadConfigFromFile(configFile = yamlConfigFile, databaseEngine = "mysql", versionSchemeName = "something-funky???")
            StandardSchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
        }
    }

    /**
     * Tests construction of services from an database.
     */
    @Test
    fun nonExistentDatabaseTest() {
        assertThrows(ApplicationException::class.java) {
            val (generalConfig, databaseEngineConfig, versionSchemeConfig) =
                    YamlConfigReader.loadConfigFromFile(configFile = yamlConfigFile, databaseEngine = "f00", versionSchemeName = "test-standard")
            StandardSchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
        }
    }
}