/*
 * SmoothGradientComboBoxEditor.java
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


package org.underworldlabs.swing.plaf.smoothgradient;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxEditor;

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
class SmoothGradientComboBoxEditor extends BasicComboBoxEditor {

  public SmoothGradientComboBoxEditor() {
    editor = new JTextField("",9) {
        // workaround for 4530952
        public void setText(String s) {
            if (getText().equals(s)) {
                return;
            }
            super.setText(s);
        }
      };

      editor.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
  }
    
    /**
     * A subclass of BasicComboBoxEditor that implements UIResource.
     * BasicComboBoxEditor doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with BasicListCellRenderer subclasses.
     */
    public static final class UIResource extends SmoothGradientComboBoxEditor
    	implements javax.swing.plaf.UIResource {
    }

}




