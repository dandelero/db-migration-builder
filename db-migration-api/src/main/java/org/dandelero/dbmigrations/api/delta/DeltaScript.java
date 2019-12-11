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
package org.dandelero.dbmigrations.api.delta;

import org.jetbrains.annotations.NotNull;

/**
 * A delta script is a script containing a change to a database resources, such as adding a column, adding an index, etc.
 * Delta scripts have a sequence number that dictates the order in which they are applied.
 * <br />
 * Created at: 29/10/19 7:35 am
 *
 * @author dandelero
 */
public interface DeltaScript extends Comparable<DeltaScript> {

    /**
     * @return the name of this script.
     */
    String getName();

    /**
     * @return the sequence number of this script.
     */
    int getSequenceNumber();

    /**
     * @return the contents of this script.
     */
    String getContents();

    @Override
    default int compareTo(@NotNull DeltaScript other) {
        if (other == null) {
            return 1;
        }
        return Integer.valueOf(getSequenceNumber()).compareTo(other.getSequenceNumber());
    }
}
