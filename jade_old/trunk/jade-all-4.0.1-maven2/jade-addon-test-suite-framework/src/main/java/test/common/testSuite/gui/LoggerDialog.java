/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package test.common.testSuite.gui;

import test.common.Logger;

/**
 * @author Alessandro Negri - AOTLab UniPR
 * @author Matteo Bisi - UniPR
 * @author Yuri Ferrari - UniPR
 * @author Rossano Vitulli - UniPR
 * @version $Date: December 2003
 */

/**
 * Handle the logger type selection with a combo-box.
 */
public class LoggerDialog extends javax.swing.JDialog {
    
    static final int CANCEL = 0;
    static final int OK = 1;
    
    private int exitValue = CANCEL;
    
    
    /** Creates new form JDialog */
    public LoggerDialog(java.awt.Frame parent, int type) {
        super(parent, true);
        initComponents();
        for (int i=0; i < Logger.typeStringArray.length; i++) {
            jComboBox1.addItem(Logger.typeStringArray[i]);
        }
        jComboBox1.setSelectedIndex(type);
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jOkButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Logger Selection");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel1.setText("Select the logger");
        jLabel1.setMaximumSize(new java.awt.Dimension(70, 16));
        jLabel1.setMinimumSize(new java.awt.Dimension(70, 16));
        jLabel1.setPreferredSize(new java.awt.Dimension(70, 16));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(20, 45, 0, 45);
        getContentPane().add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(14, 32, 23, 32);
        getContentPane().add(jComboBox1, gridBagConstraints);

        jOkButton.setText("Ok");
        jOkButton.setMaximumSize(new java.awt.Dimension(60, 26));
        jOkButton.setMinimumSize(new java.awt.Dimension(60, 26));
        jOkButton.setPreferredSize(new java.awt.Dimension(60, 26));
        jOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOkButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 13, 11);
        getContentPane().add(jOkButton, gridBagConstraints);

        jCancelButton.setText("Cancel");
        jCancelButton.setMaximumSize(new java.awt.Dimension(60, 26));
        jCancelButton.setMinimumSize(new java.awt.Dimension(60, 26));
        jCancelButton.setPreferredSize(new java.awt.Dimension(60, 26));
        jCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 13, 11);
        getContentPane().add(jCancelButton, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void jOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOkButtonActionPerformed
      exitValue = OK;
      closeDialog(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jOkButtonActionPerformed

    private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCancelButtonActionPerformed
        exitValue = CANCEL;
        closeDialog(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jCancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jCancelButton;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jOkButton;
    // End of variables declaration//GEN-END:variables
 
    public int getSelectedLoggerType() {
        return jComboBox1.getSelectedIndex();
    }
    
    public int getExitValue() {
    	return exitValue;
    }
}