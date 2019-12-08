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

import org.dandelero.dbmigrations.engine.test.util.TestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests for [DefaultDeltaScriptFileBuilder].
 * <br />
 * Created at: 4/11/19 2:45 pm
 * @author dandelero
 */
class DefaultDeltaScriptFileBuilderTest {

    /**
     * Builder under test.
     */
    private val builder = DefaultDeltaScriptFileBuilder()

    @Test
    fun testParseValidScript1() {
        val file = TestUtil.createTempFile("0001-create-ping_ping-table-199", "sql")
        val deltaScript = builder.build(file)
        assertNotNull(deltaScript, "Delta script not found")
        assertEquals(1, deltaScript!!.sequenceNumber, "Incorrect sequence number")
    }

    @Test
    fun testParseValidScript2() {
        val file = TestUtil.createTempFile("938821-create-ping_ping-table-199", "sql")
        val deltaScript = builder.build(file)
        assertNotNull(deltaScript, "Delta script not found")
        assertEquals(938821, deltaScript!!.sequenceNumber, "Incorrect sequence number")
    }

    @Test
    fun testParseInvalidScriptName1() {
        val file = TestUtil.createTempFile("-1-create-ping_ping-table-199", "sql")
        assertNull(builder.build(file), "Invalid file name should not be parsed")
    }

    @Test
    fun testParseInvalidScriptName2() {
        val file = TestUtil.createTempFile("a43-create-ping_ping-table-199", "sql")
        assertNull(builder.build(file), "Invalid file name should not be parsed")
    }
}