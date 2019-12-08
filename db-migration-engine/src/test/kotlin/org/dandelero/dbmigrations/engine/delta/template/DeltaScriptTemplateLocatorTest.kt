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
package org.dandelero.dbmigrations.engine.delta.template

import java.io.File
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests around [TemplateLocator] functionality.
 * <br />
 * Created at: 10/11/19 10:55 am
 * @author dandelero
 */
class DeltaScriptTemplateLocatorTest {

    private val cpTemplateLocator = ClasspathDeltaScriptTemplateLocator()

    @Test
    fun simpleTemplateLocatorTest() {
        assertNotNull(cpTemplateLocator.findMigrationScriptFileTemplate(), "No migration template found")
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.UPGRADE),
                "No upgrade delta script template found")
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.ROLLBACK),
                "No rollback delta script template found")
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.BIDIRECTIONAL),
                "No bidirectional delta script template found")

        assertNull(cpTemplateLocator.findDeltaScriptTemplate("unsupported", DeltaScriptCategory.UPGRADE),
                "No upgrade delta script template expected")
        assertNull(cpTemplateLocator.findDeltaScriptTemplate("unsupported", DeltaScriptCategory.ROLLBACK),
                "No rollback delta script template expected")
        assertNull(cpTemplateLocator.findDeltaScriptTemplate("unsupported", DeltaScriptCategory.BIDIRECTIONAL),
                "No bidirectional delta script template expected")

        val emptyDirTemplateLocator = DirectoryDeltaScriptTemplateLocator(TestUtil.createTempDirectory())
        assertNull(emptyDirTemplateLocator.findMigrationScriptFileTemplate(), "No migration template expected")
        assertNull(emptyDirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.UPGRADE),
                "No upgrade delta script template expected")
        assertNull(emptyDirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.ROLLBACK),
                "No rollback delta script template expected")
        assertNull(emptyDirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.BIDIRECTIONAL),
                "No bidirectional delta script template expected")
    }

    @Test
    fun compositeTemplateLocatorTest() {
        val engine = "piebase"
        val dir = TestUtil.createTempDirectory()
        // Create the migration file template.
        val newFileTemplate = File(dir, "file_template.txt")
        newFileTemplate.writeText("__file_template__")

        // Now create the template directory for our engine.
        val dbEngineDir = File(dir, engine)
        dbEngineDir.mkdirs()

        // Add in an upgrade and rollback script, but no bidirectional script.
        val upgradeTemplate = File(dbEngineDir, "upgrade_template.txt")
        upgradeTemplate.writeText("__upgrade__")
        val rollbackTemplate = File(dbEngineDir, "rollback_template.txt")
        rollbackTemplate.writeText("__rollback__")

        // Now create a template locator using a directory then classpath composite locator.
        val templateLocator = PeckingOrderDeltaScriptTemplateLocator(
                DirectoryDeltaScriptTemplateLocator(dir),
                ClasspathDeltaScriptTemplateLocator())
        val migrationScriptFileTemplate = templateLocator.findMigrationScriptFileTemplate()
        assertNotNull(migrationScriptFileTemplate, "No migration script template found")
        assertEquals("__file_template__", migrationScriptFileTemplate, "Incorrect file template contents")

        assertEquals("__upgrade__", templateLocator.findDeltaScriptTemplate(engine, DeltaScriptCategory.UPGRADE),
                "Incorrect upgrade script contents")
        assertEquals("__rollback__", templateLocator.findDeltaScriptTemplate(engine, DeltaScriptCategory.ROLLBACK),
                "Incorrect rollback script contents")
        assertNull(templateLocator.findDeltaScriptTemplate(engine, DeltaScriptCategory.BIDIRECTIONAL),
                "No bidirectional delta script template expected")
    }
}