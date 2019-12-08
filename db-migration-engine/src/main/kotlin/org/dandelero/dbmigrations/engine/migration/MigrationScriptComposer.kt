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
package org.dandelero.dbmigrations.engine.migration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScript
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptService
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriter
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriterFactory
import org.dandelero.dbmigrations.api.module.Module
import org.dandelero.dbmigrations.api.module.ModuleService
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.api.version.VersionService
import org.slf4j.LoggerFactory

/**
 * Composes migration scripts that are used to advance or rollback a database version.
 * <br />
 * Created at: 28/10/19 9:19 pm
 * @author dandelero
 *
 * @param moduleService used to interact with modules.
 * @param versionService used to interact with versions within a module.
 * @param deltaScriptService used to locate delta scripts.
 * @param migrationScriptWriterFactory used to create [MigrationScriptWriter] instances.
 * @param migrationScriptSettings the settings for controlling the script composition.
 */
class MigrationScriptComposer<M : Module, V : Version>(
    private val moduleService: ModuleService<M>,
    private val versionService: VersionService<M, V>,
    private val deltaScriptService: DeltaScriptService<M, V>,
    private val migrationScriptWriterFactory: MigrationScriptWriterFactory,
    private val migrationScriptSettings: MigrationScriptSettings
) {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(MigrationScriptComposer::class.java)

    /**
     * The OS user that is currently running this script.
     */
    private val systemUser: String = System.getProperty("user.name")

    /**
     * Composes the migration script for the latest version in each module.
     * @param databaseEngine the database engine to create the migration script for.
     */
    fun composeMigrationScriptForLatestVersion(databaseEngine: String) {
        logger.info("Starting migration script composition for $databaseEngine")
        moduleService.listAllModules().forEach { composeMigrationScriptForLatestVersionInModule(databaseEngine, it) }
        logger.info("Migration script composition completed")
    }

    /**
     * As the name suggests this function composes the migration script for the latest
     * version in the specified module.
     * @param databaseEngine the database engine to create the migration script for.
     * @param moduleName the name of the module to be processed.
     */
    fun composeMigrationScriptForLatestVersionInModule(databaseEngine: String, moduleName: String) {
        val module: M? = moduleService.findModuleByName(moduleName)
        if (module == null) {
            logger.warn("No module found for name: $moduleName")
            return
        }

        val latestVersion: V? = versionService.latestVersion(module)
        if (latestVersion == null) {
            logger.debug("No version found for module: ${module.name}")
        } else {
            composeMigrationScriptForModuleVersion(databaseEngine, module, latestVersion)
        }
    }

    /**
     * As the name suggests this function composes the migration script for the latest
     * version in the specified module.
     * @param databaseEngine the database engine to create the migration script for.
     * @param module the module to be processed.
     */
    fun composeMigrationScriptForLatestVersionInModule(databaseEngine: String, module: M) {
        val latestVersion: V? = versionService.latestVersion(module)
        if (latestVersion == null) {
            logger.debug("No version found for module: ${module.name}")
        } else {
            composeMigrationScriptForModuleVersion(databaseEngine, module, latestVersion)
        }
    }

    /**
     * Composes the migration script for the given module and version, and writes the output to the script writer.
     * @param databaseEngine the database engine to create the migration script for.
     * @param module the module to be processed.
     * @param version the version to be processed.
     */
    fun composeMigrationScriptForModuleVersion(databaseEngine: String, module: M, version: V) {
        logger.info("Processing ${module.name}:${version.nameString} ...")

        logger.debug("Loading all the scripts for the version ...")
        val upgradeScripts: List<DeltaScript> = deltaScriptService.getScripts(DeltaScriptCategory.UPGRADE, module,
                version)
        val rollbackScripts: List<DeltaScript> = deltaScriptService.getScripts(DeltaScriptCategory.ROLLBACK, module,
                version)
        if (rollbackScripts.isNotEmpty() && upgradeScripts.size != rollbackScripts.size) {
            throw ApplicationException(ErrorCode.VERSION_DIRECTORY_ERROR.withDetails(
                    "The number of rollback scripts must equal the upgrade script count"))
        }

        val (bidirectionalUpgradeScripts, bidirectionalRollbackScripts) =
                getBidirectionalScripts(module, version)

        logger.debug("Composing the upgrade script using ${upgradeScripts.size} upgrade delta scripts " +
                "and ${bidirectionalUpgradeScripts.size} bidirectional scripts ...")
        with(migrationScriptWriterFactory.createUpgradeScriptWriter(databaseEngine, module, version)) {
            generateOverallScript(this, DeltaScriptCategory.UPGRADE, module, version, upgradeScripts,
                    bidirectionalUpgradeScripts, migrationScriptSettings.upgradeScriptOrder)
        }

        logger.debug("Composing the rollback script using ${rollbackScripts.size} upgrade delta scripts " +
                "and ${bidirectionalRollbackScripts.size} bidirectional scripts ...")
        with(migrationScriptWriterFactory.createRollbackScriptWriter(databaseEngine, module, version)) {
            generateOverallScript(this, DeltaScriptCategory.ROLLBACK, module, version, rollbackScripts,
                    bidirectionalRollbackScripts, migrationScriptSettings.rollbackScriptOrder)
        }
    }

    /**
     * Generates the script from the given list of delta and bidirectional scripts in the specified order, and
     * writes the output to the writer.
     * @param scriptWriter the script writer.
     * @param scriptCategory the category of scripts being processed.
     * @param module the module being processed.
     * @param version the version that is being processed.
     * @param scripts the delta scripts.
     * @param bidirectionalScripts the bidirectional scripts.
     * @param scriptOrder the order to apply the scripts in.
     */
    private fun generateOverallScript(
        scriptWriter: MigrationScriptWriter,
        scriptCategory: DeltaScriptCategory,
        module: M,
        version: V,
        scripts: List<DeltaScript>,
        bidirectionalScripts: List<DeltaScript>,
        scriptOrder: BidirectionalFilesOrder
    ) {
        if (scripts.isEmpty()) {
            logger.info("No scripts to be applied for category=$scriptCategory in module=${module.name}")
            return
        }

        // The overall file writer context containing general values about
        // the set of files that are to be processed.
        val writerContext: Map<String, Any> = mapOf(
                KEY_RELEASE_LABEL to version.nameString,
                KEY_CREATION_TIMESTAMP to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                KEY_SCRIPT_AUTHOR to systemUser,
                KEY_MODULE_NAME to module.name,
                KEY_FILE_TYPE to scriptCategory.name.toLowerCase(),
                KEY_SCRIPT_COUNT to "${scripts.size + bidirectionalScripts.size}",
                KEY_CHANGE_LOG_TABLE to migrationScriptSettings.dbChangeLogTableName,
                KEY_STMT_SEPARATOR to migrationScriptSettings.dbStatementSeparator,
                KEY_STMT_DELIMITER to migrationScriptSettings.dbStatementDelimiter
        )

        scriptWriter.setup(writerContext)

        if (scriptOrder == BidirectionalFilesOrder.FIRST) {
            writeBidirectionalScripts(scriptWriter, bidirectionalScripts)
            writeRegularScripts(scriptWriter, scripts)
        } else {
            writeRegularScripts(scriptWriter, scripts)
            writeBidirectionalScripts(scriptWriter, bidirectionalScripts)
        }
        scriptWriter.finish()
    }

    /**
     * Helper function to write the regular scripts out to a sink.
     * @param scriptWriter the writer to use for writing script output.
     * @param scripts the scripts to be written.
     */
    private fun writeRegularScripts(scriptWriter: MigrationScriptWriter, scripts: List<DeltaScript>) {
        scriptWriter.beginRegularScriptProcessing()

        scripts.forEachIndexed { index, script ->
            val scriptContents = script.contents
            val scriptFileName = script.name
            val scriptSequenceNumber = script.sequenceNumber
            val scriptContext = mapOf<String, Any>(
                    KEY_SCRIPT_FILE_NAME to scriptFileName,
                    KEY_SCRIPT_CONTENTS to scriptContents,
                    KEY_INDEX to index + 1,
                    KEY_SCRIPT_COUNT to scripts.size,
                    KEY_SCRIPT_SEQ_NUMBER to "$scriptSequenceNumber"
            )

            scriptWriter.writeRegularScript(scriptContext)
        }
        scriptWriter.finishRegularScriptProcessing()
    }

    /**
     * Helper function to write the bidirectional scripts out.
     * @param scriptWriter the writer to use for writing script output.
     * @param bidirectionalScripts the scripts to be written.
     */
    private fun writeBidirectionalScripts(scriptWriter: MigrationScriptWriter, bidirectionalScripts: List<DeltaScript>) {
        if (bidirectionalScripts.isEmpty()) {
            return
        }

        scriptWriter.beginBidirectionalScriptProcessing()

        bidirectionalScripts.forEachIndexed { index, bs ->
            val scriptFileName = bs.name
            val scriptContents = bs.contents
            val scriptSequenceNumber = bs.sequenceNumber
            val scriptContext: Map<String, Any> = mapOf(
                    KEY_SCRIPT_FILE_NAME to scriptFileName,
                    KEY_SCRIPT_CONTENTS to scriptContents,
                    KEY_INDEX to index + 1,
                    KEY_SCRIPT_COUNT to bidirectionalScripts.size,
                    KEY_SCRIPT_SEQ_NUMBER to "$scriptSequenceNumber")
            scriptWriter.writeBidirectionalScript(scriptContext)
        }

        scriptWriter.finishBidirectionalScripts()
    }

    /**
     * Applies the script ordering settings to the list of bidirectional files.
     * @param module the module service.
     * @param version the version whose bidirectional scripts are to be retrieved.
     * @return a pair containing the bidirectional scripts to be applied for both upgrade and rollback operations.
     */
    private fun getBidirectionalScripts(module: M, version: V): Pair<List<DeltaScript>, List<DeltaScript>> {
        // Get the bidirectional scripts to be applied, returning an empty list if the files are not to be applied.
        val allBidirectionalScripts: List<DeltaScript> = deltaScriptService.getScripts(
                DeltaScriptCategory.BIDIRECTIONAL, module, version)

        /**
         * Inner function to filter the bidirectional scripts according to the settings.
         * @param scriptOrder the order of the bidirectional scripts.
         * @return the list of bidirectional scripts to be applied.
         */
        fun filterScripts(scriptOrder: BidirectionalFilesOrder): List<DeltaScript> {
            return with(allBidirectionalScripts) {
                when (scriptOrder) {
                    BidirectionalFilesOrder.FIRST, BidirectionalFilesOrder.LAST -> {
                        this
                    }
                    else -> {
                        logger.debug("Filtering out the bidirectional scripts")
                        emptyList()
                    }
                }
            }
        }
        return Pair(
                filterScripts(migrationScriptSettings.upgradeScriptOrder),
                filterScripts(migrationScriptSettings.rollbackScriptOrder))
    }
}