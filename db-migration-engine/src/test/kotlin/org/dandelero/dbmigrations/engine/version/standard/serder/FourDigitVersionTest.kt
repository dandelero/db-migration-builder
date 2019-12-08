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
package org.dandelero.dbmigrations.engine.version.standard.serder

import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersion
import org.dandelero.dbmigrations.engine.version.simple.FourDigitVersionDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests around the [org.dandelero.dbmigrations.engine.version.scheme.FourDigitNumberScheme] class.
 * <br />
 * Created at: 1/11/19 8:06 am
 * @author dandelero
 */
class FourDigitVersionTest {

    /**
     * The digit separator string.
     */
    private val digitSeparator = "."

    /**
     * The deserializer instance under test.
     */
    private val deserializer = FourDigitVersionDeserializer(digitSeparator = digitSeparator)

    @Test
    fun simpleDeserializationTest() {
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3), deserializer.deserialize("1.2.3"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3, 4), deserializer.deserialize("1.2.3.4"))
        assertEquals(FourDigitVersion(digitSeparator, 0, 0, 0, 0), deserializer.deserialize("0.0.0.0"))
        assertEquals(FourDigitVersion(digitSeparator, 0, 0, 0, 10), deserializer.deserialize("0.0.0.10"))
    }

    @Test
    fun versionsWithAdditionalCharsDeserializationTest() {
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2.?"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2."))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2.x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2), deserializer.deserialize("1.2.x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3), deserializer.deserialize("1.2.3x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3, 4), deserializer.deserialize("1.2.3.4x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3), deserializer.deserialize("1.2.3.x"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3), deserializer.deserialize("1.2.3.?"))
        assertEquals(FourDigitVersion(digitSeparator, 1, 2, 3), deserializer.deserialize("1.2.3."))
    }

    @Test
    fun invalidVersionsDeserializationTest() {
        // 1 digit.
        assertNull(deserializer.deserialize("r1"))
        assertNull(deserializer.deserialize("1"))
        assertNull(deserializer.deserialize("1."))
        assertNull(deserializer.deserialize("1x"))
        // 2 digits.
        assertNull(deserializer.deserialize("1.x"))
        assertNull(deserializer.deserialize("r1.2"))
        // 3 digits.
        assertNull(deserializer.deserialize("r1.2.3"))
        // 4 digits.
        assertNull(deserializer.deserialize("r1.2.3.4"))
    }
}