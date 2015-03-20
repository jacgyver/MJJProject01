/*
 * ErdTitlePanelData.java
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


package org.executequery.gui.erd;

import java.awt.Rectangle;

import java.io.Serializable;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Save data for the ERD title panel.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ErdTitlePanelData implements Serializable {
    
    /** The title panel bounds */
    private Rectangle titleBounds;
    /** The ERD name */
    private String erdName;
    /** The date stamp */
    private String erdDate;
    /** The description */
    private String erdDescription;
    /** The database type */
    private String erdDatabase;
    /** The author */
    private String erdAuthor;
    /** The file name */
    private String erdFileName;
    /** The revision number */
    private String erdRevision;
    
    public ErdTitlePanelData() {}
    
    public String getErdRevision() {
        return erdRevision;
    }
    
    public void setErdRevision(String erdRevision) {
        this.erdRevision = erdRevision;
    }
    
    public String getErdFileName() {
        return erdFileName;
    }
    
    public void setErdFileName(String erdFileName) {
        this.erdFileName = erdFileName;
    }
    
    public String getErdAuthor() {
        return erdAuthor;
    }
    
    public void setErdAuthor(String erdAuthor) {
        this.erdAuthor = erdAuthor;
    }
    
    public String getErdDatabase() {
        return erdDatabase;
    }
    
    public void setErdDatabase(String erdDatabase) {
        this.erdDatabase = erdDatabase;
    }
    
    public String getErdDescription() {
        return erdDescription;
    }
    
    public void setErdDescription(String erdDescription) {
        this.erdDescription = erdDescription;
    }
    
    public String getErdDate() {
        return erdDate;
    }
    
    public void setErdDate(String erdDate) {
        this.erdDate = erdDate;
    }
    
    public String getErdName() {
        return erdName;
    }
    
    public void setErdName(String erdName) {
        this.erdName = erdName;
    }
    
    public Rectangle getTitleBounds() {
        return titleBounds;
    }
    
    public void setTitleBounds(Rectangle titleBounds) {
        this.titleBounds = titleBounds;
    }
    
}




