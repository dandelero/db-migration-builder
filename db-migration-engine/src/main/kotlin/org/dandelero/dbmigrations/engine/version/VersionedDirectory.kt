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
package org.dandelero.dbmigrations.engine.version

import java.io.File
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.version.Version

/**
 * Represents a [Version] instance that is backed by a directory on the filesystem.
 * <br />
 * Created at: 31/10/19 8:12 am
 * @param directory the directory backing the version.
 * @param version the version that this directory is for.
 * @author dandelero
 */
class VersionedDirectory(val directory: File, val version: Version) : Version {

    init {
        if (!directory.exists() || !directory.isDirectory) {
            throw ApplicationException(ErrorCode.VERSION_DIRECTORY_ERROR.withDetails(
                    "Version directory not found at '${directory.absolutePath}'"))
        }

        if (directory.name != version.nameString) {
            throw ApplicationException(ErrorCode.VERSION_DIRECTORY_ERROR.withDetails(
                    "Directory name (${directory.name}) must match version name (${version.nameString})"))
        }
    }

    /**
     * @return the name of this version.
     */
    override fun getNameString(): String = directory.name

    /**
     * Compares this instance with the other version.
     * @param other the other instance.
     * @return -1, 0, or 1 based on the [Comparable] contract.
     */
    override fun compareTo(other: Version?): Int {
        if (other == null || other !is VersionedDirectory) {
            return 1
        }
        return this.version.compareTo(other.version)
    }
}