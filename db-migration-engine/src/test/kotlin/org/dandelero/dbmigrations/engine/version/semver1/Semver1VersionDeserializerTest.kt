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
package org.dandelero.dbmigrations.engine.version.semver1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests for [Semver1VersionDeserializer].
 * <br />
 * Created at: 20/11/19 10:48 pm
 * @author dandelero
 */
class Semver1VersionDeserializerTest {

    /**
     * The deserializer instance under test.
     */
    private val deserializer = Semver1VersionDeserializer(digitSeparator = ".", dateSeparator = "+",
            dateFormatString = "yyyyMMddHHmmss")

    private val deserializer2 = Semver1VersionDeserializer(digitSeparator = "-", dateSeparator = "_",
            dateFormatString = "yyyy.MM.dd_HH:mm:ss")

    @Test
    fun serializationTest() {
        assertDateSerialization1("1.22.890+20130313144700")
        assertDateSerialization1("1.22.890+20191221013259")
    }

    @Test
    fun serializationTest2() {
        assertDateSerialization2("1-22-890_2013.03.13_14:47:00")
        assertDateSerialization2("1-22-890_2019.12.21_01:32:59")
    }

    @Test
    fun invalidInputSerializationTest() {
        assertNull(deserializer.deserialize("1.22.890+20131313144700"), "Invalid month should fail")
        assertNull(deserializer.deserialize("1.22.890+20130333144700"), "Invalid day should fail")
        assertNull(deserializer.deserialize("1.22.890+20130313244700"), "Invalid hour should fail")
        assertNull(deserializer.deserialize("1.22.890+20130313146700"), "Invalid minute should fail")
        assertNull(deserializer.deserialize("1.22.890+20130313144760"), "Invalid second should fail")
    }

    private fun assertDateSerialization1(versionString: String) {
        val version = deserializer.deserialize(versionString)
        assertNotNull(version, "Deserialization failed")
        assertEquals(versionString, version!!.nameString, "Incorrect constructed version")
    }

    private fun assertDateSerialization2(versionString: String) {
        val version = deserializer2.deserialize(versionString)
        assertNotNull(version, "Deserialization failed")
        assertEquals(versionString, version!!.nameString, "Incorrect constructed version")
    }
}