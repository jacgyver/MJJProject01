/*
 * NumberTextField.java
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

import java.text.NumberFormat;
import java.text.ParseException;

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
public class NumberTextField extends JTextField {
    
    private Toolkit toolkit;
    private NumberFormat integerFormatter;
    private WholeNumberDocument numberDocument;
    private int digits;
    
    public NumberTextField() {
        super();
        
        if (numberDocument == null)
            numberDocument = new WholeNumberDocument();
        
        digits = -1;
        numberDocument.setDigits(digits);
        toolkit = Toolkit.getDefaultToolkit();
        integerFormatter = NumberFormat.getNumberInstance();
        integerFormatter.setParseIntegerOnly(true);
    }
    
    public NumberTextField(int digits) {
        this();
        numberDocument.setDigits(digits);
        this.digits = digits;
    }
    
    public void setDigits(int digits) {
        this.digits = digits;
    }
    
    public int getDigits() {
        return digits;
    }
    
    public int getValue() {
        int retVal = 0;
        try {
            retVal = integerFormatter.parse(getText()).intValue();
        } catch (ParseException e) {
            toolkit.beep();
        }
        return retVal;
    }
    
    public String getStringValue() {
        return Integer.toString(getValue());
    }
    
    public boolean isZero() {
        return getValue() == 0;
    }
    
    public void setValue(int value) {
        setText(integerFormatter.format(value));
    }
    
    protected Document createDefaultModel() {
        
        if (numberDocument == null)
            numberDocument = new WholeNumberDocument();
        
        return numberDocument;
        
    }
    
}


class WholeNumberDocument extends PlainDocument {
    
    private Toolkit toolkit;
    private int digits;
    
    public WholeNumberDocument() {
        toolkit = Toolkit.getDefaultToolkit();
    }
    
    public int getDigits() {
        return digits;
    }
    
    public void setDigits(int digits) {
        this.digits = digits;
    }
    
    public void insertString(int offs, String str, AttributeSet a)
        throws BadLocationException {

        if (digits != -1) {
            
            if (getLength() >= digits) {
                toolkit.beep();
                return;
            }

        }

        int j = 0;
        char[] source = str.toCharArray();
        char[] result = new char[source.length];
        
        for (int i = 0; i < result.length; i++) {
            
            if (Character.isDigit(source[i])) {
                result[j++] = source[i];
            } else {
                toolkit.beep();
            }
            
        }
        
        super.insertString(offs, new String(result, 0, j), a);
    }
    
} // class WholeNumberDocument



