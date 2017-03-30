/*
 * IzPack - Copyright 2001-2009 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
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
package com.izforge.izpack.panels;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.installer.gui.IzPanel;
import com.izforge.izpack.util.Debug;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The panel that loads and sets variable values from a properties file.
 *
 * @author Michael Aichler (maichler@gmail.com)
 * @author Dasapich Thongnopnua
 */
public class LoadPropertiesPanel extends IzPanel {

    private static final String PROPERTIES_FILE_VARIABLE = "LoadPropertiesPanel.relativePropertiesPath";
    private static final long serialVersionUID = -1026088572140611703L;

    private final Log log;

    @SuppressWarnings("unused")
    public LoadPropertiesPanel(Panel panel, final InstallerFrame parent, GUIInstallData installData, Resources resources,
                               Log log) {
        super(panel, parent, installData, new IzPanelLayout(log), resources);
        this.log = log;
    }

    /**
     * Called when the panel becomes active.
     */
    @Override
    public void panelActivate() {

        try {
            loadVariables();
        }
        catch (Exception e) {
//            Debug.log(e);
        }

        parent.skipPanel();
    }

    /**
     * Set variables from the properties file.
     *
     * @throws Exception
     */
    public void loadVariables() throws Exception {

        String relativePath = installData.getVariable(PROPERTIES_FILE_VARIABLE);
        if (null == relativePath) {
//            log.fine(PROPERTIES_FILE_VARIABLE + " not set--skipping.");
            return;
        }

        File propertiesFile = new File(installData.getInstallPath(), relativePath);

        System.out.println("Searching for properties in " + propertiesFile);

        if (propertiesFile.exists()) {

            try (FileInputStream in = new FileInputStream(propertiesFile)) {

                Properties properties = new Properties();
                properties.load(in);

                for (Object key : properties.keySet()) {
                    System.out.println("Setting property " + key + "(" + properties.get(key) + ")");
                    installData.setVariable((String)key, (String)properties.get(key));
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("File not found");
        }
    }

    /**
     * Indicates whether the panel has been validated or not.
     *
     * @return Always true.
     */
    @Override
    protected boolean isValidated() {

        return true;
    }
}
