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
package org.dandelero.dbmigrations.client

import java.io.File
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.dandelero.dbmigrations.engine.util.listChildDirectories
import org.dandelero.dbmigrations.engine.util.listChildFiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * A suite of tests for the [CommandLineLauncher] class.
 * <br />
 * Created at: 15/11/19 6:16 pm
 * @author dandelero
 */
class CommandLineLauncherTest {

    /**
     * The yaml config file containing our test config.
     */
    private val yamlConfigFile = TestUtil.getRequiredFileOnClasspath("input/conf/test-strict-config.yaml")

    /**
     * The yaml config file containing our (relaxed) test config.
     */
    private val lenientYamlConfigFile = TestUtil.getRequiredFileOnClasspath("input/conf/test-lenient-config.yaml")

    /**
     * Tests the generation of scripts for a specific version in set of modules that do not all contain the
     * said version.
     */
    @Test
    fun generateMssqlMigrationScriptForSpecificVersionInOneOfManyModulesTest() {
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/with-modules/scheme/standard")
        val tempDir = TestUtil.createTempDirectory()
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp", tempDir.absolutePath, "-d", "mssql") +
                arrayOf("-m", "accounting, customer, packages", "-v", "1.0.0",
                        "-cf", yamlConfigFile.absolutePath,
                        "-vs", "default")

        Assertions.assertThrows(ApplicationException::class.java) {
            CommandLineLauncher.main(args)
        }
    }

    /**
     * Tests the generation of scripts for the latest version in set of modules.
     */
    @Test
    fun generateMssqlMigrationScriptForLatestVersionInMultipleModulesTest() {
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/with-modules/scheme/standard")
        val tempDir = TestUtil.createTempDirectory()
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp", tempDir.absolutePath, "-d", "mssql") +
                arrayOf("-m", "accounting, customer",
                        "-cf", yamlConfigFile.absolutePath,
                        "-vs", "default")
        CommandLineLauncher.main(args)

        // Find the modules.
        var moduleDirs = tempDir.listChildDirectories()
        assertEquals(2, moduleDirs.size, "Expected exactly 2 directories for the modules")
        val moduleNames = moduleDirs.map { it.name }.toSet()
        assertTrue(moduleNames.contains("accounting"), "Module not found")
        assertTrue(moduleNames.contains("customer"), "Module not found")

        // Accounting module should be 1.0.1
        var accountingModule = moduleDirs.first { it.name == "accounting" }
        assertModuleDirectory(moduleDir = accountingModule, expectedModuleName = "accounting", expectedVersion = "1.0.1")

        // Customer module should be 1.0.2
        val customerModule = moduleDirs.first { it.name == "customer" }
        assertModuleDirectory(moduleDir = customerModule, expectedModuleName = "customer", expectedVersion = "1.0.2")
    }

    /**
     * Tests the generation of scripts for a specific version in set of modules.
     */
    @Test
    fun generateMssqlMigrationScriptForSpecificVersionInMultipleModulesTest() {
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/with-modules/scheme/standard")
        val tempDir = TestUtil.createTempDirectory()
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp", tempDir.absolutePath, "-d", "mssql") +
                arrayOf("-m", "accounting, customer", "-v", "1.0.0",
                        "-cf", yamlConfigFile.absolutePath,
                        "-vs", "default"
                )
        CommandLineLauncher.main(args)

        // Find the modules.
        var moduleDirs = tempDir.listChildDirectories()
        assertEquals(2, moduleDirs.size, "Expected exactly 2 directories for the modules")
        val moduleNames = moduleDirs.map { it.name }.toSet()
        assertTrue(moduleNames.contains("accounting"), "Module not found")
        assertTrue(moduleNames.contains("customer"), "Module not found")

        moduleDirs.forEach { moduleDir ->
            assertModuleDirectory(moduleDir = moduleDir, expectedModuleName = moduleDir.name, expectedVersion = "1.0.0")
        }
    }

    /**
     * Tests the generation of scripts for a specific version in a specific module.
     */
    @Test
    fun generateMssqlMigrationScriptForSpecificVersionInSpecificModuleTest() {
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/with-modules/scheme/standard")
        val tempDir = TestUtil.createTempDirectory()
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp", tempDir.absolutePath, "-d", "mssql") +
                arrayOf("-m", "accounting", "-v", "1.0.0",
                        "-cf", yamlConfigFile.absolutePath,
                        "-vs", "default")
        CommandLineLauncher.main(args)

        var moduleDirs = tempDir.listChildDirectories()
        assertEquals(1, moduleDirs.size, "Expected exactly 1 directory for the module")
        assertModuleDirectory(moduleDir = moduleDirs[0], expectedModuleName = "accounting", expectedVersion = "1.0.0")
    }

    /**
     * Tests the generation of scripts for the latest version in a specific module.
     */
    @Test
    fun generateMssqlMigrationScriptForLatestVersionInSpecificModuleTest() {
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/with-modules/scheme/standard")
        val tempDir = TestUtil.createTempDirectory()
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp", tempDir.absolutePath, "-d", "mssql") +
                arrayOf("-m", "accounting",
                        "-cf", yamlConfigFile.absolutePath,
                        "-vs", "default")
        CommandLineLauncher.main(args)

        var moduleDirs = tempDir.listChildDirectories()
        assertEquals(1, moduleDirs.size, "Expected exactly 1 directory for the module")
        assertModuleDirectory(moduleDir = moduleDirs[0], expectedModuleName = "accounting", expectedVersion = "1.0.1")
    }

    /**
     * Tests the generation of scripts for a specific version in the default module that uses a prefix of 'r'
     * for its versioning.
     */
    @Test
    fun generateMssqlMigrationScriptForSpecificVersionInDefaultModuleTest() {
        val tempDir = TestUtil.createTempDirectory()
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/no-modules/scheme/standard")
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp",
                tempDir.absolutePath, "-d", "mssql", "-cf", lenientYamlConfigFile.absolutePath) +
                arrayOf("-vs", "project-1", "-v", "r1.0.1")
        CommandLineLauncher.main(args)

        val childDirs = tempDir.listChildDirectories()
        assertEquals(1, childDirs.size, "Expected exactly 1 directory for the latest version")
        assertVersionDirectory(childDirs[0], "r1.0.1")
    }

    /**
     * Tests the generation of scripts for the latest version in the default module that uses a prefix of 'r'
     * for its versioning.
     */
    @Test
    fun generateMssqlMigrationScriptForLatestVersionInDefaultModuleTest() {
        val tempDir = TestUtil.createTempDirectory()
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/no-modules/scheme/standard")
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp",
                tempDir.absolutePath, "-d", "mssql", "-cf", yamlConfigFile.absolutePath) +
                arrayOf("-vs", "project-1")
        CommandLineLauncher.main(args)

        val childDirs = tempDir.listChildDirectories()
        assertEquals(1, childDirs.size, "Expected exactly 1 directory for the latest version")
        assertVersionDirectory(childDirs[0], "r2.5.1.2-rc-1")
    }

    /**
     * Tests the generation of scripts for a specific version in the default module that uses semver1 for its
     * versioning.
     */
    @Test
    fun generateMssqlMigrationScriptForSpecificSemver1VersionInDefaultModuleTest() {
        val tempDir = TestUtil.createTempDirectory()
        val inputDir = TestUtil.getRequiredFileOnClasspath("input/no-modules/scheme/semver1")
        val args = arrayOf("-idp", inputDir.absolutePath, "-odp",
                tempDir.absolutePath, "-d", "mssql", "-cf", lenientYamlConfigFile.absolutePath) +
                arrayOf("-vs", "project-2", "-v", "1.22.890+20130313144701")
        CommandLineLauncher.main(args)

        val childDirs = tempDir.listChildDirectories()
        assertEquals(1, childDirs.size, "Expected exactly 1 directory for the latest version")
        assertVersionDirectory(childDirs[0], "1.22.890+20130313144701")
    }

    /**
     * Asserts the structure of a module that contains the resources for a particular version.
     * @param moduleDir the module directory to be inspected.
     * @param expectedModuleName the name of the module.
     * @param expectedVersion the version string.
     */
    private fun assertModuleDirectory(moduleDir: File, expectedModuleName: String, expectedVersion: String) {
        assertEquals(expectedModuleName, moduleDir.name, "Mismatch in module")
        val childDirs = moduleDir.listChildDirectories()
        assertEquals(1, childDirs.size, "Expected exactly 1 directory for the version")
        assertVersionDirectory(versionDir = childDirs[0], expectedVersion = expectedVersion)
    }

    /**
     * Asserts the structure of a directory that contains the resources for a particular version.
     * @param versionDir the version directory to be inspected.
     * @param expectedVersion the version string.
     */
    private fun assertVersionDirectory(versionDir: File, expectedVersion: String) {
        assertEquals(expectedVersion, versionDir.name, "Mismatch in version directory name")
        val latestVersionFiles = versionDir.listChildFiles("sql")
        assertEquals(2, latestVersionFiles.size, "Latest version expected to contain 2 files")
        val latestVersionFileNames = latestVersionFiles.map { it.name }.toSet()
        assertTrue(latestVersionFileNames.contains("upgrade.sql"), "Upgrade file not found")
        assertTrue(latestVersionFileNames.contains("rollback.sql"), "Rollback file not found")
    }
}