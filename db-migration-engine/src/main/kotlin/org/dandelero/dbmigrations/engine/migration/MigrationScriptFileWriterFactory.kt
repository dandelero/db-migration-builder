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

import java.io.File
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriter
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriterFactory
import org.dandelero.dbmigrations.api.module.Module
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.engine.util.mkdir2

/**
 * A [MigrationScriptWriterFactory] implementation that creates instances that write to files.
 * <br />
 * Created at: 29/10/19 10:32 pm
 * @param baseOutputDirectory the base output directory to write scripts beneath.
 * @param deltaScriptTemplateLocator the template locator.
 * @author dandelero
 */
class MigrationScriptFileWriterFactory(
    private val baseOutputDirectory: File,
    private val deltaScriptTemplateLocator: DeltaScriptTemplateLocator
) : MigrationScriptWriterFactory {

    init {
        if (baseOutputDirectory.exists()) {
            if (!baseOutputDirectory.isDirectory) {
                throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails("No directory found at: ${baseOutputDirectory.absolutePath}"))
            }
        } else {
            if (!baseOutputDirectory.parentFile.exists()) {
                throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails(
                        "Parent directory of output directory does not exist: ${baseOutputDirectory.absolutePath}"))
            }
        }
    }

    /**
     * Creates a [MigrationScriptWriter] to output the upgrade SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module the module.
     * @param version the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    override fun createUpgradeScriptWriter(databaseEngine: String, module: Module, version: Version):
            MigrationScriptWriter {
        return createWriter(databaseEngine, module, version, DeltaScriptCategory.UPGRADE)
    }

    /**
     * Creates a [MigrationScriptWriter] to output the rollback SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module the module.
     * @param version the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    override fun createRollbackScriptWriter(databaseEngine: String, module: Module, version: Version):
            MigrationScriptWriter {
        return createWriter(databaseEngine, module, version, DeltaScriptCategory.ROLLBACK)
    }

    /**
     * Helper function that creates a writer for a specific (supported) script category.
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module the module.
     * @param version the version that will be processed with the created script writer.
     * @param scriptCategory the category of the writer to be generated.
     * @retrun the script writer instance.
     */
    private fun createWriter(
        databaseEngine: String,
        module: Module,
        version: Version,
        scriptCategory: DeltaScriptCategory
    ): MigrationScriptFileWriter {
        // Don't create an output directory for the default module
        val outputDirectory = if (module.isDefault) {
            baseOutputDirectory
        } else {
            File(baseOutputDirectory, module.name)
        }

        return with(File(outputDirectory, version.nameString).mkdir2()) {
            val outputFileName = scriptCategory.name.toLowerCase() + ".sql"
            MigrationScriptFileWriter(
                    outputFile = File(this, outputFileName),
                    fileTemplate = deltaScriptTemplateLocator.findMigrationScriptFileTemplate(),
                    regularScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine, scriptCategory),
                    bidirectionalScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine, DeltaScriptCategory.BIDIRECTIONAL)
            )
        }
    }
}