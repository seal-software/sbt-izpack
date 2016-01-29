/*
 * sbt-izpack
 *
 * Copyright (c) 2014-2016, MediaCluster GmbH
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.izforge.izpack.compiler;

import org.junit.Test;

public class CompilerOutputWatcherTest {

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionOnWarnings() throws Exception {

        try {
            CompilerOutputWatcher.start();
            System.out.println("Warning: Some error");
        }
        finally {
            CompilerOutputWatcher.stop();
        }
    }

    @Test
    public void shouldResetSystemOutOnClose() throws Exception {

        CompilerOutputWatcher.start();
        CompilerOutputWatcher.stop();

        System.out.println("Warning: Some error");
    }
}