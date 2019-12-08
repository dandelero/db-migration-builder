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

import java.util.Arrays
import kotlin.collections.ArrayList
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.slf4j.LoggerFactory

/**
 * Helper functions to run migration script generation routines.
 * <br />
 * Created at: 28/11/19 6:58 am
 * @author dandelero
 */
object MigrationGenerator {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(MigrationGenerator::class.java)

    /**
     * Main function to generate migration scripts.
     * @param databaseEngine the database to generate migration scripts for.
     * @param versionScheme the name of the versioning scheme to be used from the configuration.
     * @param inputDirectoryPath the path to the input directory containing modules and/or versions to be processed.
     * @param outputDirectoryPath the path to the output directory where migration scripts are to be written.
     * @param moduleList the list of modules to be processed.
     * @param versionToProcess the version to generate the migration script for.
     * @param configFile an optional configuration file to be used in preference over the default (bundled)
     * configuration.
     */
    fun generate(
        databaseEngine: String,
        versionScheme: String,
        inputDirectoryPath: String,
        outputDirectoryPath: String,
        moduleList: List<String>,
        versionToProcess: String?,
        configFile: String?
    ) {
        val moduleService = ServiceBuilderFactory.buildModuleService(
                moduleList = moduleList, inputDirectoryPath = inputDirectoryPath)

        val serviceBuilder = ServiceBuilderFactory.createServiceBuilder(databaseEngine = databaseEngine,
                versionScheme = versionScheme, configFile = configFile)

        val versionService = serviceBuilder.createDirectoryVersionService()

        val composer = ServiceBuilderFactory.createComposer(
                serviceBuilder = serviceBuilder,
                versionService = versionService,
                moduleService = moduleService,
                outputDirectoryPath = outputDirectoryPath)

        if (versionToProcess == null || versionToProcess.isEmpty()) {
            if (moduleList.isEmpty()) {
                logger.debug("Processing the latest version of the default module")
                composer.composeMigrationScriptForLatestVersion(databaseEngine)
            } else {
                logger.debug("Processing the latest version of the module(s): $moduleList ")
                for (moduleName in moduleList) {
                    val module = moduleService.findModuleByName(moduleName) ?: throw ApplicationException(
                            ErrorCode.INVALID_MODULE.withDetails("No such module: $moduleName"))
                    composer.composeMigrationScriptForLatestVersionInModule(databaseEngine, module)
                }
            }
        } else {
            if (moduleList.isEmpty()) {
                logger.debug("Processing version ($versionToProcess) of the default module")
                val defaultModule = moduleService.findModuleByName("") ?: throw ApplicationException(
                        ErrorCode.INVALID_MODULE.withDetails("No default module found"))
                val version = versionService.getVersion(defaultModule, versionToProcess) ?: throw ApplicationException(
                        ErrorCode.INVALID_VERSION.withDetails("No such version:" + " \$versionToProcess in the default module"))
                composer.composeMigrationScriptForModuleVersion(databaseEngine, defaultModule, version)
            } else {
                logger.debug("Processing version: ($versionToProcess) of the module(s): $moduleList")
                for (moduleName in moduleList) {
                    val module = moduleService.findModuleByName(moduleName) ?: throw ApplicationException(
                            ErrorCode.INVALID_MODULE.withDetails("No such module: $moduleName"))
                    val version = versionService.getVersion(module, versionToProcess) ?: throw ApplicationException(
                            ErrorCode.INVALID_VERSION.withDetails("No such version: ($versionToProcess) in module: $moduleName"))
                    composer.composeMigrationScriptForModuleVersion(databaseEngine, module, version)
                }
            }
        }
    }

    /**
     * Main function to generate migration scripts.
     * @param databaseEngine the database to generate migration scripts for.
     * @param versionScheme the name of the versioning scheme to be used from the configuration.
     * @param inputDirectoryPath the path to the input directory containing modules and/or versions to be processed.
     * @param outputDirectoryPath the path to the output directory where migration scripts are to be written.
     * @param moduleListCsvString the list of modules (as a csv string) to be processed.
     * @param versionToProcess the version to generate the migration script for.
     * @param configFile an optional configuration file to be used in preference over the default (bundled)
     * configuration.
     */
    fun generate(
        databaseEngine: String,
        versionScheme: String,
        inputDirectoryPath: String,
        outputDirectoryPath: String,
        moduleListCsvString: String?,
        versionToProcess: String?,
        configFile: String?
    ) {
        val moduleParts = (moduleListCsvString
                ?: "").split(",".toRegex()).map { it.trim() }.dropLastWhile({ it.isEmpty() }).toTypedArray()
        val moduleNamesToProcess = ArrayList<String>()
        if (moduleParts.isNotEmpty()) {
            moduleNamesToProcess.addAll(Arrays.asList(*moduleParts))
        }

        generate(
                databaseEngine = databaseEngine,
                versionScheme = versionScheme,
                inputDirectoryPath = inputDirectoryPath,
                outputDirectoryPath = outputDirectoryPath,
                moduleList = moduleNamesToProcess,
                versionToProcess = versionToProcess,
                configFile = configFile
        )
    }
}