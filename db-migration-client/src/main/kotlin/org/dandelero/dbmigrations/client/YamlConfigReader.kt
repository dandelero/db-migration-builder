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
package org.dandelero.dbmigrations.client

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.engine.util.getRequiredMap
import org.dandelero.dbmigrations.engine.util.getRequiredMapHierarchy
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import org.yaml.snakeyaml.Yaml

/**
 * Reads application configuration from a yaml file.
 * <br />
 * Created at: 20/11/19 6:02 pm
 * @author dandelero
 */
object YamlConfigReader {

    /**
     * Loads the configuration maps from the given yaml config file.
     * @param configFile the yaml config file.
     * @param databaseEngine the database engine.
     * @param versionSchemeName the name of the version scheme.
     * @return a triple containing the general, database and version scheme configuration respectively.
     */
    fun loadConfigFromFile(configFile: File, databaseEngine: String, versionSchemeName: String):
            Triple<Map<String, Any?>, Map<String, Any?>, Map<String, Any?>> {
        if (!configFile.exists() || !configFile.isFile) {
            throw ApplicationException(ErrorCode.RESOURCE_ERROR.withDetails("Configuration file does not exist: ${configFile.absolutePath}"))
        }

        return loadConfigFromStream(configFileStream = FileInputStream(configFile), databaseEngine = databaseEngine, versionSchemeName = versionSchemeName)
    }

    /**
     * Loads the configuration maps from the given yaml config file stream.
     * @param configFileStream the yaml config file stream.
     * @param databaseEngine the database engine.
     * @param versionSchemeName the name of the version scheme.
     * @return a triple containing the general, database and version scheme configuration respectively.
     */
    fun loadConfigFromStream(configFileStream: InputStream, databaseEngine: String, versionSchemeName: String):
            Triple<Map<String, Any?>, Map<String, Any?>, Map<String, Any?>> {

        /**
         * The configuration as a map.
         */
        val yamlConfigMap: Map<String, Any> = Yaml().load<Map<String, Any>>(configFileStream)

        /**
         * The database engine configuration.
         */
        val databaseEngineConfig = yamlConfigMap.getRequiredMapHierarchy(keyHierarchy = "database/engine/$databaseEngine")

        /**
         * General configuration.
         */
        val generalConfig = yamlConfigMap.getRequiredMap("general")

        /**
         * Version scheme configuration.
         */
        val versionSchemeConfig = yamlConfigMap.getRequiredMapHierarchy(keyHierarchy = "version-schemes/$versionSchemeName")

        return Triple(generalConfig, databaseEngineConfig, versionSchemeConfig)
    }
}