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
import org.dandelero.dbmigrations.client.service.Semver1SchemeServiceBuilder
import org.dandelero.dbmigrations.client.service.ServiceBuilder
import org.dandelero.dbmigrations.client.service.StandardSchemeServiceBuilder
import org.dandelero.dbmigrations.engine.migration.MigrationScriptComposer
import org.dandelero.dbmigrations.engine.migration.MigrationScriptFileWriterFactory
import org.dandelero.dbmigrations.engine.module.DirectoryModule
import org.dandelero.dbmigrations.engine.module.DirectoryModuleService
import org.dandelero.dbmigrations.engine.module.NoModuleDirectoryModuleService
import org.dandelero.dbmigrations.engine.version.DirectoryVersionService
import org.dandelero.dbmigrations.engine.version.VersionedDirectory
import java.io.File
import org.slf4j.LoggerFactory

/**
 * A factory for creating service instances that can be used by a variety of clients.
 * Created at: 24/11/19 11:31 pm
 * @author dandelero
 */
object ServiceBuilderFactory {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(ServiceBuilderFactory::class.java)

    /**
     * Builds the migration script composer for the given parameters.
     * @param versionService the version service.
     * @param serviceBuilder the helper service builder.
     * @param moduleService the service to locate modules that ought to be processed.
     * @param outputDirectoryPath the path to the output directory where migration scripts are to be written.
     * configuration.
     * @return the constructed composer.
     */
    fun createComposer(
        versionService: DirectoryVersionService,
        serviceBuilder: ServiceBuilder,
        moduleService: DirectoryModuleService,
        outputDirectoryPath: String
    ): MigrationScriptComposer<DirectoryModule, VersionedDirectory> {
        logger.debug("Constructing the required services from the command-line args ...")

        // Build services.
        val deltaScriptService = serviceBuilder.createDeltaScriptDirectoryService()
        val migrationScriptWriterFactory = buildMigrationScriptFileWriterFactory(
                serviceBuilder = serviceBuilder, outputDirectoryPath = outputDirectoryPath)
        val migrationScriptOrderSettings = serviceBuilder.loadMigrationScriptSettings()

        logger.debug("Services have been constructed")

        return MigrationScriptComposer(moduleService, versionService, deltaScriptService, migrationScriptWriterFactory, migrationScriptOrderSettings)
    }

    /**
     * Creates a [ServiceBuilder] instance for the given parameters.
     * @param databaseEngine the database to generate migration scripts for.
     * @param versionScheme the name of the versioning scheme to be used from the configuration.
     * @param configFile an optional configuration file to be used in preference over the default (bundled)
     * configuration.
     * @return the service builder instance.
     */
    fun createServiceBuilder(databaseEngine: String, versionScheme: String, configFile: String?): ServiceBuilder {
        logger.debug("Constructing the required services from the command-line args ...")
        return if (configFile.isNullOrEmpty()) {
            createDefaultServices(databaseEngine, versionScheme)
        } else {
            createServiceForSchemeName(configFile, databaseEngine, versionScheme)
        }
    }

    /**
     * Creates a [ServiceBuilder] using the default configuration bundled with the application.
     * @param databaseEngine the database engine that the service is to build migration scripts for.
     * @param versionSchemeName the version scheme to construct the service for.
     * @return the service instance.
     * @throws ApplicationException if the version scheme is not supported.
     */
    fun createDefaultServices(databaseEngine: String, versionSchemeName: String = "default-standard"): ServiceBuilder {
        return createServiceForSchemeNameAndConfigFileStream(configFileStreamPath = "conf/default-config.yaml",
                databaseEngine = databaseEngine, versionSchemeName = versionSchemeName)
    }

    /**
     * Creates a [ServiceBuilder] for the given parameters.
     * @param configFilePath the path to the config file.
     * @param databaseEngine the database engine that the service is to build migration scripts for.
     * @param versionSchemeName the version scheme to construct the service for.
     * @return the service instance.
     * @throws ApplicationException if the version scheme is not supported.
     */
    fun createServiceForSchemeName(configFilePath: String, databaseEngine: String, versionSchemeName: String):
            ServiceBuilder {
        // Construct the configuration from the parameters.
        val yamlConfig = YamlConfigReader.loadConfigFromFile(
                configFile = File(configFilePath), databaseEngine = databaseEngine, versionSchemeName = versionSchemeName)
        return createServiceBuilder(yamlConfig, versionSchemeName)
    }

    /**
     * Creates a [ServiceBuilder] for the given parameters.
     * @param configFilePath the path to the config file.
     * @param databaseEngine the database engine that the service is to build migration scripts for.
     * @param versionSchemeName the version scheme to construct the service for.
     * @return the service instance.
     * @throws ApplicationException if the version scheme is not supported.
     */
    fun createServiceForSchemeNameAndConfigFileStream(configFileStreamPath: String, databaseEngine: String, versionSchemeName: String): ServiceBuilder {
        val configStream = ServiceBuilderFactory::class.java.classLoader.getResourceAsStream(configFileStreamPath)
                ?: throw ApplicationException(ErrorCode.RESOURCE_ERROR.withDetails("Configuration stream (" +
                        "$configFileStreamPath) file was not bundled with the application"))

        // Construct the configuration from the parameters.
        val yamlConfig =
                YamlConfigReader.loadConfigFromStream(configFileStream = configStream, databaseEngine = databaseEngine,
                        versionSchemeName = versionSchemeName)

        return createServiceBuilder(yamlConfig, versionSchemeName)
    }

    /**
     * Creates a [DirectoryModuleService] that loads versions from the given directory path.
     * @param moduleList the list of modules to be processed.
     * @param inputDirectoryPath the path to the input directory.
     * @return a directory-based module retrieval service.
     */
    fun buildModuleService(moduleList: List<String>, inputDirectoryPath: String): DirectoryModuleService {
        return with(File(inputDirectoryPath)) {
            if (moduleList.isNullOrEmpty()) {
                logger.debug("Setting up a no-module directory module service")
                NoModuleDirectoryModuleService(this)
            } else {
                DirectoryModuleService(this)
            }
        }
    }

    /**
     * Creates a migration script file writer factor to write migration scripts to the specified directory.
     * @param serviceBuilder the service builder.
     * @param outputDirectoryPath the path to the output directory where migration scripts are to be written.
     * @return a migration script writer factory.
     */
    fun buildMigrationScriptFileWriterFactory(serviceBuilder: ServiceBuilder, outputDirectoryPath: String): MigrationScriptFileWriterFactory {
        val outputDirectory = File(outputDirectoryPath)
        return MigrationScriptFileWriterFactory(baseOutputDirectory = outputDirectory,
                deltaScriptTemplateLocator = serviceBuilder.createDeltaScriptTemplateLocator())
    }

    /**
     * Creates a [ServiceBuilder] instance from the given config.
     * @param yamlConfig the yaml configuration triple.
     * @param versionSchemeName the name of the version scheme to be used.
     * @return the service builder instance.
     */
    private fun createServiceBuilder(yamlConfig: Triple<Map<String, Any?>, Map<String, Any?>, Map<String, Any?>>, versionSchemeName: String): ServiceBuilder {
        // Construct the configuration from the parameters.
        val (generalConfig, databaseEngineConfig, versionSchemeConfig) = yamlConfig

        return when (versionSchemeConfig["scheme"]) {
            "standard" -> {
                StandardSchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
            }
            "semver1" -> {
                Semver1SchemeServiceBuilder(versionSchemeConfig, databaseEngineConfig, generalConfig)
            }
            else -> {
                throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("Unsupported version scheme: $versionSchemeName"))
            }
        }
    }
}