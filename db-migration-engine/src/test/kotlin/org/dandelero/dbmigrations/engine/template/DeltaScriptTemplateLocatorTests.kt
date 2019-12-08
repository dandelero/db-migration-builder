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
package org.dandelero.dbmigrations.engine.template

import java.io.File
import org.dandelero.dbmigrations.api.delta.DeltaScriptCategory
import org.dandelero.dbmigrations.api.delta.DeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.template.ClasspathDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.template.DirectoryDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.delta.template.PeckingOrderDeltaScriptTemplateLocator
import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests around [DeltaScriptTemplateLocator]s.
 * <br />
 * Created at: 28/10/19 8:10 pm
 * @author dandelero
 */
class DeltaScriptTemplateLocatorTests {

    /**
     * The template locator that locates files on the classpath.
     */
    private val cpTemplateLocator = ClasspathDeltaScriptTemplateLocator()

    /**
     * The temp directory that is written to.
     */
    private val tempDirectory = TestUtil.createTempDirectory()

    /**
     * The template locator that locates files in a temp directory.
     */
    private val dirTemplateLocator = DirectoryDeltaScriptTemplateLocator(tempDirectory)

    /**
     * The template locator that is used by the application.
     */
    private val combinedDeltaScriptTemplateLocator: DeltaScriptTemplateLocator = PeckingOrderDeltaScriptTemplateLocator(
            dirTemplateLocator, cpTemplateLocator)

    @Test
    fun classpathLocatorTests() {
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.UPGRADE),
                "Script not found")
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.ROLLBACK),
                "Script not found")
        assertNotNull(cpTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.BIDIRECTIONAL),
                "Script not found")
    }

    @Test
    fun directoryLocatorTests() {
        assertNull(dirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.UPGRADE),
                "Script not found")

        // Now create a directory with a single template
        val mssqlTemplateDirectory = File(tempDirectory, "mssql")
        mssqlTemplateDirectory.mkdir()
        val upgradeTemplate = File(mssqlTemplateDirectory, "upgrade_template.txt")
        upgradeTemplate.writeText("This is the template")

        assertNotNull(dirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.UPGRADE),
                "Script not found")
    }

    @Test
    fun combinedLocatorTests() {
        val rollbackTemplateContentsInCp = combinedDeltaScriptTemplateLocator.findDeltaScriptTemplate("mssql",
                DeltaScriptCategory.ROLLBACK)
        assertNotNull(rollbackTemplateContentsInCp, "Script not found")

        assertNull(dirTemplateLocator.findDeltaScriptTemplate("mssql", DeltaScriptCategory.ROLLBACK),
                "No rollback script expected for rollback")

        // Now create a directory with a single template
        val mssqlTemplateDirectory = File(tempDirectory, "mssql")
        mssqlTemplateDirectory.mkdir()
        val rollbackTemplate = File(mssqlTemplateDirectory, "rollback_template.txt")
        rollbackTemplate.writeText("This is the template")

        // Obtain the template - we should now find the new file instead.
        val rollbackTemplateContentsFromCombinedLocator = combinedDeltaScriptTemplateLocator.findDeltaScriptTemplate(
                "mssql", DeltaScriptCategory.ROLLBACK)
        assertNotNull(rollbackTemplateContentsFromCombinedLocator, "Script not found")
        assertNotEquals(rollbackTemplateContentsInCp, rollbackTemplateContentsFromCombinedLocator,
                "Incorrect locator behaviour")
    }
}