/*
 * ImportExportWizardModel.java
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


package org.executequery.gui.importexport;

import org.underworldlabs.swing.wizard.DefaultWizardProcessModel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ImportExportWizardModel extends DefaultWizardProcessModel {

    /** the number of steps in this model */
    private int stepsCount;
    
    /** Creates a new instance of ImportExportWizardModel */
    public ImportExportWizardModel(int stepsCount, int transferType) {
        this.stepsCount = stepsCount;

        String firstTitle = "Database Connection and Export Type";
        String fifthTitle = "Exporting Data...";
        if (transferType == ImportExportProcess.IMPORT) {
            firstTitle = "Database Connection and Import Type";
            fifthTitle = "Importing Data...";
        }

        String[] titles = new String[stepsCount];
        titles[0] = firstTitle;
        titles[1] = "Table Selection";
        titles[2] = "Data File Selection";
        titles[3] = "Options";
        titles[4] = fifthTitle;
        setTitles(titles);

        String[] steps = {"Select database connection and transfer type",
                          "Select the tables/columns",
                          transferType == ImportExportProcess.IMPORT ?
                              "Select the data file(s) to import from" :
                              "Select the data file(s) to export to",
                          "Set any further transfer options",
                          transferType == ImportExportProcess.IMPORT ?
                              "Import the data" :
                              "Export the data"};
        setSteps(steps);
    }
    
}



