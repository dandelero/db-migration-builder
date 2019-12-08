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

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A suit of tests around [Semver1Version].
 * <br />
 * Created at: 20/11/19 10:18 pm
 * @author dandelero
 */
class SemverVersionTest {

    private val dateFormatString: String = "yyyyMMddHHmmss"

    @Test
    fun versionComparisionDifferentVersionSameDateTest() {
        val ldt = LocalDateTime.of(
                LocalDate.of(2018, 3, 30),
                LocalTime.of(21, 3, 58))

        val v1_0_3 = Semver1Version(digitSeparator = ".", major = 1, minor = 0, patch = 3, dateSeparator = "+",
                dateFormat = dateFormatString, date = ldt)
        assertEquals("1.0.3+20180330210358", v1_0_3.nameString, "Incorrect namestring")

        val v1_0_1 = Semver1Version(digitSeparator = ".", major = 1, minor = 0, patch = 1, dateSeparator = "+",
                dateFormat = dateFormatString, date = ldt)
        assertEquals("1.0.1+20180330210358", v1_0_1.nameString, "Incorrect namestring")

        assertEquals(-1, v1_0_1.compareTo(v1_0_3))
        assertEquals(1, v1_0_3.compareTo(v1_0_1))
    }

    @Test
    fun versionComparisionSameVersionDifferentDateTest() {
        val date1 = LocalDateTime.of(
                LocalDate.of(2018, 3, 30),
                LocalTime.of(21, 3, 58))
        val v1_0_3a = Semver1Version(digitSeparator = ".", major = 1, minor = 0, patch = 3, dateSeparator = "+",
                dateFormat = dateFormatString, date = date1)
        assertEquals("1.0.3+20180330210358", v1_0_3a.nameString, "Incorrect namestring")

        val date2 = LocalDateTime.of(
                LocalDate.of(2018, 3, 30),
                LocalTime.of(9, 3, 58))
        val v1_0_3b = Semver1Version(digitSeparator = ".", major = 1, minor = 0, patch = 3, dateSeparator = "+",
                dateFormat = dateFormatString, date = date2)
        assertEquals("1.0.3+20180330090358", v1_0_3b.nameString, "Incorrect namestring")

        assertEquals(-1, v1_0_3b.compareTo(v1_0_3a))
        assertEquals(1, v1_0_3a.compareTo(v1_0_3b))
    }
}