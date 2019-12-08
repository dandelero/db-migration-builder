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
package org.dandelero.dbmigrations.engine.version.standard.tag

import org.dandelero.dbmigrations.engine.version.alpha
import org.dandelero.dbmigrations.engine.version.beta
import org.dandelero.dbmigrations.engine.version.rc
import org.dandelero.dbmigrations.engine.version.standard.serder.DefaultPreReleaseTagDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * A suite of tests for [VersionTag] behaviour.
 * <br />
 * Created at: 1/11/19 4:48 pm
 * @author dandelero
 */
class DefaultPreReleaseTagTest {

    /**
     * The tag separator string.
     */
    private val tagSequenceSeparator = "_"

    /**
     * The deserializer instance under test.
     */
    private val deserializer = DefaultPreReleaseTagDeserializer(tagSequenceSeparator = tagSequenceSeparator)

    @Test
    fun simpleSerializationTest() {
        assertEquals(alpha(1, tagSequenceSeparator), deserializer.deserialize("alpha_1"))
        assertEquals(beta(10002, tagSequenceSeparator), deserializer.deserialize("beta_10002"))
        assertEquals(rc(3, tagSequenceSeparator), deserializer.deserialize("rc_3"))
    }

    @Test
    fun invalidVersionsDeserializationTest() {
        assertNull(deserializer.deserialize("alpha_01"))
        assertNull(deserializer.deserialize("aleph_01"))
        assertNull(deserializer.deserialize("beta_x1"))
        assertNull(deserializer.deserialize("rc_1x1"))
        assertNull(deserializer.deserialize("alpha_1-"))
    }
}