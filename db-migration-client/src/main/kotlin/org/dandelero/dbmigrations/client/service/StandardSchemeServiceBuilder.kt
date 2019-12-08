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
import org.dandelero.dbmigrations.engine.version.standard.serder.VersionWithTagDeserializer

/**
 * Reads configuration from a map and constructs an instance of services
 * geared for processing versions adhering to the standard scheme.
 * <br />
 * Created at: 20/11/19 6:02 pm
 * @author dandelero
 */
class StandardSchemeServiceBuilder(
    private val versionSchemeConfig: Map<String, Any?>,
    databaseEngineConfig: Map<String, Any?>,
    generalConfig: Map<String, Any?>
) : CommonVersionSchemeServiceBuilder(databaseEngineConfig, generalConfig) {

    /**
     * @return a [DirectoryVersionService] created from the configuration.
     */
    override fun createDirectoryVersionService(): DirectoryVersionService {
        return createStandardSchemeDirectoryVersionService(generalConfig = generalConfig, schemeConfig = versionSchemeConfig)
    }

    /**
     * Parses the standard scheme configuration to configure a [DirectoryVersionService].
     * @param generalConfig the general configuration settings.
     * @param schemeConfig the scheme configuration.
     * @return the service used to load version according to the configuration of the standard scheme.
     */
    private fun createStandardSchemeDirectoryVersionService(generalConfig: Map<String, Any?>, schemeConfig: Map<String, Any?>): DirectoryVersionService {
        val prefix = schemeConfig.getRequiredString("prefix")
        val prefixSeparator = schemeConfig.getRequiredString("prefix-separator")
        val digitSeparator = schemeConfig.getRequiredString("digit-separator")
        val tagSeparator = schemeConfig.getRequiredString("tag-separator")
        val tagSequenceSeparator = schemeConfig.getRequiredString("tag-sequence-separator")
        val ignoreInvalidVersions = generalConfig.getRequiredBoolean("ignore-invalid-versions")

        val versionDeserializer = VersionWithTagDeserializer.createDeserializer(
                prefix = prefix, prefixSeparator = prefixSeparator,
                digitSeparator = digitSeparator, tagSeparator = tagSeparator,
                tagSequenceSeparator = tagSequenceSeparator)

        val versionServiceSettings = VersionServiceSettings(ignoreInvalidVersions = ignoreInvalidVersions)

        return DirectoryVersionService(settings = versionServiceSettings,
                deserializer = versionDeserializer)
    }
}