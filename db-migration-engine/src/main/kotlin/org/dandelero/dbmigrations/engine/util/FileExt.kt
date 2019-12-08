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
package org.dandelero.dbmigrations.engine.util

import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.util.Properties

/**
 * Loads this file as a Properties file.
 * @return the properties in this file.
 */
fun File.loadProperties(): Properties {
    // This exists because of Java's awesome Properties API!
    return with(Properties()) {
        load(inputStream())
        this
    }
}

/**
 * Reads the file in its entirety.
 * @return the file contents; null if it was not read.
 */
fun File.readFully(): String? {
    if (!exists() || !isFile) {
        return null
    }
    return (String(Files.readAllBytes(toPath())))
}

/**
 * Lists the immediate child directories in the current directory.
 * @return the child directories; empty array if none exist.
 */
fun File.listChildDirectories(): Array<File> {
    return this.listFiles(FileFilter { it.isDirectory }) ?: emptyArray()
}

/**
 * Lists the immediate child files in the current directory.
 * @param extension the desired extension of the files.
 * @return the child files; empty array if none exist.
 */
fun File.listChildFiles(extension: String): Array<File> {
    return this.listFiles(FileFilter { it.isFile && it.name.endsWith(extension) }) ?: emptyArray()
}

/**
 * An extension function that creates a directory and returns the instance.
 * @return the directory.
 */
fun File.mkdir2(): File {
    this.mkdirs()
    return this
}