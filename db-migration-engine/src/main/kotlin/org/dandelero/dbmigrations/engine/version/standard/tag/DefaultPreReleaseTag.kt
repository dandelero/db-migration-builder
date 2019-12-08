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

import org.dandelero.dbmigrations.engine.version.standard.PreReleaseTag

/**
 * The default [PreReleaseTag] implementation used by the application.
 * <br />
 * Created at: 31/10/19 8:05 am
 * @param tag the tag.
 * @param sequenceNumber the sequence number of the tag.
 * @param separator the separator string to use between label and sequence number.
 * @author dandelero
 */
class DefaultPreReleaseTag(tag: PreReleaseTagEnum, sequenceNumber: Int, private val separator: String) :
    PreReleaseTag(milestone = tag.label, weight = tag.weight, sequenceNumber = sequenceNumber) {

    /**
     * Compares this instance with the other for equivalence.
     * @param other the other instance.
     * @return true if they are equivalent.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultPreReleaseTag
        if (this.compareTo(other) != 0) {
            return milestone == other.milestone
        }
        return true
    }

    /**
     * @return the hashcode value.
     */
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    /**
     * @return the string representation of this tag.
     */
    override fun toString(): String {
        return milestone + separator + sequenceNumber
    }
}