/*
 * Copyright (C) 2021 Badi Moore
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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * A wizard for adding new films, or updating info on an existing film
 *
 * @author badi_
 */
public class AddFilm extends javax.swing.JFrame {

    /**
     * Creates new form AddFilm
     *
     * @param filmID the ID number of the film to edit, a value of 0 means
     * adding a new film
     * @param DB the DBManager object for handling database operations
     */
    public AddFilm(int filmID, DBManager DB) {
        initComponents();

        this.DB = DB;
        oldFilmID = filmID;
        currentCard = "card1";
        refreshComboBoxes();

        updating = false;
        imageStream = null;
        imageBlob = null;

        //if an existing film is to be edited
        if (filmID > 0) {
            //keep track of areas where changes were made
            crewChanged = false;
            countriesChanged = false;
            languageChanged = false;
            ratingKeywordsChanged = false;
            genreChanged = false;
            mediaChanged = false;
            updating = true;
            DB.connect();
            setFilmInfo();
            DB.close();
        }

    }

    /**
     * Read in the info of a film to be edited
     */
    private void setFilmInfo() {
        setTitle("Update Film");
        
        //Fetch the info found on the Film table
        String sql = "SELECT TypeID, StudioID, RatingID, Title, Year, Runtime, Description, Coverart FROM Film"
                + " WHERE FilmID = ?;";
        ArrayList<Object> data = new ArrayList<>();
        data.add(oldFilmID);
        ResultSet result = DB.makeQuery(sql, data);

        try {
            //if query returned result
            if (result.next()) {
                //read data from query result
                int type = result.getInt(1);
                int studio = result.getInt(2);
                int rating = result.getInt(3);
                String title = result.getString(4);
                int year = result.getInt(5);
                int runtime = result.getInt(6);
                String description = result.getString(7);

                // get image from database
                Blob imageBlobNew = result.getBlob(8);
                //if loaded image is not null, store it for reinsertion into database
                if (imageBlobNew != null) {
                    imageBlob = imageBlobNew;
                    jLabelImage.setText("An image already exists in database");
                }

                //set selections for combo boxes and text fields
                Utils.setComboBoxIndex(jComboBoxType, type);
                Utils.setComboBoxIndex(jComboBoxStudio, studio);
                Utils.setComboBoxIndex(jComboBoxRatings, rating);
                jTextFieldTitle.setText(title);
                jTextFieldYear.setText(String.valueOf(year));
                jTextFieldRuntime.setText(String.valueOf(runtime));
                if (description != null) {
                    jTextAreaDescription.setText(description);
                }

                //load existing cast & crew selections
                String crewSql = "SELECT Crew.CrewID, CONCAT(Crew.Firstname,' ',Crew.Lastname), CONCAT(Role.RoleID, ' - ',Role.Name), Crewrole.Charactername FROM Crewrole"
                        + " INNER JOIN Crew USING(CrewID)"
                        + " INNER JOIN Role USING (RoleID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                result = DB.makeQuery(crewSql, data);
                Utils.populateTable(jTableCrewSelected, result);
                
                //load country selections
                String countrySql = "SELECT Country.CountryID, Country.Name FROM Country"
                        + " INNER JOIN Filmcountry USING(CountryID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                result = DB.makeQuery(countrySql, data);
                Utils.populateTable(jTableCountries, result);
                
                //load language selections
                String languageSql = "SELECT Language.LanguageID, Language.Name, 'Audio' FROM Language"
                        + " INNER JOIN Audiolanguage USING(LanguageID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?"
                        + " UNION"
                        + " SELECT Language.LanguageID, Language.Name, 'Subtitle' FROM Language"
                        + " INNER JOIN Subtitlelanguage USING(LanguageID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                data.add(oldFilmID);
                result = DB.makeQuery(languageSql, data);
                Utils.populateTable(jTableLanguages, result);
                            
                //load rating keyword selections
                String ratingKeywordSql = "SELECT Ratingkeyword.RatingkeywordID, Ratingkeyword.Keyword FROM Ratingkeyword"
                        + " INNER JOIN Filmratingkeyword USING(RatingkeywordID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                data.clear();
                data.add(oldFilmID);
                result = DB.makeQuery(ratingKeywordSql, data);
                Utils.populateTable(jTableRatingKeywords, result);

                //load genre selections
                String genreSql = "SELECT Genre.GenreID, Genre.Name FROM Genre"
                        + " INNER JOIN Filmgenre USING(GenreID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                result = DB.makeQuery(genreSql, data);
                Utils.populateTable(jTableGenres, result);

                //load media selections
                String mediaSql = "SELECT Media.MediaID, Media.Name, Filmmedia.Stock, Filmmedia.Price FROM Media"
                        + " INNER JOIN Filmmedia USING(MediaID)"
                        + " INNER JOIN Film USING(FilmID)"
                        + " WHERE FilmID = ?;";
                result = DB.makeQuery(mediaSql, data);
                Utils.populateTable(jTableMedia, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read database info and populate each combo box on form
     */
    private void refreshComboBoxes() {
        DB.connect();
        ResultSet types = DB.makeQuery("Select TypeID, Name FROM Type;");
        ResultSet studios = DB.makeQuery("Select StudioID, Name FROM Studio;");
        ResultSet roles = DB.makeQuery("Select RoleID, Name FROM Role;");
        ResultSet countries = DB.makeQuery("Select CountryID, Name FROM Country;");
        ResultSet languages = DB.makeQuery("Select LanguageID, Name FROM Language;");
        ResultSet ratings = DB.makeQuery("Select RatingID, Designation FROM Rating;");
        ResultSet ratingKeywords = DB.makeQuery("Select RatingkeywordID, Keyword FROM Ratingkeyword;");
        ResultSet genres = DB.makeQuery("Select GenreID, Name FROM Genre;");
        ResultSet media = DB.makeQuery("Select MediaID, Name FROM Media;");
        DB.close();

        Utils.populateComboBox(jComboBoxType, types, "-");
        Utils.populateComboBox(jComboBoxStudio, studios, "-");
        Utils.populateComboBox(jComboBoxRoles, roles, "Select role");
        Utils.populateComboBox(jComboBoxCountry, countries, "Select country");
        Utils.populateComboBox(jComboBoxLanguage, languages, "Select language");
        Utils.populateComboBox(jComboBoxRatings, ratings, "Select rating");
        Utils.populateComboBox(jComboBoxRatingKeywords, ratingKeywords, "Select keyword");
        Utils.populateComboBox(jComboBoxGenres, genres, "Select genre");
        Utils.populateComboBox(jComboBoxMedia, media, "Select media");
    }
    
    /**
     * Close window
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

        buttonGroupLanguage = new javax.swing.ButtonGroup();
        jPanelBase = new javax.swing.JPanel();
        jPanelEnterInfo1 = new javax.swing.JPanel();
        jLabelFilmTitle = new javax.swing.JLabel();
        jLabelFilmYear = new javax.swing.JLabel();
        jTextFieldTitle = new javax.swing.JTextField();
        jTextFieldRuntime = new javax.swing.JTextField();
        jLabelRuntime = new javax.swing.JLabel();
        jTextFieldYear = new javax.swing.JTextField();
        jComboBoxStudio = new javax.swing.JComboBox<>();
        jLabelStudio = new javax.swing.JLabel();
        jScrollPaneDescription = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();
        jLabelDescription = new javax.swing.JLabel();
        jComboBoxType = new javax.swing.JComboBox<>();
        jLabelType = new javax.swing.JLabel();
        jLabelImage = new javax.swing.JLabel();
        jButtonAddImage = new javax.swing.JButton();
        jPanelEnterInfo2 = new javax.swing.JPanel();
        jButtonCrewSearch = new javax.swing.JButton();
        jTextFieldCrewSearch = new javax.swing.JTextField();
        jComboBoxRoles = new javax.swing.JComboBox<>();
        jButtonAddCrew = new javax.swing.JButton();
        jTextFieldCharName = new javax.swing.JTextField();
        jLabelCharName = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCrewSearch = new javax.swing.JTable();
        jButtonCrewRemove = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCrewSelected = new javax.swing.JTable();
        jPanelEnterInfo3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableCountries = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableLanguages = new javax.swing.JTable();
        jButtonAddCountry = new javax.swing.JButton();
        jButtonAddLanguage = new javax.swing.JButton();
        jComboBoxCountry = new javax.swing.JComboBox<>();
        jComboBoxLanguage = new javax.swing.JComboBox<>();
        jRadioButtonAudio = new javax.swing.JRadioButton();
        jRadioButtonSubtitle = new javax.swing.JRadioButton();
        jButtonRemoveCountry = new javax.swing.JButton();
        jButtonRemoveLanguage = new javax.swing.JButton();
        jPanelEnterInfo4 = new javax.swing.JPanel();
        jComboBoxRatingKeywords = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRatingKeywords = new javax.swing.JTable();
        jComboBoxRatings = new javax.swing.JComboBox<>();
        jButtonAddRatingKeyword = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableGenres = new javax.swing.JTable();
        jButtonAddGenre = new javax.swing.JButton();
        jComboBoxGenres = new javax.swing.JComboBox<>();
        jButtonRemoveKeyword = new javax.swing.JButton();
        jButtonRemoveGenre = new javax.swing.JButton();
        jLabelRating = new javax.swing.JLabel();
        jPanelEnterInfo5 = new javax.swing.JPanel();
        jButtonAddMedia = new javax.swing.JButton();
        jTextFieldMediaStock = new javax.swing.JTextField();
        jComboBoxMedia = new javax.swing.JComboBox<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableMedia = new javax.swing.JTable();
        jLabelStock = new javax.swing.JLabel();
        jButtonRemoveMedia = new javax.swing.JButton();
        jTextFieldPrice = new javax.swing.JTextField();
        jLabelPrice = new javax.swing.JLabel();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Film");
        setBackground(new java.awt.Color(255, 225, 180));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(1000, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelBase.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelBase.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanelBase.setPreferredSize(new java.awt.Dimension(200, 200));
        jPanelBase.setLayout(new java.awt.CardLayout());

        jPanelEnterInfo1.setBackground(new java.awt.Color(250, 250, 250));
        jPanelEnterInfo1.setName(""); // NOI18N
        jPanelEnterInfo1.setLayout(new java.awt.GridBagLayout());

        jLabelFilmTitle.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelFilmTitle.setText("Title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelFilmTitle, gridBagConstraints);

        jLabelFilmYear.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelFilmYear.setText("Year");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelFilmYear, gridBagConstraints);

        jTextFieldTitle.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldTitle.setMinimumSize(new java.awt.Dimension(200, 25));
        jTextFieldTitle.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanelEnterInfo1.add(jTextFieldTitle, gridBagConstraints);

        jTextFieldRuntime.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldRuntime.setMinimumSize(new java.awt.Dimension(60, 25));
        jTextFieldRuntime.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanelEnterInfo1.add(jTextFieldRuntime, gridBagConstraints);

        jLabelRuntime.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRuntime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRuntime.setText("Runtime (minutes)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelRuntime, gridBagConstraints);

        jTextFieldYear.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldYear.setMinimumSize(new java.awt.Dimension(60, 25));
        jTextFieldYear.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanelEnterInfo1.add(jTextFieldYear, gridBagConstraints);

        jComboBoxStudio.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxStudio.setMinimumSize(new java.awt.Dimension(200, 25));
        jComboBoxStudio.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanelEnterInfo1.add(jComboBoxStudio, gridBagConstraints);

        jLabelStudio.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelStudio.setText("Studio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelStudio, gridBagConstraints);

        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextAreaDescription.setLineWrap(true);
        jTextAreaDescription.setRows(5);
        jScrollPaneDescription.setViewportView(jTextAreaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 160);
        jPanelEnterInfo1.add(jScrollPaneDescription, gridBagConstraints);

        jLabelDescription.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelDescription.setText("Description");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelDescription, gridBagConstraints);

        jComboBoxType.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxType.setMinimumSize(new java.awt.Dimension(200, 25));
        jComboBoxType.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanelEnterInfo1.add(jComboBoxType, gridBagConstraints);

        jLabelType.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo1.add(jLabelType, gridBagConstraints);

        jLabelImage.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(34, 10, 10, 0);
        jPanelEnterInfo1.add(jLabelImage, gridBagConstraints);

        jButtonAddImage.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddImage.setText("Add Image");
        jButtonAddImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddImageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(30, 10, 10, 0);
        jPanelEnterInfo1.add(jButtonAddImage, gridBagConstraints);

        jPanelBase.add(jPanelEnterInfo1, "card1");

        jPanelEnterInfo2.setBackground(new java.awt.Color(250, 250, 250));
        jPanelEnterInfo2.setLayout(new java.awt.GridBagLayout());

        jButtonCrewSearch.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonCrewSearch.setText("Search People");
        jButtonCrewSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrewSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo2.add(jButtonCrewSearch, gridBagConstraints);

        jTextFieldCrewSearch.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldCrewSearch.setMinimumSize(new java.awt.Dimension(140, 25));
        jTextFieldCrewSearch.setPreferredSize(new java.awt.Dimension(120, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelEnterInfo2.add(jTextFieldCrewSearch, gridBagConstraints);

        jComboBoxRoles.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxRoles.setMinimumSize(new java.awt.Dimension(140, 25));
        jComboBoxRoles.setPreferredSize(new java.awt.Dimension(140, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelEnterInfo2.add(jComboBoxRoles, gridBagConstraints);

        jButtonAddCrew.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddCrew.setText("Add Person >");
        jButtonAddCrew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddCrewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(30, 20, 0, 20);
        jPanelEnterInfo2.add(jButtonAddCrew, gridBagConstraints);

        jTextFieldCharName.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldCharName.setMinimumSize(new java.awt.Dimension(75, 25));
        jTextFieldCharName.setPreferredSize(new java.awt.Dimension(75, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        jPanelEnterInfo2.add(jTextFieldCharName, gridBagConstraints);

        jLabelCharName.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabelCharName.setText("Character name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelEnterInfo2.add(jLabelCharName, gridBagConstraints);

        jTableCrewSearch.setAutoCreateRowSorter(true);
        jTableCrewSearch.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableCrewSearch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCrewSearch.setFillsViewportHeight(true);
        jTableCrewSearch.setRowHeight(20);
        jTableCrewSearch.setShowHorizontalLines(false);
        jTableCrewSearch.setShowVerticalLines(false);
        jTableCrewSearch.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTableCrewSearch);
        if (jTableCrewSearch.getColumnModel().getColumnCount() > 0) {
            jTableCrewSearch.getColumnModel().getColumn(0).setMinWidth(0);
            jTableCrewSearch.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableCrewSearch.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelEnterInfo2.add(jScrollPane3, gridBagConstraints);

        jButtonCrewRemove.setText("Remove Person");
        jButtonCrewRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrewRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanelEnterInfo2.add(jButtonCrewRemove, gridBagConstraints);

        jTableCrewSelected.setAutoCreateRowSorter(true);
        jTableCrewSelected.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableCrewSelected.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Role", "Character"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCrewSelected.setFillsViewportHeight(true);
        jTableCrewSelected.setRowHeight(20);
        jTableCrewSelected.setShowHorizontalLines(false);
        jTableCrewSelected.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTableCrewSelected);
        if (jTableCrewSelected.getColumnModel().getColumnCount() > 0) {
            jTableCrewSelected.getColumnModel().getColumn(0).setMinWidth(0);
            jTableCrewSelected.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableCrewSelected.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelEnterInfo2.add(jScrollPane2, gridBagConstraints);

        jPanelBase.add(jPanelEnterInfo2, "card2");

        jPanelEnterInfo3.setBackground(new java.awt.Color(250, 250, 250));
        jPanelEnterInfo3.setLayout(new java.awt.GridBagLayout());

        jTableCountries.setAutoCreateRowSorter(true);
        jTableCountries.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableCountries.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Countries"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCountries.setFillsViewportHeight(true);
        jTableCountries.setRowHeight(20);
        jTableCountries.setShowHorizontalLines(false);
        jTableCountries.setShowVerticalLines(false);
        jTableCountries.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTableCountries);
        if (jTableCountries.getColumnModel().getColumnCount() > 0) {
            jTableCountries.getColumnModel().getColumn(0).setMinWidth(0);
            jTableCountries.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableCountries.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 160);
        jPanelEnterInfo3.add(jScrollPane4, gridBagConstraints);

        jTableLanguages.setAutoCreateRowSorter(true);
        jTableLanguages.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableLanguages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Languages", "Category"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLanguages.setFillsViewportHeight(true);
        jTableLanguages.setRowHeight(20);
        jTableLanguages.setShowHorizontalLines(false);
        jTableLanguages.setShowVerticalLines(false);
        jTableLanguages.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(jTableLanguages);
        if (jTableLanguages.getColumnModel().getColumnCount() > 0) {
            jTableLanguages.getColumnModel().getColumn(0).setMinWidth(0);
            jTableLanguages.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableLanguages.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 160);
        jPanelEnterInfo3.add(jScrollPane5, gridBagConstraints);

        jButtonAddCountry.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddCountry.setText("Add Country  >");
        jButtonAddCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddCountryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo3.add(jButtonAddCountry, gridBagConstraints);

        jButtonAddLanguage.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddLanguage.setText("Add Language  >");
        jButtonAddLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddLanguageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo3.add(jButtonAddLanguage, gridBagConstraints);

        jComboBoxCountry.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxCountry.setMinimumSize(new java.awt.Dimension(200, 25));
        jComboBoxCountry.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo3.add(jComboBoxCountry, gridBagConstraints);

        jComboBoxLanguage.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxLanguage.setMinimumSize(new java.awt.Dimension(200, 25));
        jComboBoxLanguage.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo3.add(jComboBoxLanguage, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonAudio);
        jRadioButtonAudio.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jRadioButtonAudio.setSelected(true);
        jRadioButtonAudio.setText("Audio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 130, 0, 0);
        jPanelEnterInfo3.add(jRadioButtonAudio, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonSubtitle);
        jRadioButtonSubtitle.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jRadioButtonSubtitle.setText("Subtitle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 130, 0, 0);
        jPanelEnterInfo3.add(jRadioButtonSubtitle, gridBagConstraints);

        jButtonRemoveCountry.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemoveCountry.setText("Remove Country");
        jButtonRemoveCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveCountryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        jPanelEnterInfo3.add(jButtonRemoveCountry, gridBagConstraints);

        jButtonRemoveLanguage.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemoveLanguage.setText("Remove Language");
        jButtonRemoveLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveLanguageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        jPanelEnterInfo3.add(jButtonRemoveLanguage, gridBagConstraints);

        jPanelBase.add(jPanelEnterInfo3, "card3");

        jPanelEnterInfo4.setBackground(new java.awt.Color(250, 250, 250));
        jPanelEnterInfo4.setLayout(new java.awt.GridBagLayout());

        jComboBoxRatingKeywords.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxRatingKeywords.setMinimumSize(new java.awt.Dimension(200, 26));
        jComboBoxRatingKeywords.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo4.add(jComboBoxRatingKeywords, gridBagConstraints);

        jTableRatingKeywords.setAutoCreateRowSorter(true);
        jTableRatingKeywords.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableRatingKeywords.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Keyword"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRatingKeywords.setFillsViewportHeight(true);
        jTableRatingKeywords.setRowHeight(20);
        jTableRatingKeywords.setShowHorizontalLines(false);
        jTableRatingKeywords.setShowVerticalLines(false);
        jScrollPane1.setViewportView(jTableRatingKeywords);
        if (jTableRatingKeywords.getColumnModel().getColumnCount() > 0) {
            jTableRatingKeywords.getColumnModel().getColumn(0).setMinWidth(0);
            jTableRatingKeywords.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableRatingKeywords.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 160);
        jPanelEnterInfo4.add(jScrollPane1, gridBagConstraints);

        jComboBoxRatings.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxRatings.setMinimumSize(new java.awt.Dimension(200, 26));
        jComboBoxRatings.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanelEnterInfo4.add(jComboBoxRatings, gridBagConstraints);

        jButtonAddRatingKeyword.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddRatingKeyword.setText("Add Keyword  >");
        jButtonAddRatingKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddRatingKeywordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        jPanelEnterInfo4.add(jButtonAddRatingKeyword, gridBagConstraints);

        jTableGenres.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableGenres.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Genre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGenres.setFillsViewportHeight(true);
        jTableGenres.setRowHeight(20);
        jTableGenres.setShowHorizontalLines(false);
        jTableGenres.setShowVerticalLines(false);
        jTableGenres.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(jTableGenres);
        if (jTableGenres.getColumnModel().getColumnCount() > 0) {
            jTableGenres.getColumnModel().getColumn(0).setMinWidth(0);
            jTableGenres.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableGenres.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 160);
        jPanelEnterInfo4.add(jScrollPane6, gridBagConstraints);

        jButtonAddGenre.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddGenre.setText("Add Genre  >");
        jButtonAddGenre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGenreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        jPanelEnterInfo4.add(jButtonAddGenre, gridBagConstraints);

        jComboBoxGenres.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxGenres.setMinimumSize(new java.awt.Dimension(200, 26));
        jComboBoxGenres.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo4.add(jComboBoxGenres, gridBagConstraints);

        jButtonRemoveKeyword.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemoveKeyword.setText("Remove");
        jButtonRemoveKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveKeywordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanelEnterInfo4.add(jButtonRemoveKeyword, gridBagConstraints);

        jButtonRemoveGenre.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemoveGenre.setText("Remove");
        jButtonRemoveGenre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveGenreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 0);
        jPanelEnterInfo4.add(jButtonRemoveGenre, gridBagConstraints);

        jLabelRating.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelRating.setText("Age Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo4.add(jLabelRating, gridBagConstraints);

        jPanelBase.add(jPanelEnterInfo4, "card4");

        jPanelEnterInfo5.setBackground(new java.awt.Color(250, 250, 250));
        jPanelEnterInfo5.setLayout(new java.awt.GridBagLayout());

        jButtonAddMedia.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonAddMedia.setText("Add Media >");
        jButtonAddMedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddMediaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        jPanelEnterInfo5.add(jButtonAddMedia, gridBagConstraints);

        jTextFieldMediaStock.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldMediaStock.setMinimumSize(new java.awt.Dimension(80, 25));
        jTextFieldMediaStock.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        jPanelEnterInfo5.add(jTextFieldMediaStock, gridBagConstraints);

        jComboBoxMedia.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jComboBoxMedia.setMinimumSize(new java.awt.Dimension(140, 24));
        jComboBoxMedia.setPreferredSize(new java.awt.Dimension(140, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jPanelEnterInfo5.add(jComboBoxMedia, gridBagConstraints);

        jTableMedia.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTableMedia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Media", "Stock", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableMedia.setFillsViewportHeight(true);
        jTableMedia.setRowHeight(20);
        jTableMedia.setShowHorizontalLines(false);
        jTableMedia.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(jTableMedia);
        if (jTableMedia.getColumnModel().getColumnCount() > 0) {
            jTableMedia.getColumnModel().getColumn(0).setMinWidth(0);
            jTableMedia.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableMedia.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 160);
        jPanelEnterInfo5.add(jScrollPane7, gridBagConstraints);

        jLabelStock.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelStock.setText("Stock");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 5);
        jPanelEnterInfo5.add(jLabelStock, gridBagConstraints);

        jButtonRemoveMedia.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRemoveMedia.setText("Remove");
        jButtonRemoveMedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveMediaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        jPanelEnterInfo5.add(jButtonRemoveMedia, gridBagConstraints);

        jTextFieldPrice.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextFieldPrice.setMinimumSize(new java.awt.Dimension(80, 25));
        jTextFieldPrice.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        jPanelEnterInfo5.add(jTextFieldPrice, gridBagConstraints);

        jLabelPrice.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabelPrice.setText("Price");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 5);
        jPanelEnterInfo5.add(jLabelPrice, gridBagConstraints);

        jPanelBase.add(jPanelEnterInfo5, "card5");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 0, 20);
        getContentPane().add(jPanelBase, gridBagConstraints);

        jButtonPrevious.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonPrevious.setText("< Previous");
        jButtonPrevious.setEnabled(false);
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 5);
        getContentPane().add(jButtonPrevious, gridBagConstraints);

        jButtonNext.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonNext.setText("Next >");
        jButtonNext.setMaximumSize(new java.awt.Dimension(99, 25));
        jButtonNext.setMinimumSize(new java.awt.Dimension(99, 25));
        jButtonNext.setPreferredSize(new java.awt.Dimension(99, 25));
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 20);
        getContentPane().add(jButtonNext, gridBagConstraints);

        jButtonCancel.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 0);
        getContentPane().add(jButtonCancel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * Functionality for next-button (form navigation)
     * @param evt Event
     */
    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        CardLayout layout = (CardLayout) jPanelBase.getLayout();

        //check current card and use switch to check inputs and navigate to correct card
        //the first page contains several mandatory fields that are also checked
        switch (currentCard) {
            case "card1":
                jLabelFilmYear.setForeground(Color.black);
                jLabelFilmTitle.setForeground(Color.black);
                jLabelRuntime.setForeground(Color.black);

                //if title field is empty
                if (jTextFieldTitle.getText().isBlank()) {
                    jLabelFilmTitle.setForeground(Color.red);
                    break;
                }
                //if year field empty, or not integer
                if (jTextFieldYear.getText().isBlank() || !jTextFieldYear.getText().matches("\\d+")) {
                    jLabelFilmYear.setForeground(Color.red);
                    break;
                }
                //if runtime field is empty or not integer
                if (jTextFieldRuntime.getText().isBlank() || !jTextFieldRuntime.getText().matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "The runtime field is required.", "Missing Info", JOptionPane.ERROR_MESSAGE);
                    jLabelRuntime.setForeground(Color.red);
                    break;
                }
                layout.show(jPanelBase, "card2");
                jButtonPrevious.setEnabled(true); //previous-button starts disabled
                currentCard = "card2";
                break;
            case "card2":
                layout.show(jPanelBase, "card3");
                currentCard = "card3";
                break;
            case "card3":
                layout.show(jPanelBase, "card4");
                currentCard = "card4";
                break;
            case "card4":
                layout.show(jPanelBase, "card5");
                currentCard = "card5";
                jButtonNext.setText("Submit"); //next step is submitting form
                break;
            case "card5":
                if (submit()) {
                    closeWindow();
                }
                break;
        }
    }//GEN-LAST:event_jButtonNextActionPerformed

    /**
     * Functionality for previous-button (form navigation)
     * @param evt Event
     */
    private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
        CardLayout layout = (CardLayout) jPanelBase.getLayout();

        //check current card and use switch to navigate to correct card
        switch (currentCard) {
            case "card2":
                layout.show(jPanelBase, "card1");
                jButtonPrevious.setEnabled(false); //disable previous-button on first page
                currentCard = "card1";
                break;
            case "card3":
                layout.show(jPanelBase, "card2");
                currentCard = "card2";
                break;
            case "card4":
                layout.show(jPanelBase, "card3");
                currentCard = "card3";
                break;
            case "card5":
                layout.show(jPanelBase, "card4");
                currentCard = "card4";
                jButtonNext.setText("Next >"); //restore original text (button text on last card is otherwise "Submit")
                break;
        }
    }//GEN-LAST:event_jButtonPreviousActionPerformed

    /**
     * Submit form and write to database
     * 
     * @return was insertion/update successful?
     */
    private boolean submit() {
        //read data that will be written to the Film-table
        //input format for text fields is checked earlier during form navigation
        Integer typeID = 0;
        Integer studioID = 0;
        Integer ratingID = 0;
        String title = jTextFieldTitle.getText();
        Integer year = Integer.valueOf(jTextFieldYear.getText());
        Integer runtime = Integer.valueOf(jTextFieldRuntime.getText());

        //if type, studio, ratings fields not set to default
        //read text in selection, the selection text starts with the ID number for the selected item
        //use the Utils method to grab the ID number from the String and assign it to variable
        if (!jComboBoxType.getSelectedItem().equals("-")) {
            String selected = (String) jComboBoxType.getSelectedItem();
            typeID = Integer.valueOf(Utils.getSelectedID(selected));
        }
        if (!jComboBoxStudio.getSelectedItem().equals("-")) {
            String selected = (String) jComboBoxStudio.getSelectedItem();
            studioID = Integer.valueOf(Utils.getSelectedID(selected));
        }
        if (!jComboBoxRatings.getSelectedItem().equals("Select rating")) {
            String selected = (String) jComboBoxRatings.getSelectedItem();
            ratingID = Integer.valueOf(Utils.getSelectedID(selected));
        }

        String description = jTextAreaDescription.getText();

        String sql;
        //select correct sql depending on whether new film, or updating old
        if (updating) {
            sql = "UPDATE Film SET TypeID = ?, StudioID = ?, RatingID = ?, Title = ?, Year = ?, Runtime = ?, Description = ?, Coverart = ?"
                    + " WHERE FilmID = " + oldFilmID + ";";
        } else {
            sql = "INSERT INTO Film (FilmID, TypeID, StudioID, RatingID, Title, Year, Runtime, Description, Coverart) VALUES (null,?,?,?,?,?,?,?,?);";
        }

        //load data into arraylist for use with preparedstatement
        //if no data exists, create NullParameter object
        ArrayList<Object> data = new ArrayList<>();
        if (typeID == 0) {
            data.add(new NullParameter(Types.INTEGER));
        } else {
            data.add(typeID);
        }
        if (studioID == 0) {
            data.add(new NullParameter(Types.INTEGER));
        } else {
            data.add(studioID);
        }
        if (ratingID == 0) {
            data.add(new NullParameter(Types.INTEGER));
        } else {
            data.add(ratingID);
        }
        data.add(title);
        data.add(year);
        data.add(runtime);

        //add description
        if (description.isEmpty()) {
            data.add(new NullParameter(Types.VARCHAR));
        } else {
            data.add(description);
        }

        //add image
        //if imageStream is non-null, an image hasd been loaded from disk, use this
        //otherwise, if imageBlob is not null, an image has been loaded from database, feed that back in
        //otherwise, write null value
        if (imageStream != null) {
            data.add(imageStream);
        } else if (imageBlob != null) {
            data.add(imageBlob);
        } else {
            data.add(new NullParameter(Types.BLOB));
        }

        //write data to Film table
        DB.connect();
        DB.makeQuery(sql, data);

        //if new film added, fetch the generated autoincrement ID, otherwise use ID for existing film
        //if no valid ID was generated, abort
        int filmID;
        if (updating) {
            filmID = oldFilmID;
        } else {
            filmID = DB.getLastGeneratedKey();
        }
        if (filmID < 1) {
            return false;
        }
        //add and remove entries in other database tables
        addCrew(filmID);
        addCountries(filmID);
        addLanguages(filmID);
        addRatingKeyWords(filmID);
        addGenres(filmID);
        addMedia(filmID);

        DB.close();

        return true;
    }

    /**
     *
     * @param filmID
     */
    private void addCrew(int filmID) {
        //Return if no changes have been made
        if (!crewChanged) {
            return;
        }
        //Remove all crew entries if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Crewrole WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "INSERT INTO Crewrole (CrewroleID, CrewID, RoleID, FilmID, Charactername) VALUES (null, ?, ?, ?, ?);";

        DefaultTableModel model = (DefaultTableModel) jTableCrewSelected.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(model.getValueAt(i, 0)); //add CrewID            
            data.add(Integer.valueOf(Utils.getSelectedID((String) model.getValueAt(i, 2)))); //add RoleID            
            data.add(filmID); //add FilmID
            //add Character
            Object character = model.getValueAt(i, 3);
            if (character == null) {
                data.add(new NullParameter(Types.VARCHAR));
            } else {
                data.add((String) character);
            }
            DB.makeQuery(sql, data);
        }
    }

    private void addCountries(int filmID) {
        //Return if no changes have been made
        if (!countriesChanged) {
            return;
        }
        //Remove all country entries for film if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Filmcountry WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "INSERT INTO Filmcountry (FilmcountryID, FilmID, CountryID) VALUES (null, ?, ?);";
        DefaultTableModel model = (DefaultTableModel) jTableCountries.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(filmID); //add filmID           
            data.add(model.getValueAt(i, 0)); //add CountryID

            DB.makeQuery(sql, data);
        }
    }

    private void addLanguages(int filmID) {
        //Return if no changes have been made
        if (!languageChanged) {
            return;
        }
        //Remove all language entries if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Audiolanguage WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
            sql = "DELETE FROM Subtitlelanguage WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "";
        DefaultTableModel model = (DefaultTableModel) jTableLanguages.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();

            if (model.getValueAt(i, 2).equals("Audio")) {
                sql = "INSERT INTO Audiolanguage (AudiolanguageID, LanguageID, FilmID) VALUES (null, ?, ?);";
            }
            if (model.getValueAt(i, 2).equals("Subtitle")) {
                sql = "INSERT INTO Subtitlelanguage (SubtitlelanguageID, LanguageID, FilmID) VALUES (null, ?, ?);";
            }
            data.add(model.getValueAt(i, 0)); //add LanguageID           
            data.add(filmID); //add FilmID

            DB.makeQuery(sql, data);
        }
    }

    private void addRatingKeyWords(int filmID) {
        //Return if no changes have been made
        if (!ratingKeywordsChanged) {
            return;
        }
        //Remove all rating keyword entries if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Filmratingkeyword WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "INSERT INTO Filmratingkeyword (FilmratingkeywordID, FilmID, RatingkeywordID) VALUES (null, ?, ?);";
        DefaultTableModel model = (DefaultTableModel) jTableRatingKeywords.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(filmID); //add filmID            
            data.add(model.getValueAt(i, 0)); //add RatingkeywordID

            DB.makeQuery(sql, data);
        }
    }

    private void addGenres(int filmID) {
        //Return if no changes have been made
        if (!genreChanged) {
            return;
        }
        //Remove all genre entries if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Filmgenre WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "INSERT INTO Filmgenre (FilmgenreID, GenreID, FilmID) VALUES (null, ?, ?);";
        DefaultTableModel model = (DefaultTableModel) jTableGenres.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(model.getValueAt(i, 0)); //add GenreID            
            data.add(filmID); //add FilmID

            DB.makeQuery(sql, data);
        }
    }

    private void addMedia(int filmID) {
        //Return if no changes have been made
        if (!mediaChanged) {
            return;
        }
        //Remove all media entries if changes were made and updating existing entry
        if (updating) {
            String sql = "DELETE FROM Filmmedia WHERE FilmID = " + filmID;
            DB.makeQuery(sql);
        }

        String sql = "INSERT INTO Filmmedia (FilmmediaID, FilmID, MediaID, Stock, Price) VALUES (null, ?, ?, ?, ?);";
        DefaultTableModel model = (DefaultTableModel) jTableMedia.getModel();
        if (model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(filmID); //add FilmID            
            data.add(model.getValueAt(i, 0)); //add mediaID            
            data.add(model.getValueAt(i, 2)); //add stock            
            data.add(model.getValueAt(i, 3)); //add price

            DB.makeQuery(sql, data);
        }
    }

    private void jButtonCrewSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrewSearchActionPerformed
        String[] names = Utils.splitByLastName(jTextFieldCrewSearch.getText());

        String sql = "Select CrewID, CONCAT(Firstname, ' ', Lastname) FROM Crew";
        ArrayList<Object> data = new ArrayList<>();

        if (names.length == 1 && !names[0].isEmpty()) {
            sql += " WHERE Firstname = ? OR Lastname = ?;";
            data.add(names[0]);
            data.add(names[0]);

        } else if (names.length == 2) {
            sql += " WHERE Firstname = ? AND Lastname = ?;";
            data.add(names[0]);
            data.add(names[1]);

        } else {
            sql += " WHERE 1;";
        }

        DB.connect();
        ResultSet results = DB.makeQuery(sql, data);
        DB.close();

        jTableCrewSearch.removeAll();
        Utils.populateTable(jTableCrewSearch, results);
    }//GEN-LAST:event_jButtonCrewSearchActionPerformed

    private void jButtonAddCrewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddCrewActionPerformed
        int selectedRow = jTableCrewSearch.getSelectedRow();

        if (selectedRow == -1 || jComboBoxRoles.getSelectedItem().equals("Select role")) {
            return;
        }

        Integer ID = (Integer) jTableCrewSearch.getValueAt(selectedRow, 0);
        String name = (String) jTableCrewSearch.getValueAt(selectedRow, 1);
        String role = (String) jComboBoxRoles.getSelectedItem();
        String character = jTextFieldCharName.getText();

        Object[] newRow = new Object[4];
        newRow[0] = ID;
        newRow[1] = name;
        newRow[2] = role;
        if (character.isEmpty()) {
            newRow[3] = null;
        } else {
            newRow[3] = character;
        }

        Utils.addTableEntry(jTableCrewSelected, newRow);
        crewChanged = true;
    }//GEN-LAST:event_jButtonAddCrewActionPerformed

    private void jButtonCrewRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrewRemoveActionPerformed
        int selectedRow = jTableCrewSelected.getSelectedRow();
        if (selectedRow != -1) {
            crewChanged = true;
        }
        Utils.removeTableEntry(jTableCrewSelected, selectedRow);
    }//GEN-LAST:event_jButtonCrewRemoveActionPerformed

    private void jButtonAddCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddCountryActionPerformed
        String selection = (String) jComboBoxCountry.getSelectedItem();
        if (selection.equals("Select country")) {
            return;
        }
        String[] splitSelection = selection.split(" - ");
        Integer ID = Integer.valueOf(splitSelection[0]);
        String country = splitSelection[1];

        Object[] newRow = new Object[2];
        newRow[0] = ID;
        newRow[1] = country;

        Utils.addTableEntry(jTableCountries, newRow);
        countriesChanged = true;
    }//GEN-LAST:event_jButtonAddCountryActionPerformed

    private void jButtonAddLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddLanguageActionPerformed

        String selection = (String) jComboBoxLanguage.getSelectedItem();
        if (selection.equals("Select language")) {
            return;
        }

        String[] splitSelection = selection.split(" - ");
        Integer ID = Integer.valueOf(splitSelection[0]);
        String language = splitSelection[1];

        Object[] newRow = new Object[3];
        newRow[0] = ID;
        newRow[1] = language;
        if (jRadioButtonAudio.isSelected()) {
            newRow[2] = "Audio";
        } else {
            newRow[2] = "Subtitle";
        }

        Utils.addTableEntry(jTableLanguages, newRow);
        languageChanged = true;
    }//GEN-LAST:event_jButtonAddLanguageActionPerformed

    private void jButtonRemoveCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveCountryActionPerformed
        int selectedRow = jTableCountries.getSelectedRow();
        if (selectedRow != -1) {
            countriesChanged = true;
        }
        Utils.removeTableEntry(jTableCountries, selectedRow);
    }//GEN-LAST:event_jButtonRemoveCountryActionPerformed

    private void jButtonRemoveLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveLanguageActionPerformed
        int selectedRow = jTableLanguages.getSelectedRow();
        if (selectedRow != -1) {
            languageChanged = true;
        }
        Utils.removeTableEntry(jTableLanguages, selectedRow);
    }//GEN-LAST:event_jButtonRemoveLanguageActionPerformed

    private void jButtonAddRatingKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddRatingKeywordActionPerformed
        String selection = (String) jComboBoxRatingKeywords.getSelectedItem();
        if (selection.equals("Select keyword")) {
            return;
        }

        String[] splitSelection = selection.split(" - ");
        Integer ID = Integer.valueOf(splitSelection[0]);
        String keyword = splitSelection[1];

        Object[] newRow = new Object[2];
        newRow[0] = ID;
        newRow[1] = keyword;

        Utils.addTableEntry(jTableRatingKeywords, newRow);
        ratingKeywordsChanged = true;
    }//GEN-LAST:event_jButtonAddRatingKeywordActionPerformed

    private void jButtonAddGenreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddGenreActionPerformed
        String selection = (String) jComboBoxGenres.getSelectedItem();
        if (selection.equals("Select genre")) {
            return;
        }

        String[] splitSelection = selection.split(" - ");
        Integer ID = Integer.valueOf(splitSelection[0]);
        String genre = splitSelection[1];

        Object[] newRow = new Object[2];
        newRow[0] = ID;
        newRow[1] = genre;

        Utils.addTableEntry(jTableGenres, newRow);
        genreChanged = true;
    }//GEN-LAST:event_jButtonAddGenreActionPerformed

    private void jButtonRemoveKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveKeywordActionPerformed
        int selectedRow = jTableRatingKeywords.getSelectedRow();
        if (selectedRow != -1) {
            ratingKeywordsChanged = true;
        }
        Utils.removeTableEntry(jTableRatingKeywords, selectedRow);
    }//GEN-LAST:event_jButtonRemoveKeywordActionPerformed

    private void jButtonRemoveGenreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveGenreActionPerformed
        int selectedRow = jTableGenres.getSelectedRow();
        if (selectedRow != -1) {
            genreChanged = true;
        }
        Utils.removeTableEntry(jTableGenres, selectedRow);
    }//GEN-LAST:event_jButtonRemoveGenreActionPerformed

    private void jButtonAddMediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddMediaActionPerformed
        String selection = (String) jComboBoxMedia.getSelectedItem();
        // Make sure all info is entered
        if (selection.equals("Select media") || jTextFieldMediaStock.getText().isEmpty() || jTextFieldPrice.getText().isEmpty()) {
            return;
        }

        String[] splitSelection = selection.split(" - ");
        Integer ID = Integer.valueOf(splitSelection[0]);
        String media = splitSelection[1];

        Integer stockInt;
        Float priceFloat;
        try {
            stockInt = Integer.valueOf(jTextFieldMediaStock.getText());
            priceFloat = Float.valueOf(jTextFieldPrice.getText());
        } catch (Exception e) {
            return;
        }
        if (stockInt < 0) {
            stockInt = 0;
        }

        Object[] newRow = new Object[4];
        newRow[0] = ID;
        newRow[1] = media;
        newRow[2] = stockInt;
        newRow[3] = priceFloat;

        Utils.addTableEntry(jTableMedia, newRow);
        mediaChanged = true;
    }//GEN-LAST:event_jButtonAddMediaActionPerformed

    private void jButtonRemoveMediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveMediaActionPerformed
        int selectedRow = jTableMedia.getSelectedRow();
        if (selectedRow != -1) {
            mediaChanged = true;
        }
        Utils.removeTableEntry(jTableMedia, selectedRow);
    }//GEN-LAST:event_jButtonRemoveMediaActionPerformed

    private void jButtonAddImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddImageActionPerformed
        if (imageStream != null) {
            imageStream = null;
            jButtonAddImage.setText("Add Image");
            jLabelImage.setText("");
        } else {

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif", "png");
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
            chooser.setMultiSelectionEnabled(false);

            int choice = chooser.showOpenDialog(this);

            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                try {
                    imageStream = new FileInputStream(file);
                    jButtonAddImage.setText("Remove Image");
                    jLabelImage.setText(file.getName());
                } catch (Exception e) {
                    jLabelImage.setText("Error: Image failed to load");
                }
            }
        }
    }//GEN-LAST:event_jButtonAddImageActionPerformed

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
            java.util.logging.Logger.getLogger(AddFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddFilm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddFilm(0, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLanguage;
    private javax.swing.JButton jButtonAddCountry;
    private javax.swing.JButton jButtonAddCrew;
    private javax.swing.JButton jButtonAddGenre;
    private javax.swing.JButton jButtonAddImage;
    private javax.swing.JButton jButtonAddLanguage;
    private javax.swing.JButton jButtonAddMedia;
    private javax.swing.JButton jButtonAddRatingKeyword;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCrewRemove;
    private javax.swing.JButton jButtonCrewSearch;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JButton jButtonRemoveCountry;
    private javax.swing.JButton jButtonRemoveGenre;
    private javax.swing.JButton jButtonRemoveKeyword;
    private javax.swing.JButton jButtonRemoveLanguage;
    private javax.swing.JButton jButtonRemoveMedia;
    private javax.swing.JComboBox<String> jComboBoxCountry;
    private javax.swing.JComboBox<String> jComboBoxGenres;
    private javax.swing.JComboBox<String> jComboBoxLanguage;
    private javax.swing.JComboBox<String> jComboBoxMedia;
    private javax.swing.JComboBox<String> jComboBoxRatingKeywords;
    private javax.swing.JComboBox<String> jComboBoxRatings;
    private javax.swing.JComboBox<String> jComboBoxRoles;
    private javax.swing.JComboBox<String> jComboBoxStudio;
    private javax.swing.JComboBox<String> jComboBoxType;
    private javax.swing.JLabel jLabelCharName;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelFilmTitle;
    private javax.swing.JLabel jLabelFilmYear;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelPrice;
    private javax.swing.JLabel jLabelRating;
    private javax.swing.JLabel jLabelRuntime;
    private javax.swing.JLabel jLabelStock;
    private javax.swing.JLabel jLabelStudio;
    private javax.swing.JLabel jLabelType;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelEnterInfo1;
    private javax.swing.JPanel jPanelEnterInfo2;
    private javax.swing.JPanel jPanelEnterInfo3;
    private javax.swing.JPanel jPanelEnterInfo4;
    private javax.swing.JPanel jPanelEnterInfo5;
    private javax.swing.JRadioButton jRadioButtonAudio;
    private javax.swing.JRadioButton jRadioButtonSubtitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPaneDescription;
    private javax.swing.JTable jTableCountries;
    private javax.swing.JTable jTableCrewSearch;
    private javax.swing.JTable jTableCrewSelected;
    private javax.swing.JTable jTableGenres;
    private javax.swing.JTable jTableLanguages;
    private javax.swing.JTable jTableMedia;
    private javax.swing.JTable jTableRatingKeywords;
    private javax.swing.JTextArea jTextAreaDescription;
    private javax.swing.JTextField jTextFieldCharName;
    private javax.swing.JTextField jTextFieldCrewSearch;
    private javax.swing.JTextField jTextFieldMediaStock;
    private javax.swing.JTextField jTextFieldPrice;
    private javax.swing.JTextField jTextFieldRuntime;
    private javax.swing.JTextField jTextFieldTitle;
    private javax.swing.JTextField jTextFieldYear;
    // End of variables declaration//GEN-END:variables

    private boolean updating; //is existing film being updated?
    private String currentCard; //UI layout card currently being viewed
    private DBManager DB; //handles database transactions
    private InputStream imageStream; //used to store image loaded from disk
    private Blob imageBlob; //used to store image loaded from database
    private int oldFilmID; //ID number of film being edited

    //which information has been edited?
    private boolean crewChanged;
    private boolean countriesChanged;
    private boolean languageChanged;
    private boolean ratingKeywordsChanged;
    private boolean genreChanged;
    private boolean mediaChanged;
}
