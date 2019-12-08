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
package org.dandelero.dbmigrations.api.version;

/**
 * Defines a service that deserializes a version string into the version structure
 * that it represents.
 * <br />
 * Created at: 24/11/19 10:42 pm
 *
 * @author dandelero
 */
public interface VersionDeserializer {

    /**
     * Deserializes the given version string into a version object.
     *
     * @param versionString the string representation of the version.
     * @return the reconstructed version or null.
     */
    Version deserialize(String versionString);

}
