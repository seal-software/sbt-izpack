package custom;

import java.awt.LayoutManager2;
import java.util.ArrayList;

import javax.swing.JLabel;

import com.izforge.izpack.api.GuiId;
import com.izforge.izpack.api.data.Info;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.gui.LayoutConstants;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.installer.gui.IzPanel;

public class CustomHelloPanel extends IzPanel {


    private static final long serialVersionUID = 3257848774955905587L;

    public CustomHelloPanel(Panel panel, InstallerFrame parent, GUIInstallData installData,
                      Resources resources, Log log) {
        this(panel, parent, installData, new IzPanelLayout(log), resources);
    }

    public CustomHelloPanel(Panel panel, InstallerFrame parent, GUIInstallData installData,
                      LayoutManager2 layout, Resources resources) {
        super(panel, parent, installData, layout, resources);

        String welcomeText = "Hello from sbt-izpack custom panel";

        JLabel welcomeLabel = LabelFactory.create(welcomeText, parent.getIcons().get("host"),
                LayoutConstants.LEADING);

        add(welcomeLabel, LayoutConstants.NEXT_LINE);

        getLayoutHelper().completeLayout();
    }

    public boolean isValidated()
    {
        return true;
    }
}