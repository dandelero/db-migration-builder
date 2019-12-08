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
package org.dandelero.dbmigrations.engine.module

import java.io.File
import org.dandelero.dbmigrations.engine.migration.DEFAULT_MODULE_NAME

/**
 * A [org.dandelero.db.api.module.ModuleService] implementation that loads the default module.
 * <br />
 * Created at: 11/11/19 8:51 pm
 * @author dandelero
 */
class NoModuleDirectoryModuleService(private val baseDirectory: File) : DirectoryModuleService(baseDirectory) {

    /**
     * Finds the module with the given name.
     *
     * @param moduleName the name of the module.
     * @return the module matching the name; null if not found.
     */
    override fun findModuleByName(moduleName: String): DirectoryModule? {
        return if (moduleName.isNullOrEmpty()) {
            DirectoryModule(moduleDirectory = baseDirectory, nameValue = DEFAULT_MODULE_NAME)
        } else {
            null
        }
    }

    /**
     * @return all the modules available.
     */
    override fun listAllModules(): List<DirectoryModule> = listOfNotNull(findModuleByName(""))
}