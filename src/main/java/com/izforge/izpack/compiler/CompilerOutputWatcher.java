/*
 * sbt-izpack
 *
 * Copyright (c) 2014-2016, MediaCluster GmbH.
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Watches compiler output and throws exceptions on warnings.
 *
 * @author Michael Aichler (maichler@gmail.com)
 */
public class CompilerOutputWatcher extends PrintStream {

    private final PrintStream systemOut;
    private final OutputStream nullOutputStream = new NullOutputStream();
    private static CompilerOutputWatcher instance = null;

    private CompilerOutputWatcher() {
        super(System.out);
        this.systemOut = System.out;
        System.setOut(this);
    }

    /**
     * Replaces {@link System#out} with an instance of this listener.
     */
    public static synchronized void start() {
        if (null == instance) {
            instance = new CompilerOutputWatcher();
        }
    }

    /**
     * Restores {@link System#out} to its previous value, if replaced.
     */
    public static synchronized void stop() {
        if (null != instance) {
            instance.close();
            instance = null;
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            this.out = nullOutputStream;
            super.close();
            System.setOut(systemOut);
        }
    }

    @Override
    public void println(String x) {
        if (x.startsWith("Warning:")) {
            throw new RuntimeException(x);
        }
        super.println(x);
    }

    /**
     * An output stream which does nothing.
     *
     * @author Michael Aichler (maichler@gmail.com)
     */
    class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
        }
    }
}
