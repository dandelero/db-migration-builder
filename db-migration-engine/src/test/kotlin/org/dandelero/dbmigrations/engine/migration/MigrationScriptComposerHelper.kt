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
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.delta.DeltaScriptService
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.api.module.ModuleService
import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.dandelero.dbmigrations.engine.delta.DefaultDeltaScriptFileBuilder
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryService
import org.dandelero.dbmigrations.engine.delta.DeltaScriptDirectoryServiceSettings
import org.dandelero.dbmigrations.engine.delta.DeltaScriptFileBuilder
import org.dandelero.dbmigrations.engine.delta.template.ClasspathDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.module.DirectoryModule
import org.dandelero.dbmigrations.engine.module.DirectoryModuleService
import org.dandelero.dbmigrations.engine.module.NoModuleDirectoryModuleService
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.dandelero.dbmigrations.engine.util.isContainedIn
import org.dandelero.dbmigrations.engine.util.loadProperties
import org.dandelero.dbmigrations.engine.util.readFully
import org.dandelero.dbmigrations.engine.version.DirectoryVersionService
import org.dandelero.dbmigrations.engine.version.VersionServiceSettings
import org.dandelero.dbmigrations.engine.version.VersionedDirectory
import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory
import java.io.File

/**
 * An end-to-end test!
 * <br />
 * Created at: 31/10/19 7:47 am
 * @param deltaScriptDirectoryServiceSettings customisation of processing of the [DeltaScriptDirectoryService].
 * @param versionServiceSettings settings for controlling the [DirectoryVersionService].
 * @param migrationScriptOrderSettings specifies the migration script values to be used when generating the overall script.
 * @author dandelero
 */
class MigrationScriptComposerHelper(
        private val deltaScriptDirectoryServiceSettings: DeltaScriptDirectoryServiceSettings,
        private val versionServiceSettings: VersionServiceSettings,
        private val migrationScriptOrderSettings: MigrationScriptSettings,
        private val versionDeserialzer: VersionDeserializer
) {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(MigrationScriptComposerHelper::class.java)

    /**
     * The file separator.
     */
    private val sep = File.separator

    /**
     * The template locator for templates to be applied to delta scripts.
     */
    private val deltaScriptTemplateLocator: DeltaScriptTemplateLocator = ClasspathDeltaScriptTemplateLocator()

    /**
     * The service for operating on versions in modules.
     */
    private val versionService = DirectoryVersionService(versionServiceSettings, versionDeserialzer)

    /**
     * The name of the upgrade migration script.
     */
    private val upgradeFileName = "upgrade.txt"

    /**
     * The name of the rollback migration script.
     */
    private val rollbackFileName = "rollback.txt"

    /**
     * The output directory that will be written to.
     */
    private val scriptOutputDirectory = TestUtil.createTempDirectory()

    /**
     * The migration script that will be written to.
     */
    private val migrationScriptUpgradeFile = File(scriptOutputDirectory, upgradeFileName)

    /**
     * The rollback script that will be written to.
     */
    private val migrationScriptRollbackFile = File(scriptOutputDirectory, rollbackFileName)

    /**
     * A script writer factory that writes migration scripts to predefined files.
     */
    private val migrationScriptWriterFactory = TestMigrationScriptWriterFactory(migrationScriptUpgradeFile,
            migrationScriptRollbackFile, deltaScriptTemplateLocator)

    /**
     * Constructs [org.dandelero.db.api.delta.DeltaScript] instances from files.
     */
    private val deltaScriptFileBuilder: DeltaScriptFileBuilder = DefaultDeltaScriptFileBuilder()

    /**
     * The service to load delta scripts with.
     */
    private val deltaScriptService: DeltaScriptService<DirectoryModule, VersionedDirectory> =
            DeltaScriptDirectoryService(deltaScriptFileBuilder, deltaScriptDirectoryServiceSettings)

    /**
     * The list of developer machine host names, which is used to determine whether (initial) expected output is to
     * be generated upon running of the test for the first time.
     */
    private val developerMachines: List<String> = with(TestUtil.getRequiredFileOnClasspath("test.properties")
            .loadProperties()) { getProperty("env.developer.hosts").split(",") }

    /**
     * @param directoryWithTestData the directory containing the test data.
     * @param testCaseLabel the identifier label for the test case.
     * @param versionString the version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    fun runDefaultModuleTest(
            directoryWithTestData: File,
            testCaseLabel: String,
            versionString: String,
            databaseEngine: String
    ) {
        val moduleService: ModuleService<DirectoryModule> = NoModuleDirectoryModuleService(directoryWithTestData)
        val defaultModule = moduleService.findModuleByName("")

        val composer = MigrationScriptComposer(moduleService = moduleService,
                versionService = versionService,
                deltaScriptService = deltaScriptService,
                migrationScriptWriterFactory = migrationScriptWriterFactory,
                migrationScriptSettings = migrationScriptOrderSettings)

        // The default module test output should be created in an indicative folder.
        val testIdentifier = directoryWithTestData.name + sep + testCaseLabel

        generateAndAssertMigrationScriptForSpecificVersionOfModule(
                composer = composer, testIdentifier = testIdentifier, module = defaultModule,
                versionString = versionString, databaseEngine = databaseEngine
        )
    }

    /**
     * @param directoryWithTestData the directory containing the test data.
     * @param testIdentifier the name of the directory that contains the modules to be processed.
     * @param testCaseLabel the label for the test case.
     * @param moduleName the name of the module to generate the migration script for.
     * @param expectedLatestVersionString the expected latest version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    fun runTestForLatestVersionInModule(
            directoryWithTestData: File,
            testIdentifier: String,
            testCaseLabel: String,
            moduleName: String,
            expectedLatestVersionString: String,
            databaseEngine: String
    ) {
        // Add in the test identifier into the directory structure as it contains our modules.
        val moduleBaseDir = File(directoryWithTestData, testIdentifier)
        val moduleService: ModuleService<DirectoryModule> = DirectoryModuleService(moduleBaseDir)

        val composer = MigrationScriptComposer(moduleService = moduleService,
                versionService = versionService,
                deltaScriptService = deltaScriptService,
                migrationScriptWriterFactory = migrationScriptWriterFactory,
                migrationScriptSettings = migrationScriptOrderSettings)

        // The module test output should be created in an indicative folder.
        val testId = directoryWithTestData.name + sep + testIdentifier + sep + testCaseLabel

        generateAndAssertMigrationScriptForLatestVersionOfModule(moduleService, composer, testId,
                moduleName, expectedLatestVersionString, databaseEngine)
    }

    /**
     * @param directoryWithTestData the directory containing the test data.
     * @param testIdentifier the identifying directory the test data resides in.
     * @param testCaseLabel the label for the test case.
     * @param moduleName the name of the module to generate the migration script for.
     * @param versionString the version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    fun runTestForSpecificModuleVersion(
            directoryWithTestData: File,
            testIdentifier: String,
            testCaseLabel: String,
            moduleName: String,
            versionString: String,
            databaseEngine: String
    ) {

        // Add in the test identifier into the directory structure as it contains our modules.
        val moduleBaseDir = File(directoryWithTestData, testIdentifier)
        val moduleService: ModuleService<DirectoryModule> = DirectoryModuleService(moduleBaseDir)

        val composer = MigrationScriptComposer(
                moduleService = moduleService,
                versionService = versionService,
                deltaScriptService = deltaScriptService,
                migrationScriptWriterFactory = migrationScriptWriterFactory,
                migrationScriptSettings = migrationScriptOrderSettings)

        val module = moduleService.findModuleByName(moduleName)
        assertNotNull(module, "No such module: $moduleName")

        // The module test output should be created in an indicative folder.
        val directoryPath = directoryWithTestData.name + sep + testIdentifier + sep + testCaseLabel

        generateAndAssertMigrationScriptForSpecificVersionOfModule(composer, directoryPath, module, versionString,
                databaseEngine)
    }

    /**
     * Generates the migration script for the test identified by the given parameters and asserts
     * the generated output against the expected output.
     * @param moduleService the service for interacting with modules.
     * @param composer the composer for creating the migration script.
     * @param testIdentifier the name of the directory containing the tests beneath the input folder.
     * @param moduleName the name of the module to generate the migration script for.
     * @param latestVersionString the expected latest version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    private fun generateAndAssertMigrationScriptForLatestVersionOfModule(
            moduleService: ModuleService<DirectoryModule>,
            composer: MigrationScriptComposer<DirectoryModule, VersionedDirectory>,
            testIdentifier: String,
            moduleName: String,
            latestVersionString: String,
            databaseEngine: String
    ) {
        // Ensure the module exists.
        val module = moduleService.findModuleByName(moduleName)
        assertNotNull(module, "Module '$moduleName' not found")
        generateAndAssertMigrationScriptForLatestVersionOfModule(composer, testIdentifier, module, latestVersionString,
                databaseEngine)
    }

    /**
     * Generates the migration script for the test identified by the given parameters and asserts
     * the generated output against the expected output.
     * @param composer the composer for creating the migration script.
     * @param testIdentifier the name of the directory containing the tests beneath the input folder.
     * @param versionString the expected version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    private fun generateAndAssertMigrationScriptForSpecificVersionOfModule(
            composer: MigrationScriptComposer<DirectoryModule, VersionedDirectory>,
            testIdentifier: String,
            module: DirectoryModule,
            versionString: String,
            databaseEngine: String
    ) {
        val moduleName = module.name

        // Check the version is as expected.
        val requiredVersion = versionService.getVersion(module, versionString)
        assertNotNull(requiredVersion, "Version not found for module '$moduleName'")
        assertEquals(versionString, requiredVersion!!.nameString, "Mismatch in version number of module '$moduleName'")

        // Now compose the migration script.
        composer.composeMigrationScriptForModuleVersion(databaseEngine, module, requiredVersion)
        val actualUpgradeOutput = migrationScriptUpgradeFile.readFully() ?: fail<String>("Upgrade file not generated")

        // Rollback file is only generated if there were input scripts.
        val actualRollbackOutput = migrationScriptRollbackFile.readFully()
                ?: if (deltaScriptDirectoryServiceSettings.rollbackScriptsMustExist) {
                    fail<String>("Rollback file not generated")
                } else {
                    ""
                }

        // Read the expected output.
        // Note that we will only generate the expected output if this is running on a developer machine, and if the
        // expected output does not already exist.
        val expectedOutputPair = readOrCreateExpectedOutput(developerMachines, testIdentifier,
                if (module.isDefault) "" else moduleName, versionString, actualUpgradeOutput, actualRollbackOutput)
        val expectedUpgradeOutput = expectedOutputPair.first
        val expectedRollbackOutput = expectedOutputPair.second

        // We want to ignore timestamp lines.
        val dynamicLineIndicator: (String) -> Boolean = { line: String -> line.isContainedIn("Created on", "Created by") }

        // Compare the upgrade script.
        val upgradeOutputComparisonResult = TestUtil.compareLineByLine(
                expectedUpgradeOutput.split(System.lineSeparator()),
                actualUpgradeOutput.split(System.lineSeparator()), dynamicLineIndicator)
        if (upgradeOutputComparisonResult != null) {
            fail<String>(upgradeOutputComparisonResult)
        }

        // Compare the rollback script.
        val rollbackOutputComparisonResult = TestUtil.compareLineByLine(
                expectedRollbackOutput.split(System.lineSeparator()),
                actualRollbackOutput.split(System.lineSeparator()), dynamicLineIndicator)
        if (rollbackOutputComparisonResult != null) {
            fail<String>(rollbackOutputComparisonResult)
        }
    }

    /**
     * Generates the migration script for the test identified by the given parameters and asserts
     * the generated output against the expected output.
     * @param composer the composer for creating the migration script.
     * @param testIdentifier the name of the directory containing the tests beneath the input folder.
     * @param latestVersionString the expected latest version to generate the migration script for.
     * @param databaseEngine the database engine to generate the migration script for.
     */
    private fun generateAndAssertMigrationScriptForLatestVersionOfModule(
            composer: MigrationScriptComposer<DirectoryModule, VersionedDirectory>,
            testIdentifier: String,
            module: DirectoryModule,
            latestVersionString: String,
            databaseEngine: String
    ) {
        val moduleName = module.name

        // Check the latest version is as expected.
        val latestVersion = versionService.latestVersion(module)
        assertNotNull(latestVersion, "Latest version not found for module '$moduleName'")
        assertEquals(latestVersionString, latestVersion!!.nameString, "Mismatch in latest version number of " +
                "module '$moduleName'")

        // Now compose the migration script.
        composer.composeMigrationScriptForLatestVersionInModule(databaseEngine, module)
        val actualUpgradeOutput = migrationScriptUpgradeFile.readFully() ?: fail<String>("Upgrade file not generated")

        // Rollback file is only generated if there were input scripts.
        val actualRollbackOutput = migrationScriptRollbackFile.readFully()
                ?: fail<String>("Rollback file not generated")

        // Read the expected output.
        // Note that we will only generate the expected output if this is running on a developer machine, and if the
        // expected output does not already exist.
        val expectedOutputPair = readOrCreateExpectedOutput(developerMachines, testIdentifier,
                moduleName, latestVersionString, actualUpgradeOutput, actualRollbackOutput)
        val expectedUpgradeOutput = expectedOutputPair.first
        val expectedRollbackOutput = expectedOutputPair.second

        // We want to ignore timestamp lines.
        val dynamicLineIndicator: (String) -> Boolean = { line: String ->
            line.isContainedIn(
                    "Created on", "Created by")
        }

        // Compare the upgrade script.
        val upgradeOutputComparisonResult = TestUtil.compareLineByLine(
                expectedUpgradeOutput.split(System.lineSeparator()),
                actualUpgradeOutput.split(System.lineSeparator()), dynamicLineIndicator)
        if (upgradeOutputComparisonResult != null) {
            fail<String>(upgradeOutputComparisonResult)
        }

        // Compare the rollback script.
        val rollbackOutputComparisonResult = TestUtil.compareLineByLine(
                expectedRollbackOutput.split(System.lineSeparator()),
                actualRollbackOutput.split(System.lineSeparator()), dynamicLineIndicator)
        if (rollbackOutputComparisonResult != null) {
            fail<String>(rollbackOutputComparisonResult)
        }
    }

    /**
     * Reads the expected output for the given test parameters, and creates the expected output if it does not exist
     * or it ought to be regenerated.
     * @param permittedMachines the list of machines that are allowed to write to the src/test/resources directory.
     * @param testIdentifier the test identifier, typically the directory name.
     * @param moduleName the name of the module to process.
     * @param releaseDirName the name of the release directory to process.
     * @param actualUpgradeOutput the upgrade file output.
     * @param actualRollbackOutput the rollback file output.
     * @param regenerateExpectedOutputFiles whether the expected output files ought to be regenerated.
     * @return a pair of strings containing the expected output for the upgrade and rollback tests respectively.
     */
    private fun readOrCreateExpectedOutput(
            permittedMachines: List<String>,
            testIdentifier: String,
            moduleName: String,
            releaseDirName: String,
            actualUpgradeOutput: String,
            actualRollbackOutput: String,
            regenerateExpectedOutputFiles: Boolean = false
    ): Pair<String, String> {
        val expectedOutputFiles = readExceptedOutput(testIdentifier, moduleName, releaseDirName,
                listOf(upgradeFileName, rollbackFileName))
        if (regenerateExpectedOutputFiles || expectedOutputFiles.size != 2) {
            // Regenerate
            logger.info("Generating the expected output files")
            writeExpectedOutput(permittedMachines, testIdentifier, moduleName, releaseDirName,
                    listOf(upgradeFileName to actualUpgradeOutput, rollbackFileName to actualRollbackOutput), true)

            // Now that we've generated the output files they won't be on the classpath.
            // So we can just return the inputs.
            return Pair(actualUpgradeOutput, actualRollbackOutput)
        }

        return Pair(expectedOutputFiles[upgradeFileName]
                ?: error("No file: $upgradeFileName"), expectedOutputFiles[rollbackFileName]
                ?: error("No file: $rollbackFileName"))
    }

    /**
     * Reads the preconfigured expected output for the test with the given parameters.
     * @param testIdentifier the test identifier, typically the directory name.
     * @param moduleName the name of the module to process.
     * @param releaseDirName the name of the release directory to process.
     * @param fileNames the list of file names to be retrieved.
     * @return a mapping from the file name to the file contents.
     */
    private fun readExceptedOutput(
            testIdentifier: String,
            moduleName: String,
            releaseDirName: String,
            fileNames: List<String>
    ): Map<String, String> {
        val directoryPath = if (moduleName.isEmpty()) {
            "expected_output$sep$testIdentifier$sep$releaseDirName$sep"
        } else {
            "expected_output$sep$testIdentifier$sep$moduleName$sep$releaseDirName$sep"
        }

        return fileNames.mapNotNull { fileName ->
            val fileObject = TestUtil.getFileOnClasspath(directoryPath + sep + fileName)
            if (fileObject == null) {
                null
            } else {
                Pair(fileName, fileObject.readText())
            }
        }.toMap()
    }

    /**
     * Writes output to the src/test/resources directory beneath a directory structure that reflects the executing
     * test, module and release directory.
     * @param permittedMachines the list of machines that are allowed to write to the src/test/resources directory.
     * @param testIdentifier the test identifier, typically the directory name.
     * @param moduleName the name of the module to process.
     * @param releaseDirName the name of the release directory to process.
     * @param filesAndContents a list of tuples containing the file name and file contents to be written.
     * @param rewriteFiles whether existing files are to be re-written.
     */
    private fun writeExpectedOutput(
            permittedMachines: List<String>,
            testIdentifier: String,
            moduleName: String,
            releaseDirName: String,
            filesAndContents: List<Pair<String, String>>,
            rewriteFiles: Boolean
    ) {
        val directoryPath = if (moduleName.isEmpty()) {
            "$testIdentifier$sep$releaseDirName$sep"
        } else {
            "$testIdentifier$sep$moduleName$sep$releaseDirName$sep"
        }

        val directory = TestUtil.writeExpectedOutputDirectory(permittedMachines = permittedMachines,
                relativeDirectoryPath = directoryPath)
                ?: throw ApplicationException(ErrorCode.RESOURCE_ERROR
                        .withDetails("Directory could not be created at $directoryPath"))

        filesAndContents.forEach { (fileName, fileContents) ->
            val fileToWrite = File(directory, fileName)
            if (!fileToWrite.exists() || rewriteFiles) {
                logger.debug("Writing file at ${fileToWrite.absolutePath}")
                fileToWrite.writeText(fileContents)
            }
        }
    }
}