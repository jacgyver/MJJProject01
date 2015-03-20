/*
 * BrowserLauncherUtils.java
 *
 * Copyright (C) 2002, 2003, 2004, 2005, 2006 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.executequery.util;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import org.executequery.GUIUtilities;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/20 02:59:49 $
 */
public class BrowserLauncherUtils {

    public static void launch(String url) {
        try {
            BrowserLauncher launcher = new BrowserLauncher(null);
            BrowserLauncherRunner runner = 
                    new BrowserLauncherRunner(launcher, url, null);
            Thread launcherThread = new Thread(runner);
            launcherThread.start();
        } 
        catch (BrowserLaunchingInitializingException e) {
            handleException(e);
        }
        catch (UnsupportedOperatingSystemException e) {
            handleException(e);
        }
    }
    
    private static void handleException(Throwable e) {
        GUIUtilities.displayExceptionErrorDialog(
                "Error launching local web browser:\n" + 
                e.getMessage(), e);
    }
    
    /** Creates a new instance of BrowserLauncherUtils */
    private BrowserLauncherUtils() {}
    
}
