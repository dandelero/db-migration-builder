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
package org.dandelero.dbmigrations.engine.delta

import java.io.File
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScript
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptService
import org.dandelero.dbmigrations.engine.module.DirectoryModule
import org.dandelero.dbmigrations.engine.util.listChildFiles
import org.dandelero.dbmigrations.engine.version.VersionedDirectory
import org.slf4j.LoggerFactory

/**
 * A [DeltaScriptService] implementation that loads scripts from a directory on the filesystem mandated by module
 * and version.
 * <br />
 * Created at: 1/11/19 8:57 pm
 * @param builder the builder used to construct [DeltaScriptFile] instances from files.
 * @param serviceSettings the settings driving the parameters and customisation of this service.
 * @author dandelero
 */
class DeltaScriptDirectoryService(
    private val builder: DeltaScriptFileBuilder,
    private val serviceSettings: DeltaScriptDirectoryServiceSettings
) : DeltaScriptService<DirectoryModule, VersionedDirectory> {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(DeltaScriptDirectoryService::class.java)

    /**
     * A map from the script category to the name of the directory that those scripts are located in.
     */
    private val categoryToDirNameMap = mapOf(
            DeltaScriptCategory.UPGRADE to serviceSettings.upgradeDirectoryName,
            DeltaScriptCategory.ROLLBACK to serviceSettings.rollbackDirectoryName,
            DeltaScriptCategory.BIDIRECTIONAL to serviceSettings.bidirectionalDirectoryName
    )

    /**
     * Gets the change scripts for the given category in the version.
     *
     * @param category the category of scripts sought.
     * @param module the module to search within.
     * @param version the version whose scripts are sought.
     * @return the list of scripts found.
     */
    override fun getScripts(category: DeltaScriptCategory, module: DirectoryModule, version: VersionedDirectory): List<DeltaScript> {
        val dirName = categoryToDirNameMap[category] ?: throw ApplicationException(
                ErrorCode.RESOURCE_ERROR.withDetails("Unsupported category: '$category'"))

        val dir = File(version.directory, dirName)
        if (!dir.exists()) {
            // No "upgrade" and/or "rollback" directory - that's ok because there may be no changes!
            logger.debug("No directory found for category=$category in version=${version.nameString}")
            return emptyList()
        }
        if (!dir.isDirectory) {
            throw ApplicationException(ErrorCode.RESOURCE_ERROR.withDetails(
                    "Invalid version directory provided: expected a directory at ${dir.absolutePath}"))
        }

        val deltaScriptFiles: Array<File> = dir.listChildFiles(serviceSettings.deltaScriptExtension)
        val deltaScriptFilesCount = deltaScriptFiles.size
        if (deltaScriptFilesCount == 0) {
            if (category != DeltaScriptCategory.BIDIRECTIONAL && serviceSettings.rollbackScriptsMustExist) {
                throw ApplicationException(ErrorCode.NO_SCRIPTS_FOUND.withDetails(
                        "No '${serviceSettings.deltaScriptExtension}' scripts found in ${dir.absolutePath}"))
            } else {
                return emptyList()
            }
        }

        val orderedDeltaScripts = deltaScriptFiles.mapNotNull { file ->
            builder.build(file)
        }.sorted().toSet()

        if (orderedDeltaScripts.size != deltaScriptFilesCount) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPT_SEQUENCE.withDetails(
                    "Invalid script sequencing detected for version: '${version.directory}'"))
        }

        // Now check that we have sequence numbers from 1 to n, with no repeats.
        val firstSequenceNumber = orderedDeltaScripts.first().sequenceNumber
        val lastSequenceNumber = orderedDeltaScripts.last().sequenceNumber

        // The first and last file sequence numbers must match the count
        if (firstSequenceNumber != 1) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPT_SEQUENCE.withDetails(
                    "Sequence number of the first file expected to be 1 but got $firstSequenceNumber"))
        } else if (lastSequenceNumber != deltaScriptFilesCount) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPT_SEQUENCE.withDetails(
                    "Sequence number of the last file expected to be $deltaScriptFilesCount but got $lastSequenceNumber"))
        }

        val numUniqueSequenceNumbers = orderedDeltaScripts.map { it.sequenceNumber }.toSet().count()
        if (numUniqueSequenceNumbers != deltaScriptFilesCount) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPT_SEQUENCE.withDetails("Sequence numbers must be unique"))
        }
        return orderedDeltaScripts.toList()
    }
}