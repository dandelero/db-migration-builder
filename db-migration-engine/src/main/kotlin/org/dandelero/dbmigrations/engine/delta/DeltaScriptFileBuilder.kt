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

/**
 * Provides a way for [DeltaScriptFile] instances to be constructed from files.
 * <br />
 * Created at: 4/11/19 2:42 pm
 * @author dandelero
 */
interface DeltaScriptFileBuilder {

    /**
     * Constructs a [DeltaScriptFile] instance of the given file.
     * @param file the file to be adapted into a change script.
     * @return the change script instance; null if the file cannot be adapted.
     */
    fun build(file: File): DeltaScriptFile?
}