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

import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author badi_
 */
public class Utils {

    public static void addToTable(JTable table, ResultSet result) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            int columnCount = result.getMetaData().getColumnCount();
            Object[] newRow = new Object[columnCount];

            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    newRow[i - 1] = result.getObject(i);
                }
                model.addRow(newRow);
            }

            model.fireTableDataChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateTable(JTable table, ResultSet result) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            int columnCount = result.getMetaData().getColumnCount();
            Object[] newRow = new Object[columnCount];

            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    newRow[i - 1] = result.getObject(i);
                }
                model.addRow(newRow);
            }

            model.fireTableDataChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadIntoTable(int filmID, String sql, JTable table, String[] datatypes, DBManager DB) {
        ArrayList data = new ArrayList();
        data.add(Integer.valueOf(filmID));
        ResultSet result = DB.makeQuery(sql, data);
        if (getResultSize(result) == 0) {
            return;
        }
        try {
            int columns = result.getMetaData().getColumnCount();
            while (result.next()) {
                Object[] newRow = new Object[columns];
                for (int i = 0; i < columns; i++) {
                    switch (datatypes[i]) {
                        case "int":
                            newRow[i] = Integer.valueOf(result.getInt(i + 1));
                            break;
                        case "string":
                            newRow[i] = result.getString(i + 1);
                            break;
                        case "float":
                            newRow[i] = Float.valueOf(result.getFloat(i + 1));
                            break;
                    }
                }
                addTableEntry(table, newRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeTableEntry(JTable table, int index) {
        if (index == -1) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.removeRow(index);
        model.fireTableDataChanged();
    }

    public static void addTableEntry(JTable table, Object[] newrow) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(newrow);
        model.fireTableDataChanged();
    }

    public static void populateComboBox(JComboBox box, ResultSet result, String defaultOption) {
        try {
            String[] items = new String[getResultSize(result) + 1];
            items[0] = defaultOption;

            int i = 1;
            while (result.next()) {
                items[i] = result.getString(1) + " - " + result.getString(2);
                i++;
            }

            box.setModel(new DefaultComboBoxModel<String>(items));
            box.setSelectedIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setComboBoxIndex(JComboBox box, int Id) {
        String selected = String.valueOf(Id);

        for (int i = 0; i < box.getItemCount(); i++) {
            String IdAtIndex = getSelectedID((String) box.getItemAt(i));
            if (IdAtIndex.equals(selected)) {
                box.setSelectedIndex(i);
                break;
            }
        }
    }

    public static String buildWhere(ArrayList<String> elements) {
        String result = " WHERE ";

        for (int i = 0; i < elements.size(); i++) {
            result += elements.get(i);
            if (i < elements.size() - 1) {
                result += " AND ";
            }
        }

        return result;
    }

    public static String[] splitByLastName(String name) {
        int index = name.lastIndexOf(" ");

        if (index == -1 || index == name.length() - 1) {
            return new String[]{name};
        }

        return new String[]{name.substring(0, index), name.substring(index + 1)};
    }

    public static int getResultSize(ResultSet result) {
        int size = 0;
        try {
            result.last();
            size = result.getRow();
            result.beforeFirst();
            return size;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getSelectedID(String selection) {
        if (!selection.contains(" - ")) {
            return "";
        }
        String[] splitSelection = selection.split(" ", 2);
        return splitSelection[0];
    }

    public static String getSelectedValue(String selection) {
        if (!selection.contains(" - ")) {
            return "";
        }
        String[] splitSelection = selection.split(" - ");
        return splitSelection[1];
    }

    public static Object getSelectedCellContent(int row, int column, JTable table) {
        row = table.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        return model.getValueAt(row, column);
    }
    
    public static void setSelectedCellContent(int row, int column, JTable table, Object value) {
        row = table.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setValueAt(value, row, column);
    }

    /**
     * Returns a java.sql.Date -object based on input String
     * Input must be in yyyy-mm-dd format.
     * @param dateString the date (yyyy-mm-dd)
     * @return java.sql.Date representation of date
     */
    public static Date getDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new Date(dateFormat.parse(dateString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String makeSalt(int length) {
        char[] characters = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] salt = new char[length];
        Random rnd = new Random();

        for (int i = 0; i < length; i++) {
            salt[i] = characters[rnd.nextInt(36)];
        }
        return new String(salt);
    }

    public static String addBreaks(String html) {
        html = html.trim();
        int i = 0;
        int counter = 0;
        while (i < html.length()) {
            //if character is a white space and line is over 80 characters long, insert <br> tag
            if (Character.isWhitespace(html.charAt(i)) && counter > 80) {
                html = html.substring(0, i) + "<br>" + html.substring(i);
                i += 5; //move i over the added <br> tag
                counter = 0;
            } else {
                i++;
                counter++;
            }
        }
        return html;
    }
    
    /**
     * 
     * @param rating
     * @param entity
     * @return 
     */
    public static String starText(float rating, boolean entity) {
        rating = (float) (Math.round(rating * 2) / 2.0);
        String stars = "";
        float counter = 0;
        String star;
        if (entity) {
            star = "&#9733;";
        } else {
            star = "*";
        }
        
        while (counter <= rating) {
            if (counter > 0 && counter % 1 == 0) {
                stars += star;
            } else if (counter == rating) {
                stars += "<small>" + star + "</small>";
            }
            counter += 0.5;
        }
        return stars;
    }
}
