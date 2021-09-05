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

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author badi_
 */
public class ViewRental extends javax.swing.JFrame {

    /**
     * Creates new form ViewRental
     */
    public ViewRental(DBManager DB, Integer rentalID) {
        this.DB = DB;
        this.rentalID = rentalID;
        
        initComponents();
        
        String sql = "SELECT Customer.CustomerID, CONCAT(Customer.Firstname, ' ', Customer.Lastname), Filmrental.Rentdate, ADDDATE(Filmrental.Rentdate, Filmrental.Rentlength),"
                + " Filmrental.Totalprice, Filmrental.Returned FROM Customer"
                + " INNER JOIN Filmrental USING(CustomerID)"
                + " WHERE FilmrentalID = ?;";
        ArrayList<Object> data = new ArrayList<>();
        
        data.add(rentalID);
        DB.connect();
        ResultSet result = DB.makeQuery(sql, data);
        
        sql = "SELECT Film.Title, Media.Name FROM Film"
                + " INNER JOIN Filmmedia USING (FilmID)"
                + " INNER JOIN Media USING (MediaID)"
                + " INNER JOIN Rentalitem USING (FilmmediaID)"
                + " WHERE Rentalitem.FilmrentalID = ?";
        data.clear();
        data.add (rentalID);
        ResultSet filmResult = DB.makeQuery(sql, data);
        DB.close();
        
        try {
            if (result.next()) {
                jLabelCustomerName.setText(result.getString(2) + " (#" + result.getInt(1) + ")");
                jLabelRentDate.setText(result.getDate(3).toString());
                jLabelDueDate.setText(result.getDate(4).toString());
                jLabelTotalPrice.setText(String.valueOf(result.getFloat(5)));
                boolean returned = result.getBoolean(6);
                if (returned) {
                    jCheckBoxReturned.setSelected(true);
                }
                Utils.populateTable(jTableFilms, filmResult);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
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
        jLabelCustomerLabel = new javax.swing.JLabel();
        jLabelCustomerName = new javax.swing.JLabel();
        jLabelRentDateLabel = new javax.swing.JLabel();
        jLabelRentDate = new javax.swing.JLabel();
        jLabelDueLabel = new javax.swing.JLabel();
        jLabelDueDate = new javax.swing.JLabel();
        jLabelPriceLabel = new javax.swing.JLabel();
        jLabelTotalPrice = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFilms = new javax.swing.JTable();
        jLabelRentedItems = new javax.swing.JLabel();
        jLabelReturned = new javax.swing.JLabel();
        jCheckBoxReturned = new javax.swing.JCheckBox();
        jButtonClose = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("View Rental Event");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setBackground(new java.awt.Color(250, 250, 250));
        jPanelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jLabelCustomerLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelCustomerLabel.setText("Customer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelCustomerLabel, gridBagConstraints);

        jLabelCustomerName.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelCustomerName.setText("Donald Duck (#1)");
        jLabelCustomerName.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelCustomerName, gridBagConstraints);

        jLabelRentDateLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRentDateLabel.setText("Rent Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelRentDateLabel, gridBagConstraints);

        jLabelRentDate.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRentDate.setText("rented");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelRentDate, gridBagConstraints);

        jLabelDueLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelDueLabel.setText("Due Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelDueLabel, gridBagConstraints);

        jLabelDueDate.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelDueDate.setText("duedate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelDueDate, gridBagConstraints);

        jLabelPriceLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelPriceLabel.setText("Total Price");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelPriceLabel, gridBagConstraints);

        jLabelTotalPrice.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelTotalPrice.setText("price");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelTotalPrice, gridBagConstraints);

        jTableFilms.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableFilms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Media"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableFilms.setFillsViewportHeight(true);
        jTableFilms.setRowHeight(20);
        jTableFilms.setShowGrid(true);
        jTableFilms.setShowHorizontalLines(false);
        jTableFilms.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableFilms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jScrollPane1, gridBagConstraints);

        jLabelRentedItems.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRentedItems.setText("Rented Items");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelMain.add(jLabelRentedItems, gridBagConstraints);

        jLabelReturned.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelReturned.setText("Returned");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jLabelReturned, gridBagConstraints);

        jCheckBoxReturned.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jCheckBoxReturned.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelMain.add(jCheckBoxReturned, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(jButtonClose, gridBagConstraints);

        jButtonDelete.setText("Delete Event");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        getContentPane().add(jButtonDelete, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonCloseActionPerformed

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
            java.util.logging.Logger.getLogger(ViewRental.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewRental.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewRental.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewRental.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewRental(null, 0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JCheckBox jCheckBoxReturned;
    private javax.swing.JLabel jLabelCustomerLabel;
    private javax.swing.JLabel jLabelCustomerName;
    private javax.swing.JLabel jLabelDueDate;
    private javax.swing.JLabel jLabelDueLabel;
    private javax.swing.JLabel jLabelPriceLabel;
    private javax.swing.JLabel jLabelRentDate;
    private javax.swing.JLabel jLabelRentDateLabel;
    private javax.swing.JLabel jLabelRentedItems;
    private javax.swing.JLabel jLabelReturned;
    private javax.swing.JLabel jLabelTotalPrice;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableFilms;
    // End of variables declaration//GEN-END:variables
    DBManager DB;
    Integer rentalID;
}
