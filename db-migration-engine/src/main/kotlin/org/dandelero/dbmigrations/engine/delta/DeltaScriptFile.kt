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

/**
 * A [DeltaScript] that is sourced from a file.
 * <br />
 * Created at: 29/10/19 6:45 pm
 *
 * @param file the file that this script is sourced from.
 * @param seqNumber the sequence number for the file.
 * @author dandelero
 */
class DeltaScriptFile(private val file: File, private val seqNumber: Int) : DeltaScript {

    init {
        if (!file.exists() || !file.isFile) {
            throw ApplicationException(ErrorCode.INVALID_SCRIPTS_DETECTED.withDetails("Delta script not found at: ${file.absolutePath}"))
        }
    }

    /**
     * @return the name of this script.
     */
    override fun getName(): String {
        return file.name
    }

    /**
     * @return the sequence number of this script.
     */
    override fun getSequenceNumber(): Int = seqNumber

    /**
     * @return the contents of this script.
     */
    override fun getContents(): String = this.file.readText()
}