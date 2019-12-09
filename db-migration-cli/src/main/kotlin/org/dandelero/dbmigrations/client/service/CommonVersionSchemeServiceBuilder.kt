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

import java.io.File
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.DefaultDeltaScriptFileBuilder
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryService
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryServiceSettings
import org.dandelero.dbmigrations.engine.delta.template.ClasspathDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.template.DirectoryDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.template.PeckingOrderDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.migration.BidirectionalFilesOrder
import org.dandelero.dbmigrations.engine.migration.MigrationScriptSettings
import org.dandelero.dbmigrations.engine.util.getOptionalString
import org.dandelero.dbmigrations.engine.util.getRequiredBoolean
import org.dandelero.dbmigrations.engine.util.getRequiredString

/**
 * An implementation of [ServiceBuilder] that returns the common constructs and services between all version schemes.
 * <br />
 * Created at: 20/11/19 6:02 pm
 * @author dandelero
 */
abstract class CommonVersionSchemeServiceBuilder(private val databaseEngineConfig: Map<String, Any?>, val generalConfig: Map<String, Any?>) : ServiceBuilder {

    /**
     * @return a [DeltaScriptDirectoryService] created from the configuration.
     */
    override fun createDeltaScriptDirectoryService(): DeltaScriptDirectoryService {
        val rollbackScriptsOptional = generalConfig.getRequiredBoolean("rollback-scripts-optional")
        val deltaScriptExtension = generalConfig.getRequiredString("delta-script-extension")
        val upgradeDirectoryName = generalConfig.getRequiredString("upgrade-directory-name")
        val rollbackDirectoryName = generalConfig.getRequiredString("rollback-directory-name")
        val bidirectionalDirectoryName = generalConfig.getRequiredString("bidirectional-directory-name")

        return with(DefaultDeltaScriptFileBuilder()) {
            val settings = DeltaScriptDirectoryServiceSettings(
                    rollbackScriptsMustExist = rollbackScriptsOptional.not(),
                    deltaScriptExtension = deltaScriptExtension,
                    upgradeDirectoryName = upgradeDirectoryName,
                    rollbackDirectoryName = rollbackDirectoryName,
                    bidirectionalDirectoryName = bidirectionalDirectoryName
            )

            DeltaScriptDirectoryService(builder = this, serviceSettings = settings)
        }
    }

    /**
     * @return a [DeltaScriptTemplateLocator] created from the configuration.
     */
    override fun createDeltaScriptTemplateLocator(): DeltaScriptTemplateLocator {
        val templateOverrideDirectory: File? = generalConfig.getOptionalString("template-override-directory")?.let { File(it) }

        return if (templateOverrideDirectory == null || !templateOverrideDirectory.exists()) {
            ClasspathDeltaScriptTemplateLocator()
        } else {
            PeckingOrderDeltaScriptTemplateLocator(
                    DirectoryDeltaScriptTemplateLocator(templateOverrideDirectory),
                    ClasspathDeltaScriptTemplateLocator())
        }
    }

    /**
     * @return the [MigrationScriptSettings] extracted from the configuration.
     */
    override fun loadMigrationScriptSettings(): MigrationScriptSettings {
        val changeLogTableName = databaseEngineConfig.getRequiredString("change-log-table-name")
        val dbStatementDelimiter = databaseEngineConfig.getRequiredString("db-statement-delimiter")
        val dbStatementSeparator = databaseEngineConfig.getRequiredString("db-statement-separator")

        val bidirectionalUpgradeScriptOrder = generalConfig.getRequiredString(
                "bidirectional-script-to-upgrade-script-order")
        val bidirectionalRollbackScriptOrder = generalConfig.getRequiredString(
                "bidirectional-script-to-rollback-script-order")

        return MigrationScriptSettings(
                upgradeScriptOrder = BidirectionalFilesOrder.valueOf(bidirectionalUpgradeScriptOrder.toUpperCase()),
                rollbackScriptOrder = BidirectionalFilesOrder.valueOf(bidirectionalRollbackScriptOrder.toUpperCase()),
                dbChangeLogTableName = changeLogTableName,
                dbStatementDelimiter = dbStatementDelimiter,
                dbStatementSeparator = dbStatementSeparator
        )
    }
}