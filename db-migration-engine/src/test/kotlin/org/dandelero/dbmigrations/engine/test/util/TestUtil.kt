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
package org.dandelero.dbmigrations.engine.test.util

import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.util.Properties
import java.util.UUID
import kotlin.math.min
import org.dandelero.dbmigrations.engine.util.loadProperties
import org.junit.jupiter.api.Disabled

/**
 * A set of utility functions for test purposes.
 * <br />
 * Created at: 26/10/19 6:47 am
 * @author dandelero
 */
@Disabled
object TestUtil {

    /**
     * The class loader used to locate classpath resources.
     */
    private val classLoader = javaClass.classLoader

    /**
     * The file separator.
     */
    private val sep = File.separator

    /**
     * The current host name.
     */
    private val currentHostName: String = InetAddress.getLocalHost().hostName

    /**
     * The path to the src/test/resources directory.
     */
    private val srcTestResourcesDir = File("src${sep}test${sep}resources")

    /**
     * Creates a directory in the src/test/resources directory in the given structure.
     * @param permittedMachines the list of machines that creation of the directory structure is to be performed on.
     * @param relativeDirectoryPath the directory path beneath src/test/resources/expected_output to be created.
     * @param expectedOutputDirName the name of the directory beneath src/test/resources to create.
     * @return the file object for the created directory; null if none was created.
     */
    fun writeExpectedOutputDirectory(
        permittedMachines: List<String>,
        relativeDirectoryPath: String,
        expectedOutputDirName: String = "expected_output"
    ): File? {
        if (!permittedMachines.contains(currentHostName)) {
            return null
        }
        if (!srcTestResourcesDir.exists()) {
            srcTestResourcesDir.mkdirs()
        }

        val directoryPath = if (relativeDirectoryPath.startsWith(expectedOutputDirName)) {
            relativeDirectoryPath
        } else {
            expectedOutputDirName + sep + relativeDirectoryPath
        }

        with(File(srcTestResourcesDir, directoryPath)) {
            this.mkdirs()
            return if (exists()) this else null
        }
    }

    /**
     * Loads a properties file on the classpath.
     * @param relativePath the relative path to the properties file.
     * @return the properties object.
     * @throws AssertionError if the file does not exist.
     */
    fun loadRequiredPropertiesFromClasspath(relativePath: String): Properties {
        return getRequiredFileOnClasspath(relativePath).loadProperties()
    }

    /**
     * Gets a file on the classpath.
     * @param relativePath the relative path to the file.
     * @return the corresponding file if it exists; null if not found.
     */
    fun getFileOnClasspath(relativePath: String): File? {
        val resource = classLoader.getResource(relativePath) ?: return null

        return with(File(resource.file)) {
            if (this.exists())
                this
            else null
        }
    }

    /**
     * Gets a file on the classpath.
     * @param relativePath the relative path to the file.
     * @return the corresponding file if it exists.
     * @throws AssertionError if the file does not exist.
     */
    fun getRequiredFileOnClasspath(relativePath: String): File {
        val file = getFileOnClasspath(relativePath)
        if (file == null) {
            throw AssertionError("File not found at '$relativePath'")
        } else {
            return file
        }
    }

    /**
     * Gets a directory on the classpath.
     * @param relativePath the relative path to the directory.
     * @return the corresponding directory if it exists; null if not found.
     */
    fun getDirOnClasspath(relativePath: String): File? {
        val file = getFileOnClasspath(relativePath)
        if (file != null) {
            if (file.exists() && file.isDirectory) {
                return file
            }
            throw java.lang.AssertionError("Expected a directory: $relativePath")
        }
        return null
    }

    /**
     * Gets a directory on the classpath.
     * @param relativePath the relative path to the directory.
     * @return the corresponding directory if it exists.
     * @throws AssertionError if the directory does not exist.
     */
    fun getRequiredDirectoryOnClasspath(relativePath: String): File {
        val dir = getDirOnClasspath(relativePath)
        if (dir != null) {
            return dir
        }
        throw RuntimeException("Directory not found at: $relativePath")
    }

    /**
     * Creates a temp file that is deleted on exist.
     * @param name the name of the file.
     * @param extension the extension of the file.
     * @return the file.
     */
    fun createTempFile(name: String, extension: String = ".txt"): File {
        val fileExtension = if (extension.startsWith(".")) {
            extension
        } else {
            ".$extension"
        }
        return with(File.createTempFile(name, fileExtension)) {
            deleteOnExit()
            this
        }
    }

    /**
     * Creates a directory in the system's temporary directory.
     * @param name an optional directory name.
     * @return the directory.
     */
    fun createTempDirectory(name: String = UUID.randomUUID().toString()): File {
        return with(Files.createTempDirectory(name)) {
            val file = this.toFile()
            file.deleteOnExit()
            file
        }
    }

    /**
     * Compares the two sets of lines and return the error message if there is an error.
     * @param expectedOutput the lines containing the expected output.
     * @param actualOutput the lines containing the actual output.
     * @param ignoreLineEvaluator a lambda that is used to evaluate whether differences are to be ignored for a particular line.
     * @return the error message if there is a difference; null if there is no difference.
     */
    fun compareLineByLine(
        expectedOutput: List<String>,
        actualOutput: List<String>,
        ignoreLineEvaluator: (String) -> Boolean
    ): String? {
        val expectedLineCount = expectedOutput.size
        val actualLineCount = actualOutput.size

        // Process up to the minimum of the files, just to assert what we have. This will provide better error messages.
        val linesToProcess = min(actualLineCount, expectedLineCount)
        for (i in 0 until linesToProcess) {
            val currentExpectedLine = expectedOutput[i]
            val currentActualLine = actualOutput[i]
            if (currentExpectedLine != currentActualLine) {
                if (!ignoreLineEvaluator(currentExpectedLine) || !ignoreLineEvaluator(currentActualLine)) {
                    // Difference cannot be ignored.
                    return "Line difference found at line $i: \nexpected='$currentExpectedLine'\n" +
                            "actual ='$currentActualLine'"
                }
            }
        }

        // assert sizes
        if (expectedLineCount != actualLineCount) {
            return "File line count mismatch: expected $expectedLineCount but got $actualLineCount lines"
        }
        // No diffs.
        return null
    }
}