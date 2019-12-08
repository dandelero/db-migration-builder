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
import org.dandelero.dbmigrations.api.module.Module
import org.dandelero.dbmigrations.engine.migration.DEFAULT_MODULE_NAME

/**
 * A module represented by a directory structure.
 * <br />
 * Created at: 29/10/19 6:55 am
 * @param moduleDirectory the directory that this module resides in.
 * @param nameValue the name of the module.
 * @author dandelero
 */
class DirectoryModule(val moduleDirectory: File, private val nameValue: String = moduleDirectory.name) : Module {

    init {
        if (!moduleDirectory.exists() || !moduleDirectory.isDirectory) {
            throw ApplicationException(ErrorCode.INVALID_MODULE.withDetails("Invalid module directory: ${moduleDirectory.absolutePath}"))
        }
    }

    /**
     * @return true if this is the default module.
     */
    override fun isDefault(): Boolean = nameValue == DEFAULT_MODULE_NAME

    /**
     * @return the name of this module.
     */
    override fun getName(): String = nameValue

    /**
     * @return string representation of this object.
     */
    override fun toString(): String {
        return "DirectoryModule['${moduleDirectory.absolutePath}']"
    }

    /**
     * Compares this instance to the given module.
     * @param other the other module to compare with.
     * @return -1, 0 or 1 based on the contract of [Comparable]
     */
    override fun compareTo(other: Module?): Int {
        if (other == null) {
            return 1
        }
        return name.compareTo(other.name)
    }
}