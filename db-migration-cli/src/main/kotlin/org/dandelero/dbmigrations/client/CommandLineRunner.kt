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

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.slf4j.LoggerFactory

/**
 * Launches the application from command-line, with all the necessary parameters.
 * <br />
 * Created at: 10/11/19 12:43 pm
 * @author dandelero
 * @param args the arguments from the command line.
 */
class CommandLineRunner(private val args: Array<String>) {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(CommandLineRunner::class.java)

    /**
     * The CLI arg parser.
     */
    private val parser = ArgParser("DB Script Manager CLI")

    /**
     * The input directory containing versions/modules.
     */
    private val inputDirectoryPath by parser.option(ArgType.String, shortName = "idp",
            fullName = "input-directory-path",
            description = "The path to the directory containing input data to be processed (parent of module or version directory)")
            .required()

    /**
     * The output directory to write to.
     */
    private val outputDirectoryPath by parser.option(ArgType.String, shortName = "odp",
            fullName = "output-directory-path",
            description = "The path to the directory containing where output (version and module) is to be written to")
            .required()

    /**
     * The database engine to produce migration scripts for.
     */
    private val databaseEngine by parser.option(ArgType.String, shortName = "d",
            fullName = "database-engine",
            description = "The name of the database engine to generate migration scripts for")
            .required()

    /**
     * The version scheme used to parse versions.
     */
    private val versionScheme by parser.option(ArgType.String, shortName = "vs",
            fullName = "version-scheme",
            description = "The version scheme to be used to process version directories")
            .required()

    /**
     * The path to the config file for the application.
     */
    private val configFilePath by parser.option(ArgType.String, shortName = "cf",
            fullName = "config-file",
            description = "The full path to the config file")
            .required()

    /**
     * The version to be processed.
     */
    private val versionToProcess by parser.option(ArgType.String, shortName = "v",
            fullName = "version",
            description = "The version to be processed; default is the latest version")

    /**
     * The modules to be processed.
     */
    private val modulesToProcessString by parser.option(ArgType.String, shortName = "m",
            fullName = "modules",
            description = "The module(s) whose version is to be processed, default = '' which indicates no module")

    /**
     * Runs the application.
     */
    fun run() {
        logger.info("Constructing the required services from the command-line args ...")

        // Ensure the needed args are provided.
        parser.parse(args)

        MigrationGenerator.generate(databaseEngine = databaseEngine,
                versionScheme = versionScheme,
                inputDirectoryPath = inputDirectoryPath,
                outputDirectoryPath = outputDirectoryPath,
                moduleListCsvString = modulesToProcessString,
                versionToProcess = versionToProcess,
                configFile = configFilePath)
    }
}