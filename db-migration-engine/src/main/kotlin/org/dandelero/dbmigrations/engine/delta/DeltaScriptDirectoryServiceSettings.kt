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

/**
 * Stores the settings for configuring and customising the operation of the [DeltaScriptDirectoryService].
 * <br />
 * Created at: 8/11/19 7:17 am
 * @param rollbackScriptsMustExist whether at least 1 delta script is required.
 * @param deltaScriptExtension the file extension of delta scripts.
 * @param upgradeDirectoryName the name of the directory containing upgrade (delta) scripts.
 * @param rollbackDirectoryName the name of the directory containing rollback (delta) scripts.
 * @param bidirectionalDirectoryName the name of the directory containing bidirectional (delta) scripts.
 * @author dandelero
 */
data class DeltaScriptDirectoryServiceSettings(
    val rollbackScriptsMustExist: Boolean = false,
    val deltaScriptExtension: String = ".sql",
    val upgradeDirectoryName: String = "upgrade",
    val rollbackDirectoryName: String = "rollback",
    val bidirectionalDirectoryName: String = "bidirectional"
)