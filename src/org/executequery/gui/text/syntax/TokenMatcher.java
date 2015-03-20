/*
 * TokenMatcher.java
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


package org.executequery.gui.text.syntax;

import java.util.regex.Matcher;
import javax.swing.text.Style;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Represents a matcher and style combo
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:57 $
 */
public class TokenMatcher {
    
    private int type;
    private Style style;
    private Matcher matcher;

    /** Creates a new instance of TokenMatcher */
    public TokenMatcher(int type, Style style, Matcher matcher) {
        this.type = type;
        this.style = style;
        this.matcher = matcher;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}



