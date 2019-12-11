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
package org.dandelero.dbmigrations.api.migration;

import org.dandelero.dbmigrations.api.module.Module;
import org.dandelero.dbmigrations.api.version.Version;

/**
 * A factory for creating {@link MigrationScriptWriter} instances based on parameters such as the target database,
 * version, etc.
 * <br />
 * Created at: 29/10/19 10:09 pm
 *
 * @author dandelero
 */
public interface MigrationScriptWriterFactory {

    /**
     * Creates a {@link MigrationScriptWriter} to output the upgrade SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module         the module.
     * @param version        the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    MigrationScriptWriter createUpgradeScriptWriter(String databaseEngine, Module module, Version version);

    /**
     * Creates a {@link MigrationScriptWriter} to output the rollback SQL for a specific version.
     *
     * @param databaseEngine the database engine the script writer is to be composed for.
     * @param module         the module.
     * @param version        the version that will be processed with the created script writer.
     * @return the script writer instance.
     */
    MigrationScriptWriter createRollbackScriptWriter(String databaseEngine, Module module, Version version);
}
