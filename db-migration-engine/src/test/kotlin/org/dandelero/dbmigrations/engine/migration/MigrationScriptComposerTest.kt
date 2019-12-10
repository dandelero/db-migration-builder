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

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryServiceSettings
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.dandelero.dbmigrations.engine.version.VersionServiceSettings
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * A set of end-to-end migration script composition tests.
 * <br />
 * Created at: 10/11/19 12:17 pm
 * @author dandelero
 */
class MigrationScriptComposerTest {

    /**
     * A deserializer instance.
     */
    private val versionDeserialzer1 = TestVersionRegistry.createDeserializer1()

    /**
     * A deserializer instance.
     */
    private val versionDeserialzer2 = TestVersionRegistry.createDeserializer2()

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            System.setProperty("user.name", "dandelero")
        }
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts.
     */
    @Test
    fun defaultModuleTest1() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                versionString = "r1.0.0", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts.
     */
    @Test
    fun defaultModuleTestWithMysql() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementSeparator = ";",
                dbChangeLogTableName = "change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/mysql/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "mysql-standard-case",
                versionString = "r1.0.0", databaseEngine = "mysql")
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts, but with bidirectional scripts written first.
     */
    @Test
    fun defaultModuleTest1a() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(
                upgradeScriptOrder = BidirectionalFilesOrder.FIRST, rollbackScriptOrder = BidirectionalFilesOrder.FIRST,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "bd-files-first",
                versionString = "r1.0.0", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts, but with bidirectional scripts excluded.
     */
    @Test
    fun defaultModuleTest1b() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(
                upgradeScriptOrder = BidirectionalFilesOrder.EXCLUDE,
                rollbackScriptOrder = BidirectionalFilesOrder.EXCLUDE,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "bdf-files-excluded",
                versionString = "r1.0.0", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade and rollback but no bidirectional scripts.
     */
    @Test
    fun defaultModuleTest2() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                versionString = "r1.0.1", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade and bidirectional but no rollback scripts.
     */
    @Test
    fun defaultModuleTest3() {
        // Relax our rollback file existence constraint.
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings(rollbackScriptsMustExist = true)
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")

        // With the default settings the process should fail because there are no rollback scrips.
        assertThrows(ApplicationException::class.java) {
            helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                    versionString = "r2.0-alpha-1", databaseEngine = "mssql")
        }
    }

    /**
     * Default module with upgrade and bidirectional but no rollback scripts.
     */
    @Test
    fun defaultModuleTest4() {
        // Relax our rollback file existence constraint.
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings(rollbackScriptsMustExist = false)
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                versionString = "r2.0-alpha-1", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts, but len(rollback_scripts) != len(upgrade_scripts).
     */
    @Test
    fun defaultModuleTest5() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/standard")

        // With the default settings the process should fail because there are no rollback scrips.
        assertThrows(ApplicationException::class.java) {
            helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                    versionString = "r2.5.1.2-rc-1", databaseEngine = "mssql")
        }
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts, but the upgrade scripts have a sequencing error.
     */
    @Test
    fun defaultModuleInvalidTest1() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/invalid-tests/no-modules")
        assertThrows(ApplicationException::class.java) {
            helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                    versionString = "r1.0.1-alpha-3", databaseEngine = "mssql")
        }
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts, but the rollback scripts have a sequencing error.
     */
    @Test
    fun defaultModuleInvalidTest2() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/invalid-tests/no-modules")
        assertThrows(ApplicationException::class.java) {
            helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "standard-case",
                    versionString = "r1.0.2", databaseEngine = "mssql")
        }
    }

    @Test
    fun moduleTestForSpecificVersions() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)

        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/standard")

        helper.runTestForSpecificModuleVersion(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "accounting",
                versionString = "r1.0.0", databaseEngine = "mssql")
        helper.runTestForSpecificModuleVersion(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "customer",
                versionString = "r1.0", databaseEngine = "mssql")
        helper.runTestForSpecificModuleVersion(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "packages",
                versionString = "r0.1", databaseEngine = "mssql")
    }

    @Test
    fun moduleTestForLatestVersions() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(upgradeScriptOrder = BidirectionalFilesOrder.FIRST,
                rollbackScriptOrder = BidirectionalFilesOrder.EXCLUDE,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer1)

        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/standard")

        helper.runTestForLatestVersionInModule(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "accounting",
                expectedLatestVersionString = "r1.0.1", databaseEngine = "mssql")
        helper.runTestForLatestVersionInModule(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "customer",
                expectedLatestVersionString = "r1.1", databaseEngine = "mssql")
        helper.runTestForLatestVersionInModule(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "standard", moduleName = "packages",
                expectedLatestVersionString = "r0.2", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade, rollback and bidirectional scripts.
     */
    @Test
    fun defaultModuleSemver1Test1() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(
                upgradeScriptOrder = BidirectionalFilesOrder.LAST,
                rollbackScriptOrder = BidirectionalFilesOrder.LAST,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer2)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/semver1")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "semver-1",
                versionString = "1.22.890+20130313144700", databaseEngine = "mssql")
    }

    /**
     * Default module with upgrade and rollback but no bidirectional scripts.
     */
    @Test
    fun defaultModuleSemver1Test2() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(dbStatementDelimiter = "GO",
                dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer2)
        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/no-modules/scheme/semver1")
        helper.runDefaultModuleTest(directoryWithTestData = directoryWithTestData, testCaseLabel = "semver-2",
                versionString = "1.22.890+20130313144701", databaseEngine = "mssql")
    }

    @Test
    fun moduleTestForLatestSemver1Versions() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(upgradeScriptOrder = BidirectionalFilesOrder.FIRST,
                rollbackScriptOrder = BidirectionalFilesOrder.EXCLUDE,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer2)

        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/semver1")

        helper.runTestForLatestVersionInModule(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "semver1", moduleName = "finance",
                expectedLatestVersionString = "9.1.0+20191123084701", databaseEngine = "mssql")
        helper.runTestForLatestVersionInModule(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "semver1", moduleName = "hr",
                expectedLatestVersionString = "2.8.2+20191123004721", databaseEngine = "mssql")
    }

    @Test
    fun moduleTestForSpecificSemver1Version() {
        val deltaScriptDirectoryServiceSettings = DeltaScriptDirectoryServiceSettings()
        val versionServiceSettings = VersionServiceSettings()
        val migrationScriptOrderSettings = MigrationScriptSettings(upgradeScriptOrder = BidirectionalFilesOrder.FIRST,
                rollbackScriptOrder = BidirectionalFilesOrder.EXCLUDE,
                dbStatementDelimiter = "GO", dbChangeLogTableName = "dandelero_change_log")
        val helper = MigrationScriptComposerHelper(deltaScriptDirectoryServiceSettings, versionServiceSettings,
                migrationScriptOrderSettings, versionDeserialzer2)

        val directoryWithTestData = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/semver1")

        helper.runTestForSpecificModuleVersion(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "semver1", moduleName = "finance",
                versionString = "9.0.1+20191123004721", databaseEngine = "mssql")
        helper.runTestForSpecificModuleVersion(directoryWithTestData = directoryWithTestData,
                testIdentifier = "test-1", testCaseLabel = "semver1", moduleName = "hr",
                versionString = "0.1.0+20191123084701", databaseEngine = "mssql")
    }
}