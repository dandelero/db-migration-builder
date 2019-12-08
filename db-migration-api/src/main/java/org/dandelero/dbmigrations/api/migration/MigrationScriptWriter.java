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
package org.dandelero.dbmigrations.api.migration;

import java.util.Map;

/**
 * An interface for writing scripts to a sink.
 * <br />
 * Created at: 28/10/19 9:41 pm
 *
 * @author dandelero
 */
public interface MigrationScriptWriter {

    /**
     * Called when a new set of scripts is about to be processed.
     *
     * @param writerContext contains information about the scripts.
     */
    void setup(Map<String, Object> writerContext);

    /**
     * Called when processing is complete and output ought to be flushed.
     */
    void finish();

    /**
     * Called when script processing is about to commence.
     */
    void beginRegularScriptProcessing();

    /**
     * Called when all scripts have been processed and output ought to be flushed.
     */
    void finishRegularScriptProcessing();

    /**
     * Called when processing of bidirectional scripts is about to commence.
     */
    void beginBidirectionalScriptProcessing();

    /**
     * Called when all bidirectional scripts have been processed.
     */
    void finishBidirectionalScripts();

    /**
     * Called when a regular script is to be written.
     *
     * @param scriptContext the script context.
     */
    void writeRegularScript(Map<String, Object> scriptContext);

    /**
     * Called when a bidirectional script is to be written.
     *
     * @param scriptContext the script context.
     */
    void writeBidirectionalScript(Map<String, Object> scriptContext);

}
