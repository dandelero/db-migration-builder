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

import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator

/**
 * A [DeltaScriptTemplateLocator] implementation that returns the first template returned from the locators provided.
 * <br />
 * Created at: 28/10/19 8:56 pm
 * @author dandelero
 *
 * @param orderedDeltaScriptTemplateLocators the (ordered) template locators to be used.
 */
class PeckingOrderDeltaScriptTemplateLocator(
    private vararg val orderedDeltaScriptTemplateLocators: DeltaScriptTemplateLocator
) : DeltaScriptTemplateLocator {

    /**
     * Gets the template that is to be applied for the given parameters.
     *
     * @param databaseEngine the database engine that script composition is to occur for.
     * @param deltaScriptCategory the category of scripts being processed.
     * @return the template to be applied for this database engine and script category; null if none was found.
     */
    override fun findDeltaScriptTemplate(databaseEngine: String, deltaScriptCategory: DeltaScriptCategory): String? {
        return orderedDeltaScriptTemplateLocators.mapNotNull {
            it.findDeltaScriptTemplate(databaseEngine, deltaScriptCategory)
        }.firstOrNull()
    }

    /**
     * Gets the template that is to be applied for composing the overall script irrespective of the database that
     * the change script is being generated for.
     *
     * @return the template to be applied for this database engine; null if none was found.
     */
    override fun findMigrationScriptFileTemplate(): String? {
        return orderedDeltaScriptTemplateLocators.mapNotNull { it.findMigrationScriptFileTemplate() }.firstOrNull()
    }
}