/*
 * TabComponent.java
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


package org.executequery.base;

import java.awt.Component;
import javax.swing.Icon;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Represents a component within one of the application tab panes.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class TabComponent {

    /** The component for this tab */
    private Component component;
    
    /** The title for this tab */
    private String title;
    
    /** The tool tip for this tab */
    private String toolTip;
    
    /** The icon for this tab */
    private Icon icon;
    
    /** the index of this component */
    private int index;
    
    /** the containers position */
    private int position;
    
    /** the layout name of this tab component */
    private String layoutName;
    
    /** a possible title suffix */
    private String titleSuffix;
    
    /** the index of this tab for tabs with the same name */
    private int sameTitleIndex;

    /** the tab's display name */
    private String displayName;

    /** Creates a new instance of TabComponent */
    public TabComponent() {}
    
    public TabComponent(Component component,
                        String title, 
                        Icon icon, 
                        String toolTip) {
        this(-1, component, title, icon, toolTip);
    }

    public TabComponent(int index,
                        Component component,
                        String title, 
                        Icon icon, 
                        String toolTip) {
        this.index = index;
        this.component = component;
        this.title = title;
        this.icon = icon;
        this.toolTip = toolTip;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }
    
    public String getDisplayName() {
        if (displayName == null) {
            if (titleSuffix != null) {
                displayName = title + titleSuffix;
            } else {
                displayName = title;
            }
        }
        return displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        displayName = null;
        this.title = title;
    }

    public boolean hasIcon() {
        return (icon != null);
    }
    
    public boolean hasToolTipText() {
        return (toolTip != null);
    }
    
    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
 
    public String toString() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLayoutName() {
        if (layoutName == null) {
            layoutName = Integer.toString(hashCode());
        }
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getTitleSuffix() {
        return titleSuffix;
    }

    public void setTitleSuffix(String titleSuffix) {
        this.titleSuffix = titleSuffix;
    }

    public int getSameTitleIndex() {
        return sameTitleIndex;
    }

    public void setSameTitleIndex(int sameTitleIndex) {
        this.sameTitleIndex = sameTitleIndex;
    }
    
}



