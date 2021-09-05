/*
 * Copyright (C) 2021 badi_
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package videorental;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Badi Moore
 * badi_moore@hotmail.com
 */
public class EditRatings extends javax.swing.JFrame {

    /**
     * Creates new form EditCrewRoles
     */
    public EditRatings(DBManager DB) {
        this.DB = DB;
        
        initComponents();
       
        DB.connect();
        loadRatings();
        DB.close(); 
    }
    
    private void loadRatings() {
        String sql = "SELECT RatingID, Designation, Minage FROM Rating;";
        ResultSet result = DB.makeQuery(sql);
        Utils.populateTable(jTableRatings, result);
    }
    
    private void closeWindow() {
        WindowEvent closingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelMain = new javax.swing.JPanel();
        jTextFieldNewRating = new javax.swing.JTextField();
        jLabelNewRating = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRatings = new javax.swing.JTable();
        jButtonAdd = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jLabelMinAge = new javax.swing.JLabel();
        jSpinnerMinAge = new javax.swing.JSpinner();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Age Ratings");
        setMinimumSize(new java.awt.Dimension(400, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setBackground(new java.awt.Color(250, 250, 250));
        jPanelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jTextFieldNewRating.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldNewRating.setMinimumSize(new java.awt.Dimension(140, 22));
        jTextFieldNewRating.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 0);
        jPanelMain.add(jTextFieldNewRating, gridBagConstraints);

        jLabelNewRating.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelNewRating.setText("New Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelNewRating, gridBagConstraints);

        jTableRatings.setAutoCreateRowSorter(true);
        jTableRatings.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableRatings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Rating Designation", "Minimum Age"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRatings.setFillsViewportHeight(true);
        jTableRatings.setRowHeight(20);
        jTableRatings.setShowHorizontalLines(false);
        jTableRatings.setShowVerticalLines(false);
        jScrollPane1.setViewportView(jTableRatings);
        if (jTableRatings.getColumnModel().getColumnCount() > 0) {
            jTableRatings.getColumnModel().getColumn(0).setMinWidth(0);
            jTableRatings.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableRatings.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelMain.add(jScrollPane1, gridBagConstraints);

        jButtonAdd.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAdd.setText("Add Rating");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        jPanelMain.add(jButtonAdd, gridBagConstraints);

        jButtonRemove.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemove.setText("Remove Rating");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        jPanelMain.add(jButtonRemove, gridBagConstraints);

        jLabelMinAge.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelMinAge.setText("Minimum Age");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelMinAge, gridBagConstraints);

        jSpinnerMinAge.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jSpinnerMinAge.setModel(new javax.swing.SpinnerNumberModel(12, 0, 18, 1));
        jSpinnerMinAge.setMinimumSize(new java.awt.Dimension(60, 22));
        jSpinnerMinAge.setPreferredSize(new java.awt.Dimension(60, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        jPanelMain.add(jSpinnerMinAge, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jPanelMain, gridBagConstraints);

        jButtonClose.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jButtonClose, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        jLabelNewRating.setForeground(Color.black);
        String newRating = jTextFieldNewRating.getText();
        Integer newMinAge = (Integer) jSpinnerMinAge.getModel().getValue();
        
        if (newRating.isBlank()) {
            jLabelNewRating.setForeground(Color.red);
            return;
        }
        
        String sql = "INSERT INTO Rating (RatingID, Designation, Minage, Description) VALUES (null, ?, ?, null);";
        ArrayList<Object> data = new ArrayList<>();
        
        data.add(newRating);
        data.add(newMinAge);
        
        DB.connect();
        DB.makeQuery(sql, data);
        loadRatings();
        DB.close();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        int selectedRow = jTableRatings.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        Integer selectedID = (Integer) Utils.getSelectedCellContent(selectedRow, 0, jTableRatings);
        String sql = "DELETE FROM Rating WHERE RatingID=?;";
        
        ArrayList<Object> data = new ArrayList<>();
        data.add(selectedID);
        
        DB.connect();
        DB.makeQuery(sql, data);
        loadRatings();
        DB.close();
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditRatings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditRatings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditRatings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditRatings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditRatings(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JLabel jLabelMinAge;
    private javax.swing.JLabel jLabelNewRating;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerMinAge;
    private javax.swing.JTable jTableRatings;
    private javax.swing.JTextField jTextFieldNewRating;
    // End of variables declaration//GEN-END:variables
    private DBManager DB;

}
