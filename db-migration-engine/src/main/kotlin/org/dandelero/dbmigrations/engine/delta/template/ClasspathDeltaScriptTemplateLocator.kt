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
import java.io.InputStream
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator

/**
 * A [DeltaScriptTemplateLocator] that loads templates from the classpath.
 * <br />
 * Created at: 28/10/19 6:07 pm
 * @author dandelero
 * @param sqlDirectoryPath the relative path on the classpath where the sql templates resides.
 */
class ClasspathDeltaScriptTemplateLocator(private val sqlDirectoryPath: String = templatesDirectoryPath) : DeltaScriptTemplateLocator {

    /**
     * The classloader used to load resources on the classpath.
     */
    private val loader: ClassLoader = ClasspathDeltaScriptTemplateLocator::class.java.classLoader

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
        val resource: InputStream? = loader.getResourceAsStream(sqlDirectoryPath + File.separator + databaseEngine + File.separator + fileName)
        return if (resource != null) {
            String(resource.readBytes())
        } else {
            null
        }
    }

    /**
     * Gets the template that is to be applied for composing the overall script irrespective of the database that
     * the change script is being generated for.
     *
     * @return the template to be applied for this database engine; null if none was found.
     */
    override fun findMigrationScriptFileTemplate(): String? {
        val resource: InputStream? = loader.getResourceAsStream(sqlDirectoryPath + File.separator + "file_template.txt")
        return if (resource != null) {
            String(resource.readBytes())
        } else {
            null
        }
    }

    companion object {

        /**
         * The relative path to the templates directory that is packaged in the application.
         */
        private const val templatesDirectoryPath = "default_templates/sql"
    }
}