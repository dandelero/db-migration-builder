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
 * The name of the default module that has no name but only versions.
 */
const val DEFAULT_MODULE_NAME: String = "default"

const val KEY_SCRIPT_FILE_NAME: String = "scriptName"
const val KEY_SCRIPT_CONTENTS: String = "scriptContents"
const val KEY_SCRIPT_AUTHOR: String = "scriptAuthor"
const val KEY_SCRIPT_SEQ_NUMBER: String = "scriptSequenceNumber"
const val KEY_CHANGE_LOG_TABLE: String = "changeLogTableName"

const val KEY_FILE_TYPE: String = "fileType"
const val KEY_INDEX: String = "index"

const val KEY_MODULE_NAME: String = "moduleName"
const val KEY_RELEASE_LABEL: String = "releaseLabel"
const val KEY_CREATION_TIMESTAMP: String = "creationTimestamp"
const val KEY_SCRIPT_COUNT: String = "scriptCount"
const val KEY_STMT_SEPARATOR: String = "statementSeparator"
const val KEY_STMT_DELIMITER: String = "statementDelimiter"