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

import org.dandelero.dbmigrations.engine.util.getRequiredBoolean
import org.dandelero.dbmigrations.engine.util.getRequiredString
import org.dandelero.dbmigrations.engine.version.DirectoryVersionService
import org.dandelero.dbmigrations.engine.version.VersionServiceSettings
import org.dandelero.dbmigrations.engine.version.semver1.Semver1VersionDeserializer

/**
 * Reads configuration from a map and constructs an instance of services geared for processing versions adhering to the semver1 scheme.
 * <br />
 * Created at: 20/11/19 6:02 pm
 * @author dandelero
 */
class Semver1SchemeServiceBuilder(
    private val versionSchemeConfig: Map<String, Any?>,
    databaseEngineConfig: Map<String, Any?>,
    generalConfig: Map<String, Any?>
) : CommonVersionSchemeServiceBuilder(databaseEngineConfig, generalConfig) {

    /**
     * @return a [DirectoryVersionService] created from the configuration.
     */
    override fun createDirectoryVersionService(): DirectoryVersionService {
        val digitSeparator = versionSchemeConfig.getRequiredString("digit-separator")
        val dateSeparator = versionSchemeConfig.getRequiredString("date-separator")
        val dateFormat = versionSchemeConfig.getRequiredString("date-format")
        val ignoreInvalidVersions = generalConfig.getRequiredBoolean("ignore-invalid-versions")

        val versionDeserializer = Semver1VersionDeserializer(
                digitSeparator = digitSeparator,
                dateSeparator = dateSeparator,
                dateFormatString = dateFormat
        )

        val versionServiceSettings = VersionServiceSettings(ignoreInvalidVersions = ignoreInvalidVersions)

        return DirectoryVersionService(settings = versionServiceSettings, deserializer = versionDeserializer)
    }
}