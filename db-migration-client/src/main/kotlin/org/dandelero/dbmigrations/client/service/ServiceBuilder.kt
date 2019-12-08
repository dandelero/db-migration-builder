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

import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryService
import org.dandelero.dbmigrations.engine.migration.MigrationScriptSettings
import org.dandelero.dbmigrations.engine.version.DirectoryVersionService

/**
 * Simple interface for reading configuration and creating application services and
 * resources.
 * <br />
 * Created at: 20/11/19 5:58 pm
 * @author dandelero
 */
interface ServiceBuilder {

    /**
     * @return a [DirectoryVersionService] created from the configuration.
     */
    fun createDirectoryVersionService(): DirectoryVersionService

    /**
     * @return a [DeltaScriptDirectoryService] created from the configuration.
     */
    fun createDeltaScriptDirectoryService(): DeltaScriptDirectoryService

    /**
     * @return a [DeltaScriptTemplateLocator] created from the configuration.
     */
    fun createDeltaScriptTemplateLocator(): DeltaScriptTemplateLocator

    /**
     * @return the [MigrationScriptSettings] extracted from the configuration.
     */
    fun loadMigrationScriptSettings(): MigrationScriptSettings
}