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
package org.dandelero.dbmigrations.engine.version.standard

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode

/**
 * A [org.dandelero.db.api.version.Version] may have an optional pre-release tag, such as `alpha-1`, `beta-2`, where a
 * tag is composed of a milestone and sequence number.
 *
 * <br />
 * Created at: 31/10/19 7:56 am
 * @param milestone the milestone (e.g. alpha, beta, etc)
 * @param weight the weight of the milestone for comparing against other milestones.
 * @param sequenceNumber the sequence number for the milestone (e.g. `3` for `alpha-3`)
 * @author dandelero
 */
open class PreReleaseTag(val milestone: String, private val weight: Int, val sequenceNumber: Int) : Comparable<PreReleaseTag> {

    init {
        if (milestone.trim().isEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("A milestone name is required for " +
                    "a pre-release tag"))
        }
        if (weight <= 0 || sequenceNumber <= 0) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("A valid weight and/or sequence " +
                    "is required"))
        }
    }

    /**
     * Compares this instance with the given tag.
     * @param other the other tag to compare with.
     * @return -1, 0, 1 based on the [Comparable] contract.
     */
    override fun compareTo(other: PreReleaseTag): Int {
        var result = weight.compareTo(other.weight)
        if (result != 0) {
            return result
        }

        return sequenceNumber.compareTo(other.sequenceNumber)
    }
}