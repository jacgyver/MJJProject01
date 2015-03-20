/*
 * DefaultTable.java
 *
 * Created on 15 July 2006, 23:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.executequery.gui;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.underworldlabs.swing.plaf.UIUtils;
import org.underworldlabs.swing.table.DefaultTableHeaderRenderer;

/** 
 * Default table display using a custom header renderer.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/07/15 13:14:49 $
 */
public class DefaultTable extends JTable {
    
    /** Creates a new instance of DefaultTable */
    public DefaultTable() {
        this(null);
    }
    
    /** Creates a new instance of DefaultTable */
    public DefaultTable(TableModel model) {
        super(model);
        init();
    }    
    
    public DefaultTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }
    
    private void init() {
        if (UIUtils.isDefaultLookAndFeel() || UIUtils.usingOcean()) {
            getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        }
    }
    
}
