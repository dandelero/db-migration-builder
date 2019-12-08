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
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.module.ModuleService
import org.dandelero.dbmigrations.engine.module.DirectoryModule
import org.dandelero.dbmigrations.engine.module.DirectoryModuleService
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.dandelero.dbmigrations.engine.version.VersionedDirectory
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersion
import org.dandelero.dbmigrations.engine.version.standard.VersionWithTag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests around [DeltaScriptDirectoryService].
 * <br />
 * Created at: 1/11/19 9:17 pm
 * @author dandelero
 */
class DeltaScriptServiceTest {

    /**
     * The builder used to construct delta scripts from files.
     */
    private val builder = DefaultDeltaScriptFileBuilder()

    /**
     * Service settings.
     */
    private val settings = DeltaScriptDirectoryServiceSettings()

    /**
     * The service under test.
     */
    private val deltaScriptService = DeltaScriptDirectoryService(builder, settings)

    @Test
    fun findScriptsInModuleTest1() {
        val test1Directory = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/standard/test-1")
        val moduleService: ModuleService<DirectoryModule> = DirectoryModuleService(test1Directory)
        val accountingModule = moduleService.findModuleByName("accounting")
        assertNotNull(accountingModule, "Accounting module not found")

        val v1_0_1 = with(FourDigitVersion(".", 1, 0, 1)) {
            VersionWithTag(prefix = "r", prefixSeparator = "", version = this)
        }

        val version = VersionedDirectory(File(accountingModule.moduleDirectory, v1_0_1.nameString), v1_0_1)
        val upgradeScripts = deltaScriptService.getScripts(DeltaScriptCategory.UPGRADE, accountingModule, version)
        assertEquals(3, upgradeScripts.size, "Incorrect number of upgrade scripts returned")

        val rollbackScripts = deltaScriptService.getScripts(DeltaScriptCategory.ROLLBACK, accountingModule, version)
        assertEquals(3, rollbackScripts.size, "Incorrect number of rollback scripts returned")
    }

    @Test
    fun findNonExistentScriptsInModuleTest1() {
        val test1Directory = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/standard/test-1")
        val moduleService: ModuleService<DirectoryModule> = DirectoryModuleService(test1Directory)
        val customerModule = moduleService.findModuleByName("customer")
        assertNotNull(customerModule, "Customer module not found")

        val v1_0 = with(FourDigitVersion(".", 1, 0)) {
            VersionWithTag(prefix = "r", prefixSeparator = "", version = this)
        }

        val version = VersionedDirectory(File(customerModule.moduleDirectory, v1_0.nameString), v1_0)
        val upgradeScripts = deltaScriptService.getScripts(DeltaScriptCategory.UPGRADE, customerModule, version)
        assertEquals(3, upgradeScripts.size, "Incorrect number of upgrade scripts returned")

        val rollbackScripts = deltaScriptService.getScripts(DeltaScriptCategory.ROLLBACK, customerModule, version)
        assertEquals(3, rollbackScripts.size, "Incorrect number of rollback scripts returned")
    }
}