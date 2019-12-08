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
import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.dandelero.dbmigrations.api.version.VersionService
import org.dandelero.dbmigrations.engine.module.DirectoryModule
import org.dandelero.dbmigrations.engine.util.listChildDirectories
import org.slf4j.LoggerFactory

/**
 * An implementation of [VersionService] that loads [VersionedDirectory] instances from modules on a file system.
 * <br />
 * Created at: 31/10/19 11:27 pm
 * @author dandelero
 */
class DirectoryVersionService(
    private val settings: VersionServiceSettings,
    private val deserializer: VersionDeserializer
) : VersionService<DirectoryModule, VersionedDirectory> {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(DirectoryVersionService::class.java)

    /**
     * Gets the specific version in the module.
     * @param module the module whose specific version is to be retrieved.
     * @return that version or null.
     */
    override fun getVersion(module: DirectoryModule, versionString: String): VersionedDirectory? {
        logger.debug("Finding the '$versionString' version in ${module.name} ...")
        val potentialVersionDirectories = module.moduleDirectory.listChildDirectories()

        val dirForVersion = potentialVersionDirectories.firstOrNull { dir ->
            dir.name == versionString
        }

        return dirForVersion?.let { createVersionedDirectory(it) }
    }

    /**
     * Gets the latest version for the specified module.
     *
     * @param module the module whose latest version is to be retrieved.
     * @return the latest version available or null.
     */
    override fun latestVersion(module: DirectoryModule): VersionedDirectory? {
        logger.debug("Finding the latest version in ${module.name} ...")
        val potentialVersionDirectories = module.moduleDirectory.listChildDirectories()

        val allVersions = potentialVersionDirectories.mapNotNull { dir ->
            logger.debug("Processing ${dir.absolutePath} ...")
            val version = createVersionedDirectory(dir)
            if (version == null && !settings.ignoreInvalidVersions) {
                throw ApplicationException(ErrorCode.VERSION_DIRECTORY_ERROR.withDetails(
                        "Non-comformant version directory found at: ${dir.absolutePath}"))
            }
            version
        }.sorted()

        return allVersions.lastOrNull()
    }

    /**
     * Helper function to compose a [VersionedDirectory] instance from the given directory.
     * @param directory the directory.
     * @return the corresponding versioned directory or null.
     */
    private fun createVersionedDirectory(directory: File): VersionedDirectory? {
        val version = deserializer.deserialize(directory.name)
        if (version == null) {
            logger.debug("Could not deserialize '${directory.name}' into a Version instance")
            return null
        }
        return VersionedDirectory(directory, version)
    }
}