/*
 * CharLimitedTextField.java
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


package org.underworldlabs.swing;

import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

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
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class CharLimitedTextField extends JTextField {

    private int maxLength;
    private CharLimitedDocument charLimitedDocument;
    
    public CharLimitedTextField(int maxLength) {
        this.maxLength = maxLength;
        if (charLimitedDocument == null) {
            charLimitedDocument = new CharLimitedDocument();
        }
    }
    
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    public int getMaxLength() {
        return maxLength;
    }

    protected Document createDefaultModel() {
        if (charLimitedDocument == null) {
            charLimitedDocument = new CharLimitedDocument();
        }
        return charLimitedDocument;
    }
 
    class CharLimitedDocument extends PlainDocument {

        private Toolkit toolkit;

        public CharLimitedDocument() {
            toolkit = Toolkit.getDefaultToolkit();
        }

        public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
            if (getLength() >= maxLength) {
                toolkit.beep();
                return;
            }
            super.insertString(offs, str, a);
        }
    }

}



