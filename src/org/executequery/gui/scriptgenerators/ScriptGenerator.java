/*
 * ScriptGenerator.java
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


package org.executequery.gui.scriptgenerators;

import java.util.Vector;

import org.executequery.gui.browser.ColumnData;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public interface ScriptGenerator {
    
    public String getScriptFilePath();
    
    public ColumnData[] getColumnDataArray(String tableName);
    
    public Vector getSelectedTables();
    
    public boolean hasSelectedTables();
    
    public boolean includeConstraints();
    
    public boolean includeConstraintsInCreate();
    
    public void setResult(int result);
    
    public void dispose();
    
    public boolean includeConstraintsAsAlter();
    
    public void enableButtons(boolean enable);
    
    public String getDatabaseProductName();
    
    public String getSchemaName();
    
}






