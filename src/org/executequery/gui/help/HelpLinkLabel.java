/*
 * HelpLinkLabel.java
 *
 * Created on 16 September 2006, 15:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.executequery.gui.help;

import com.sun.java.help.impl.JHSecondaryViewer;
import java.awt.event.ActionEvent;
import org.executequery.util.BrowserLauncherUtils;
import org.underworldlabs.util.MiscUtils;

/**
 *
 * @author takisd
 */
public class HelpLinkLabel extends JHSecondaryViewer {

    private String mouseOverText;

    private String urlRedirect;

    public HelpLinkLabel() {
        super();
        setViewerActivator("javax.help.LinkLabel");
    }

    public void actionPerformed(ActionEvent e) {
        String redirect = getUrlRedirect();
        if (!MiscUtils.isNull(redirect)) {
            BrowserLauncherUtils.launch(redirect);
        }
    }

    public String getUrlRedirect() {
        return urlRedirect;
    }

    public void setUrlRedirect(String urlRedirect) {
        this.urlRedirect = urlRedirect;
    }

    public String getMouseOverText() {
        return mouseOverText;
    }

    public void setMouseOverText(String mouseOverText) {
        this.mouseOverText = mouseOverText;
        setToolTipText(mouseOverText);
    }
    
}
