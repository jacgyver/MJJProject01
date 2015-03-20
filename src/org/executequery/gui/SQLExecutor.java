/*
 * SQLExecutor.java
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


package org.executequery.gui;

import java.sql.SQLException;
import java.sql.ResultSet;

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
public interface SQLExecutor {
    
    /** <p>Sets the text within status bar's right-hand
     *  message area to the specified value.
     *
     *  @param the text to display
     */
    public void setRightStatusText(String text);
    
    /** <p>Sets the text within status bar's activity
     *  message area to the specified value.
     *
     *  @param the text to display
     */
    public void setActivityStatusText(String text);
    
    /** <p>Enables/disables the stop/cancel button
     *  as specified.
     *
     *  @param <code>true</code> to enable,
     *         <code>false</code> otherwise
     */
    public void setStopButtonEnabled(boolean enable);
    
    /** <p>Sets the result text within the designated
     *  statement results area to the specified values.
     *
     *  @param the result of the executed query (update)
     *  @param the type of statement executed
     */
    public void setResultText(int result, int type);
    
    /** <p>Sets the text within status bar's left-hand
     *  message area to the specified value.
     *
     *  @param the text to display
     */
    public void setLeftStatusText(String text);
    
    /** <p>Sets the error message text within the
     *  designated area to the specified value. This
     *  will usually send <code>SQLException</code>
     *  messages or dumps.
     *
     *  @param the error message to display
     */
    public void setOutputMessage(int type, String text);

    /** <p>Interrupts any executing statement. */
    public void interrupt();

    /** <p>Sets the table results to the specified
     *  <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     */
    public void setResultSet(ResultSet rs) throws SQLException;

    /** <p>Sets the table results to the specified
     *  <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @param the executed query of the result set
     */
    public void setResultSet(ResultSet rs, String query) throws SQLException;

    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @return the row count of this <code>ResultSet</code> object
     */
    public int setResultSet(ResultSet rs, boolean showRowNumber) throws SQLException;

    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @param whether the row count is displayed
     *  @param the executed query of the result set
     *  @return the row count of this <code>ResultSet</code> object
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber, String query)
      throws SQLException;

    /** <p>Adds the specified SQL statement to the statement
     *  history list if available.
     *
     *  @param the statement to add
     */
    public void addToHistory(String statement);
    
    /** Indicates whether a statement is currently executing.
     *
     *  @param <code>true</code> if executing, <code>false</code>
     *         otherwise
     */
    public void setExecuting(boolean executing);
    
}






