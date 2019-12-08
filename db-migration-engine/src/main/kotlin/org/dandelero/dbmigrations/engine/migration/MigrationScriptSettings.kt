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
package org.dandelero.dbmigrations.engine.migration

/**
 * Contains the settings for controlling migration script composition.
 * <br />
 * Created at: 29/10/19 6:59 pm
 * @param upgradeScriptOrder the order in which bidirectional scripts are to be processed in the upgrade script.
 * @param rollbackScriptOrder the order in which bidirectional scripts are to be processed in the rollback script.
 * @param dbChangeLogTableName the changelog table name to be written to.
 * @param dbStatementDelimiter the value to be written after an SQL statement is written.
 * @param dbStatementSeparator the separator string between individual database statements.
 * @author dandelero
 */
data class MigrationScriptSettings(
    val upgradeScriptOrder: BidirectionalFilesOrder = BidirectionalFilesOrder.LAST,
    val rollbackScriptOrder: BidirectionalFilesOrder = BidirectionalFilesOrder.LAST,
    val dbChangeLogTableName: String = "change_log",
    val dbStatementDelimiter: String = "",
    val dbStatementSeparator: String = ""
)

/**
 * Specifies the desired order in which bidirectional files are to be written relative to a main set of scripts.
 */
enum class BidirectionalFilesOrder {

    /**
     * Bidirectional scripts should come before other scripts in the category.
     */
    FIRST,

    /**
     * Bidirectional scripts should come after other scripts in the category.
     */
    LAST,

    /**
     * Bidirectional scripts should not be written.
     */
    EXCLUDE
}