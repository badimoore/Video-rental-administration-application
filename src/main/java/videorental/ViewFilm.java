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
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

/**
 *
 * @author badi_
 */
public class ViewFilm extends javax.swing.JFrame {

    /**
     * Creates new form ViewFilm
     */
    public ViewFilm(int filmID, DBManager DB) {
        this.filmID = filmID;
        this.DB = DB;

        initComponents();

        DB.connect();
        String sql = "SELECT Film.Title, Film.Year, Film.Runtime, Film.Description, Film.Coverart,"
                + " Type.Name, Studio.Name, Rating.Designation,"
                + " AVG(Review.Stars), COUNT(Review.ReviewID) FROM Film"
                + " LEFT JOIN Type USING(TypeID)"
                + " LEFT JOIN Studio USING(StudioID)"
                + " LEFT JOIN Rating USING(RatingID)"
                + " LEFT JOIN Review USING(FilmID)"
                + " WHERE Film.FilmID = " + filmID
                + " GROUP BY Film.FilmID;";
        ResultSet result = DB.makeQuery(sql);
        if (Utils.getResultSize(result) < 1) {
            return;
        }
        try {
            result.next();

            title = result.getString(1);
            int year = result.getInt(2);
            String description = result.getString(4);
            String studio = result.getString(7);
            String type = result.getString(6);
            String rating = result.getString(8);
            int runtime = result.getInt(3);
            float stars = result.getFloat(9);
            int reviews = result.getInt(10);

            jLabelTitle.setText(title + " (" + year + ")");
            setTitle(title);
            
            byte[] pic = result.getBytes(5);
            if (pic != null) {
                ImageIcon image = new ImageIcon(pic);
                jLabelTitle.setIcon(image);
            }

            StringBuilder html = new StringBuilder("<html><body><br><table>");
            
            //Add average user rating
            if (reviews > 0) {
                html.append("<tr><td valign=\"top\">User Rating<br>(" + reviews + " reviews):</td><td>");
                html.append("<p style=\"color: #FAA41A\">" + Utils.starText(stars, true) + "</p>");
                html.append("</td></tr>");
            }

            //Add film description                      
            if (description != null) {
                description = Utils.addBreaks(description);
                html.append("<tr><td valign=\"top\">Description:</td><td><i>");
                html.append("<p>" + description + "</p>");
                html.append("</i></td></tr></table><hr><table>");
            }

            //Add film studio
            if (studio != null) {
                html.append("<tr><td align=\"right\">Studio:</td><td>");
                html.append(studio);
                html.append("</td></tr>");
            }

            //Add film type
            if (type != null) {
                html.append("<tr><td align=\"right\">Type:</td><td>");
                html.append(type);
                html.append("</td></tr>");
            }
            
            //Add film genres
            sql = "SELECT Genre.Name FROM Genre"
                    + " INNER JOIN Filmgenre USING(GenreID)"
                    + " INNER JOIN Film USING(FilmID)"
                    + " WHERE FilmID = " + filmID + ";";
            ResultSet genreResults = DB.makeQuery(sql);
            if(Utils.getResultSize(genreResults) > 0) {
                html.append("<tr><td align=\"right\">Genre:</td><td>");
                while(genreResults.next()) {
                    html.append(genreResults.getString(1));
                    if (genreResults.next()) {
                        html.append(", ");
                    }
                    genreResults.previous();
                }
                html.append("</td></tr>");
            }

            //Add film rating
            if (rating != null) {
                html.append("<tr><td align=\"right\">Rating:</td><td>");
                html.append(rating);
                //Check if there are keywords associated with rating
                sql = "SELECT Ratingkeyword.Keyword FROM Ratingkeyword"
                        + " INNER JOIN Filmratingkeyword USING(RatingkeywordID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE Film.FilmID = ";
                sql += filmID + ";";
                ResultSet ratingResults = DB.makeQuery(sql);
                if (Utils.getResultSize(ratingResults) > 0) {
                    html.append(" (");
                    while (ratingResults.next()) {
                        html.append(ratingResults.getString(1));
                        if (ratingResults.next()) {
                            html.append(", ");
                            ratingResults.previous();
                        }
                    }
                    html.append(")");
                }
                html.append("</td></tr>");
            }

            //Add film publishing year
            html.append("<tr><td align=\"right\">Year:</td><td>");
            html.append(year);
            html.append("</td></tr>");

            //Add film runtime
            html.append("<tr><td align=\"right\">Runtime:</td><td>");
            html.append(runtime);
            html.append(" minutes</td></tr>");

            //read countries
            sql = "SELECT Country.Name FROM Country"
                    + " INNER JOIN Filmcountry USING(CountryID)"
                    + " INNER JOIN Film USING(FilmID)"
                    + " WHERE Film.FilmID = " + filmID + ";";
            ResultSet countryResults = DB.makeQuery(sql);
            //Add countries
            if (Utils.getResultSize(countryResults) > 0) {
                countryResults.next();
                html.append("<tr><td align=\"right\">Countries:</td><td>");
                html.append(countryResults.getString(1)); //do this separately to avoid extra comma
                while (countryResults.next()) {
                    html.append(", " + countryResults.getString(1));
                }
                html.append("</td></tr>");
            }
            
            html.append("</table><hr><table>");
            
            //read audio languages
            sql = "SELECT Language.Name FROM Language"
                    + " INNER JOIN Audiolanguage USING(LanguageID)"
                    + " WHERE Audiolanguage.FilmID = " + filmID + ";";
            ResultSet audioResults = DB.makeQuery(sql);
            //read subtitle languages
            sql = "SELECT Language.Name FROM Language"
                    + " INNER JOIN Subtitlelanguage USING(LanguageID)"
                    + " WHERE Subtitlelanguage.FilmID = " + filmID + ";";
            ResultSet subResults = DB.makeQuery(sql);

            //Add languages if either result set has results
            if (Utils.getResultSize(audioResults) > 0 || Utils.getResultSize(subResults) > 0) {
                html.append("<tr><td align=\"right\" valign=\"top\">Languages:</td><td><table style=\"border:1px solid black\"><tr><th>Audio</th><th>Subtitle</th></tr>");
                html.append("<tr><td align=\"center\" valign=\"top\"><i>");
                //Add audio languages
                while (audioResults.next()) {
                    html.append(audioResults.getString(1));
                    html.append("<br>");
                }
                html.append("</i></td><td align=\"center\" valign=\"top\"><i>");
                //Add subtitle languages
                while (subResults.next()) {
                    html.append(subResults.getString(1));
                    html.append("<br>");
                }
                html.append("</i></td></tr></table></td></tr>");
            }            

            //read crew
            sql = "SELECT CONCAT(Crew.Firstname, ' ', Crew.Lastname), Role.Name, Crewrole.Charactername, Crew.CrewID FROM Crew"
                    + " INNER JOIN Crewrole USING(CrewID)"
                    + " INNER JOIN Role USING(RoleID)"
                    + " INNER JOIN Film USING(FilmID)"
                    + " WHERE Film.FilmID = " + filmID
                    + " ORDER BY Role.Name DESC;";
            ResultSet crewResults = DB.makeQuery(sql);

            //add crew
            if (Utils.getResultSize(crewResults) > 0) {
                html.append("<tr><td align=\"right\" valign=\"top\">Cast & crew:</td><td><table style=\"border:1px solid black\">");
                while (crewResults.next()) {
                    html.append("<tr><td align=\"right\"><b><a href=\"http://example.com:" + crewResults.getInt(4) + "\">"+ crewResults.getString(1) + "</a></b></td><td>" + crewResults.getString(2));
                    String charName = crewResults.getString(3);
                    if (charName != null) {
                        html.append(" (" + charName + ")");
                    }
                    html.append("</td></tr>");
                }
                html.append("</table></td></tr>");
            }
            
            //read media
            sql = "SELECT Media.Name, Filmmedia.stock, Filmmedia.Price, COUNT(Rentalitem.RentalitemID), SUM(Filmrental.Returned) FROM Filmmedia"
                    + " INNER JOIN Media USING(MediaID)"
                    + " INNER JOIN Film USING(FilmID)"
                    + " LEFT JOIN Rentalitem USING(FilmmediaID)"
                    + " LEFT JOIN Filmrental USING(FilmrentalID)"
                    + " WHERE Film.FilmID = " + filmID
                    + " GROUP BY Media.MediaID;";
            ResultSet mediaResults = DB.makeQuery(sql);

            //add media
            if (Utils.getResultSize(mediaResults) > 0) {
                html.append("<tr><td align=\"right\" valign=\"top\">Media:</td><td>"
                        + "<table style=\"border:1px solid black\">"
                        + "<tr><th>Media</th><th>Stock (available)</th><th>Price</th></tr>");
                while (mediaResults.next()) {
                    String mediaName = mediaResults.getString(1);
                    html.append("<tr><td style=\"text-align:center\">" + mediaName
                            + ":</td><td style=\"text-align:center\">");
                    Integer stock = mediaResults.getInt(2);
                    if (stock != null && !mediaName.equals("Streaming")) {
                        html.append(stock + "(");
                        Integer rentedItems = mediaResults.getInt(4);
                        Integer returnedItems = mediaResults.getInt(5);
                        if (rentedItems != null) {
                            html.append(stock - rentedItems + returnedItems);
                        } else {
                            html.append(stock);
                        }
                        html.append(")");
                    }
                    String price = String.format("%.2f", mediaResults.getFloat(3));
                    html.append("</td><td style=\"text-align:center\">" + price + "</td></tr>");
                }
                html.append("</table></td></tr>");
            }

            html.append("</table></body></html>");

            jEditorPaneFilmInfo.setText(html.toString());
            DB.close();
            
            //scroll to top of page
            jEditorPaneFilmInfo.setCaretPosition(0);

        } catch (Exception e) {
            e.printStackTrace();
            DB.close();
        }
    }
    /*
    private String starText(float rating) {
        rating = (float) (Math.round(rating * 2) / 2.0);
        String stars = "";
        float counter = 0;
        
        while (counter <= rating) {
            if (counter > 0 && counter % 1 == 0) {
                stars += "&#9733;";
            } else if (counter == rating) {
                stars += "<small>&#9733;</small>";
            }
            counter += 0.5;
        }
        return stars;
    }
    */

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

        jButtonClose = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jEditorPaneFilmInfo = new javax.swing.JEditorPane();
        jLabelTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 225, 180));
        setMinimumSize(new java.awt.Dimension(700, 900));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(jButtonClose, gridBagConstraints);

        jButtonEdit.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonEdit.setText("Edit");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        getContentPane().add(jButtonEdit, gridBagConstraints);

        jButtonDelete.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        getContentPane().add(jButtonDelete, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jEditorPaneFilmInfo.setEditable(false);
        jEditorPaneFilmInfo.setContentType("text/html"); // NOI18N
        jEditorPaneFilmInfo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jEditorPaneFilmInfo.setText("");
        jEditorPaneFilmInfo.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                jEditorPaneFilmInfoHyperlinkUpdate(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel1.add(jEditorPaneFilmInfo, gridBagConstraints);

        jLabelTitle.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Title of Film (And Year)");
        jLabelTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jLabelTitle.setOpaque(true);
        jLabelTitle.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jLabelTitle, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this film?", "Deleting " + title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sql = "DELETE FROM Film WHERE FilmID=" + filmID + ";";
            DB.connect();
            DB.makeQuery(sql);
            DB.close();
            closeWindow();
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        AddFilm filmWizard = new AddFilm(filmID, DB);
        filmWizard.setVisible(true);
        filmWizard.setLocationRelativeTo(this);
        closeWindow();
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jEditorPaneFilmInfoHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_jEditorPaneFilmInfoHyperlinkUpdate
        if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            int crewID = evt.getURL().getPort();
            CrewInfo crew = new CrewInfo(DB, crewID);
            crew.setLocationRelativeTo(this);
            crew.setVisible(true);
        }
    }//GEN-LAST:event_jEditorPaneFilmInfoHyperlinkUpdate

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
            java.util.logging.Logger.getLogger(ViewFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewFilm(0, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JEditorPane jEditorPaneFilmInfo;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    private int filmID;
    private DBManager DB;
    private String title;
}
