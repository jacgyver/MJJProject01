/*
 * Token.java
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

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * A token represents a smallest meaningful fragment 
 * of text, such as a word, that is recognised.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/08/24 09:37:14 $
 */
public class Token {

    private int style;
    private int startIndex;
    private int endIndex;

    public Token(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /** Creates a new instance of BraceToken */
    public Token(int style, int startIndex, int endIndex) {
        this.style = style;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public boolean isValid() {
        return (startIndex != -1) || (endIndex != -1);
    }

    public void reset() {
        style = -1;
        startIndex = -1;
        endIndex = -1;
    }

    public void reset(int style, int startIndex, int endIndex) {
        this.style = style;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getLength() {
        return endIndex - startIndex;
    }

    public boolean intersects(int startOffset, int endOffset) {
        return ((startIndex <= startOffset) && (endIndex >= endOffset)) ||
               ((startIndex <= startOffset) && (endIndex <= endOffset)) ||
               ((startIndex >= startOffset) && (endIndex <= endOffset));
    }

    public boolean contains(int startOffset, int endOffset) {
        return (startIndex <= startOffset) && (endIndex >= endOffset);
    }

    public boolean contains(int index) {
        return contains(index, index);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean equals(Object object) {
        if (object instanceof Token) {
            Token _token = (Token)object;
            return _token.getStartIndex() == startIndex &&
                   _token.getEndIndex() == endIndex &&
                   _token.getStyle() == style;
        }
        return false;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Token - style: ").
           append(style).
           append(" startIndex: ").
           append(startIndex).
           append(" endIndex: ").
           append(endIndex);
        return sb.toString();
    }
    
}



