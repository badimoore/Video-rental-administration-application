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
import javax.swing.JOptionPane;

/**
 *
 * @author badi_
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();

        setLocationRelativeTo(null); // moves window to center of screen

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

        jTabbedPaneLoginSelect = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jButtonConnect = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabelUserName = new javax.swing.JLabel();
        jTextFieldUsername = new javax.swing.JTextField();
        jPasswordFieldUserPWD = new javax.swing.JPasswordField();
        jLabelUserPWD = new javax.swing.JLabel();
        jLabelShowPassword1 = new javax.swing.JLabel();
        jButtonShowUserPWD = new javax.swing.JButton();
        jPanelServer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldAddress = new javax.swing.JTextField();
        jTextFieldPort = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldUser = new javax.swing.JTextField();
        jPasswordFieldPWD = new javax.swing.JPasswordField();
        jLabelPort = new javax.swing.JLabel();
        jButtonShowPWD = new javax.swing.JButton();
        jLabelShowPassword = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Videorental login");
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(250, 250, 250));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jButtonConnect.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonConnect.setText("Connect");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 5, 30, 0);
        jPanel2.add(jButtonConnect, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel6.setText("Please enter login information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 30);
        jPanel2.add(jLabel6, gridBagConstraints);

        jLabelUserName.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelUserName.setText("Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 30, 0, 0);
        jPanel2.add(jLabelUserName, gridBagConstraints);

        jTextFieldUsername.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldUsername.setMinimumSize(new java.awt.Dimension(140, 24));
        jTextFieldUsername.setPreferredSize(new java.awt.Dimension(140, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 5, 0, 0);
        jPanel2.add(jTextFieldUsername, gridBagConstraints);

        jPasswordFieldUserPWD.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jPasswordFieldUserPWD.setMinimumSize(new java.awt.Dimension(140, 24));
        jPasswordFieldUserPWD.setPreferredSize(new java.awt.Dimension(140, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 5, 0, 0);
        jPanel2.add(jPasswordFieldUserPWD, gridBagConstraints);

        jLabelUserPWD.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelUserPWD.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 30, 0, 0);
        jPanel2.add(jLabelUserPWD, gridBagConstraints);

        jLabelShowPassword1.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabelShowPassword1.setText("Show Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(23, 5, 0, 0);
        jPanel2.add(jLabelShowPassword1, gridBagConstraints);

        jButtonShowUserPWD.setMaximumSize(new java.awt.Dimension(20, 20));
        jButtonShowUserPWD.setMinimumSize(new java.awt.Dimension(20, 20));
        jButtonShowUserPWD.setPreferredSize(new java.awt.Dimension(20, 20));
        jButtonShowUserPWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonShowUserPWDMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonShowUserPWDMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(19, 5, 0, 0);
        jPanel2.add(jButtonShowUserPWD, gridBagConstraints);

        jTabbedPaneLoginSelect.addTab("User", jPanel2);

        jPanelServer.setBackground(new java.awt.Color(250, 250, 250));
        jPanelServer.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel1.setText("Please enter database information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 30);
        jPanelServer.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setText("Address");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 0);
        jPanelServer.add(jLabel2, gridBagConstraints);

        jTextFieldAddress.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldAddress.setMinimumSize(new java.awt.Dimension(140, 24));
        jTextFieldAddress.setPreferredSize(new java.awt.Dimension(300, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(27, 5, 0, 30);
        jPanelServer.add(jTextFieldAddress, gridBagConstraints);

        jTextFieldPort.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldPort.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldPort.setText("3306");
        jTextFieldPort.setMinimumSize(new java.awt.Dimension(40, 24));
        jTextFieldPort.setPreferredSize(new java.awt.Dimension(40, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 39, 0, 30);
        jPanelServer.add(jTextFieldPort, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 30, 0, 0);
        jPanelServer.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel4.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 30, 30, 0);
        jPanelServer.add(jLabel4, gridBagConstraints);

        jTextFieldUser.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldUser.setMinimumSize(new java.awt.Dimension(140, 24));
        jTextFieldUser.setPreferredSize(new java.awt.Dimension(140, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 5, 0, 0);
        jPanelServer.add(jTextFieldUser, gridBagConstraints);

        jPasswordFieldPWD.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jPasswordFieldPWD.setMinimumSize(new java.awt.Dimension(140, 24));
        jPasswordFieldPWD.setPreferredSize(new java.awt.Dimension(140, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 5, 0, 0);
        jPanelServer.add(jPasswordFieldPWD, gridBagConstraints);

        jLabelPort.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelPort.setText("Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 0, 0);
        jPanelServer.add(jLabelPort, gridBagConstraints);

        jButtonShowPWD.setMaximumSize(new java.awt.Dimension(20, 20));
        jButtonShowPWD.setMinimumSize(new java.awt.Dimension(20, 20));
        jButtonShowPWD.setPreferredSize(new java.awt.Dimension(20, 20));
        jButtonShowPWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonShowPWDMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonShowPWDMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(19, 5, 0, 0);
        jPanelServer.add(jButtonShowPWD, gridBagConstraints);

        jLabelShowPassword.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabelShowPassword.setText("Show Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(23, 5, 0, 0);
        jPanelServer.add(jLabelShowPassword, gridBagConstraints);

        jTabbedPaneLoginSelect.addTab("Server", jPanelServer);

        getContentPane().add(jTabbedPaneLoginSelect, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonShowPWDMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShowPWDMousePressed
        jPasswordFieldPWD.setEchoChar((char) 0);
    }//GEN-LAST:event_jButtonShowPWDMousePressed

    private void jButtonShowPWDMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShowPWDMouseReleased
        jPasswordFieldPWD.setEchoChar('\u25cf');
    }//GEN-LAST:event_jButtonShowPWDMouseReleased

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed

        String address = jTextFieldAddress.getText();
        String port = jTextFieldPort.getText();
        String DBuser = jTextFieldUser.getText();
        char[] pwd = jPasswordFieldPWD.getPassword();

        DBManager DB = new DBManager(address, port, DBuser, pwd);
        // test code
        //MainView main = new MainView(DB);
        //main.setLocationRelativeTo(this);
        //main.setVisible(true);
        //close();
        //
        if (DB.connect()) {
            String sql = "SELECT STRCMP(SHA2(CONCAT(?,Salt),512),Pwd) FROM Staff"
                    + " WHERE Username = ?;";

            ArrayList<Object> data = new ArrayList<>();
            data.add(new String(jPasswordFieldUserPWD.getPassword()));
            data.add(jTextFieldUsername.getText());

            ResultSet result = DB.makeQuery(sql, data);
            try {
                if (Utils.getResultSize(result) > 0) {
                    result.next();
                    if (result.getInt(1) == 0) {
                        MainView main = new MainView(DB, jTextFieldUsername.getText());
                        main.setLocationRelativeTo(this);
                        main.setVisible(true);
                        DB.close();
                        closeWindow();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "The username or password was incorrect.", "Incorrect login", JOptionPane.ERROR_MESSAGE);
                DB.close();
                return; 
            } catch (Exception e) {
                e.printStackTrace();
                DB.close();
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Could not connect to the database", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jButtonShowUserPWDMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShowUserPWDMousePressed
        jPasswordFieldUserPWD.setEchoChar((char) 0);
    }//GEN-LAST:event_jButtonShowUserPWDMousePressed

    private void jButtonShowUserPWDMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShowUserPWDMouseReleased
        jPasswordFieldUserPWD.setEchoChar('\u25cf');
    }//GEN-LAST:event_jButtonShowUserPWDMouseReleased

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonShowPWD;
    private javax.swing.JButton jButtonShowUserPWD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelPort;
    private javax.swing.JLabel jLabelShowPassword;
    private javax.swing.JLabel jLabelShowPassword1;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JLabel jLabelUserPWD;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelServer;
    private javax.swing.JPasswordField jPasswordFieldPWD;
    private javax.swing.JPasswordField jPasswordFieldUserPWD;
    private javax.swing.JTabbedPane jTabbedPaneLoginSelect;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldUser;
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables
}
