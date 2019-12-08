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
package org.dandelero.dbmigrations.engine.delta.template

import java.io.File
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.util.readFully

/**
 * A [DeltaScriptTemplateLocator] that loads templates from any directory.
 * <br />
 * Created at: 28/10/19 8:25 pm
 * @author dandelero
 */
class DirectoryDeltaScriptTemplateLocator(private val directory: File) : DeltaScriptTemplateLocator {

    init {
        if (!directory.exists() || !directory.isDirectory) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPTS_DETECTED
                    .withDetails("No directory found for the templates: ${directory.absolutePath}"))
        }
    }

    /**
     * Stores the names of the template files for each script category.
     */
    private val scriptCategoryTemplateNames = mapOf(
            DeltaScriptCategory.UPGRADE to "upgrade_template.txt",
            DeltaScriptCategory.ROLLBACK to "rollback_template.txt",
            DeltaScriptCategory.BIDIRECTIONAL to "bidirectional_template.txt")

    /**
     * Gets the template that is to be applied for the given parameters.
     *
     * @param databaseEngine the database engine that script composition is to occur for.
     * @param deltaScriptCategory the category of scripts being processed.
     * @return the template to be applied for this database engine and script category; null if none was found.
     */
    override fun findDeltaScriptTemplate(databaseEngine: String, deltaScriptCategory: DeltaScriptCategory): String? {
        val fileName = scriptCategoryTemplateNames[deltaScriptCategory] ?: throw ApplicationException(
                        ErrorCode.INVALID_SCRIPTS_DETECTED.withDetails("Unsupported script category: $deltaScriptCategory"))
        return File(directory, databaseEngine + File.separator + fileName).readFully()
    }

    /**
     * Gets the template that is to be applied for composing the overall script irrespective of the database that
     * the change script is being generated for.
     *
     * @return the template to be applied for this database engine; null if none was found.
     */
    override fun findMigrationScriptFileTemplate(): String? {
        return File(directory, "file_template.txt").readFully()
    }
}