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
package org.dandelero.dbmigrations.engine.module

import org.dandelero.dbmigrations.api.module.ModuleService
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests around [org.dandelero.db.api.module.Module]s.
 * <br />
 * Created at: 31/10/19 10:56 pm
 * @author dandelero
 */
class ModuleTest {

    @Test
    fun directoryModuleTest() {
        val test1Directory = TestUtil.getRequiredDirectoryOnClasspath("input/with-modules/scheme/standard/test-1")
        val moduleService: ModuleService<DirectoryModule> = DirectoryModuleService(test1Directory)
        assertNull(moduleService.findModuleByName("foo"), "No module expected")
        val allModules: List<String> = moduleService.listAllModules().map { it.name }
        val expectedModules = listOf("accounting", "customer", "packages")
        assertEquals(expectedModules.size, allModules.size, "Incorrect number of modules")
        val missingModules = expectedModules.dropWhile { allModules.contains(it) }
        assertEquals(0, missingModules.size, "Missing module(s):$missingModules")
    }
}