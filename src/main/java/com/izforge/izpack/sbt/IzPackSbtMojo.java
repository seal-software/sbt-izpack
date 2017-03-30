/*
 * sbt-izpack
 *
 * Copyright (c) 2014-2017, MediaCluster GmbH.
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
package com.izforge.izpack.sbt;

import com.izforge.izpack.api.data.Info;
import com.izforge.izpack.api.data.binding.IzpackProjectInstaller;
import com.izforge.izpack.compiler.CompilerConfig;
import com.izforge.izpack.compiler.container.CompilerContainer;
import com.izforge.izpack.compiler.data.CompilerData;
import com.izforge.izpack.compiler.data.PropertyManager;
import com.izforge.izpack.compiler.logging.MavenStyleLogFormatter;

import java.io.File;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Java helper class for the SBT task.
 *
 * <p>
 *     Ideas taken from the IzPack Maven Plugin ( by Anthonin Bonnefoy ).
 * </p>
 *
 * @author Michael Aichler (maichler@gmail.com)
 */
public class IzPackSbtMojo {

    /**
     * Format compression. Choices are bzip2, default.
     */
    public String comprFormat = "default";

    /**
     * Kind of installation. Choices are standard (default) or web.
     */
    public String kind = "standard";

    /**
     * Location of the IzPack installation file (src/main/izpack/install.xml).
     */
    public String installFile;

    /**
     * Base directory of compilation process (target/universal/stage).
     */
    public String baseDir;

    /**
     * Output where compilation result will be situate.
     */
    public String output;

    /**
     * Whether to automatically create parent directories of the output file.
     */
    public boolean mkdirs = false;

    /**
     * Compression level of the installation. Deactivated by default (-1).
     */
    public int comprLevel = -1;

    /**
     * Custom properties
     */
    public Properties properties = new Properties();

    /**
     * Custom log handler.
     */
    public Handler handler;


    /**
     * Executes the IzPack compiler.
     *
     * @throws Exception If a compiler error occurs.
     */
    public void execute() throws Exception {

        ClassLoader prevLoader = Thread.currentThread().getContextClassLoader();

        try {

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            File jarFile = new File(output);

            CompilerData compilerData = initCompilerData(jarFile);
            CompilerContainer compilerContainer = new CompilerContainer();
            compilerContainer.addConfig("installFile", installFile);
            compilerContainer.getComponent(IzpackProjectInstaller.class);
            compilerContainer.addComponent(CompilerData.class, compilerData);
            compilerContainer.addComponent(Handler.class, createLogHandler());

            CompilerConfig compilerConfig = compilerContainer.getComponent(CompilerConfig.class);

            PropertyManager propertyManager = compilerContainer.getComponent(PropertyManager.class);
            initSbtProperties(propertyManager);

            compilerConfig.executeCompiler();
        }
        finally {
            Thread.currentThread().setContextClassLoader(prevLoader);
        }
    }

    private void initSbtProperties(PropertyManager propertyManager) {

        for (String propertyName : properties.stringPropertyNames()) {

            String value = properties.getProperty(propertyName);
            propertyManager.addProperty(propertyName, value);
        }
    }

    private CompilerData initCompilerData(File jarFile) {

        return new CompilerData(comprFormat, kind, installFile, null, baseDir, jarFile.getPath(),
                mkdirs, comprLevel, new Info());
    }

    private Handler createLogHandler() {

        if (null != handler) {
            return handler;
        }

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new MavenStyleLogFormatter());
        Level level = Level.INFO;
        consoleHandler.setLevel(level);
        return consoleHandler;
    }
}
