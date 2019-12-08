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

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.engine.version.standard.PreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.tag.DefaultPreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.tag.PreReleaseTagEnum

/**
 * A deserializer for the [PreReleaseTag].
 * <br />
 * Created at: 1/11/19 4:47 pm
 *
 * @param tagSequenceSeparator the separator string between the tag components.
 * @author dandelero
 */
class DefaultPreReleaseTagDeserializer(private val tagSequenceSeparator: String) : PreReleaseTagDeserializer {

    init {
        if (tagSequenceSeparator.isNullOrEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("The tag separator cannot be empty"))
        }
    }

    /**
     * Deserializes the given version tag string into a version tag object.
     * @param tagString the string representation of the version tag.
     * @return the reconstructed version tag or null.
     */
    override fun deserialize(tagString: String): PreReleaseTag? {
        if (tagString.isNullOrEmpty()) {
            return null
        }
        val parts = tagString.split(tagSequenceSeparator)
        if (parts.size != 2) {
            return null
        }

        val versionTag = PreReleaseTagEnum.fromLabel(parts[0]) ?: return null
        val seqNumString = parts[1]
        if (seqNumString.isEmpty() || !seqNumString[0].isDigit() || seqNumString[0] == '0') {
            return null
        }

        val sequenceNumber = seqNumString.toIntOrNull() ?: return null
        return DefaultPreReleaseTag(versionTag, sequenceNumber, tagSequenceSeparator)
    }
}