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
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.module.ModuleService
import org.dandelero.dbmigrations.engine.util.listChildDirectories

/**
 * A [ModuleService] implementation that works with [org.dandelero.db.api.model.Module]s contained in
 * directories.
 * <br />
 * Created at: 29/10/19 7:06 am
 * @param baseDirectory the base directory that contains all modules.
 * @author dandelero
 */
open class DirectoryModuleService(private val baseDirectory: File) : ModuleService<DirectoryModule> {

    init {
        if (!baseDirectory.exists() || !baseDirectory.isDirectory) {
            throw ApplicationException(ErrorCode.MODULE_DIRECTORY_ERROR.withDetails(
                    "No base directory exists for locating modules: ${baseDirectory.absolutePath}"))
        }
    }

    /**
     * Finds the module with the given name.
     *
     * @param moduleName the name of the module.
     * @return the module matching the name; null if not found.
     */
    override fun findModuleByName(moduleName: String): DirectoryModule? {
        with(File(baseDirectory, moduleName)) {
            return if (exists()) {
                if (isDirectory) {
                    DirectoryModule(this)
                } else {
                    throw ApplicationException(ErrorCode.MODULE_DIRECTORY_ERROR.withDetails(
                            "The given module '$moduleName' is not a directory"))
                }
            } else {
                null // Not found.
            }
        }
    }

    /**
     * @return all the modules available.
     */
    override fun listAllModules(): List<DirectoryModule> = baseDirectory.listChildDirectories().map {
        DirectoryModule(it)
    }
}