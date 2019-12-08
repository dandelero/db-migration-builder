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
package org.dandelero.dbmigrations.engine.version

import org.dandelero.dbmigrations.engine.version.standard.tag.DefaultPreReleaseTag
import org.dandelero.dbmigrations.engine.version.standard.tag.PreReleaseTagEnum

/**
 * Creates an alpha tag for the given sequence number and separator.
 * @param sequenceNumber the sequence number.
 * @param separator the separator string for the version tag.
 * @return the tag.
 */
fun alpha(sequenceNumber: Int, separator: String): DefaultPreReleaseTag {
    return DefaultPreReleaseTag(PreReleaseTagEnum.ALPHA, sequenceNumber, separator)
}

/**
 * Creates a beta tag for the given sequence number and separator.
 * @param sequenceNumber the sequence number.
 * @param separator the separator string for the version tag.
 * @return the tag.
 */
fun beta(sequenceNumber: Int, separator: String): DefaultPreReleaseTag {
    return DefaultPreReleaseTag(PreReleaseTagEnum.BETA, sequenceNumber, separator)
}

/**
 * Creates an RC tag for the given sequence number and separator.
 * @param sequenceNumber the sequence number.
 * @param separator the separator string for the version tag.
 * @return the tag.
 */
fun rc(sequenceNumber: Int, separator: String): DefaultPreReleaseTag {
    return DefaultPreReleaseTag(PreReleaseTagEnum.RELEASE_CANDIDATE, sequenceNumber, separator)
}