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
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriter
import org.dandelero.dbmigrations.api.migration.MigrationScriptWriterFactory
import org.dandelero.dbmigrations.api.module.Module
import org.dandelero.dbmigrations.api.version.Version
import org.junit.jupiter.api.Disabled

/**
 * A configurable [MigrationScriptWriterFactory] implementation that creates [MigrationScriptFileWriter] instances to
 * predefined files.
 * <br />
 * Created at: 7/11/19 7:32 am
 *
 * @param upgradeFile the upgrade file to be written to.
 * @param rollbackFile the rollback file to be written to.
 * @param deltaScriptTemplateLocator locates the templates to be applied when generating the change scripts.
 * @author dandelero
 */
@Disabled
class TestMigrationScriptWriterFactory(
    private val upgradeFile: File,
    private val rollbackFile: File,
    private val deltaScriptTemplateLocator: DeltaScriptTemplateLocator
) : MigrationScriptWriterFactory {

    /**
     * Creates a {@link MigrationScriptWriter} to output the upgrade SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module the module.
     * @param version the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    override fun createUpgradeScriptWriter(databaseEngine: String, module: Module, version: Version):
            MigrationScriptWriter {
        return MigrationScriptFileWriter(outputFile = upgradeFile,
                fileTemplate = deltaScriptTemplateLocator.findMigrationScriptFileTemplate(),
                regularScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine,
                        DeltaScriptCategory.UPGRADE),
                bidirectionalScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine,
                        DeltaScriptCategory.BIDIRECTIONAL)
        )
    }

    /**
     * Creates a {@link MigrationScriptWriter} to output the rollback SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module the module.
     * @param version the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    override fun createRollbackScriptWriter(databaseEngine: String, module: Module, version: Version):
            MigrationScriptWriter {
        return MigrationScriptFileWriter(outputFile = rollbackFile,
                fileTemplate = deltaScriptTemplateLocator.findMigrationScriptFileTemplate(),
                regularScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine,
                        DeltaScriptCategory.ROLLBACK),
                bidirectionalScriptTemplate = deltaScriptTemplateLocator.findDeltaScriptTemplate(databaseEngine,
                        DeltaScriptCategory.BIDIRECTIONAL)
        )
    }
}