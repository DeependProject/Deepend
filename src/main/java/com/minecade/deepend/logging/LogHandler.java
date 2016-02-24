/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.logging;

import com.minecade.deepend.resources.DeependBundle;

import java.util.logging.ConsoleHandler;

/**
 * A simple console handler that will
 * set the formatter
 *
 * @see java.util.logging.ConsoleHandler
 * @see LogFormatter
 */
public class LogHandler extends ConsoleHandler {

    /**
     * Constructor
     * @param bundle Resources used for translation
     */
    protected LogHandler(final DeependBundle bundle) {
        setFormatter(new LogFormatter(bundle));
    }

}