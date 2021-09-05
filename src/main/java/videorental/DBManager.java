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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author badi_
 */
public class DBManager {

    private String address;
    private String user;
    private char[] pwd;
    private Connection con;
    private int lastGeneratedKey;

    public DBManager(String address, String port, String user, char[] pwd) {

        this.address = "jdbc:mariadb://" + address + ":" + port + "/VIDEORENTAL";
        this.user = user;
        this.pwd = pwd;
        lastGeneratedKey = 0;
    }

    public boolean connect() {
        try {
            con = DriverManager.getConnection(address, user, new String(pwd));
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in database connection: " + e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in database connection: " + e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean test() {
        try {
            connect();
            boolean test = con.isValid(3);
            close();
            return test;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet makeQuery(String sql) {
        return makeQuery(sql, new ArrayList<Object>());
    }

    public ResultSet makeQuery(String sql, ArrayList<Object> data) {
        lastGeneratedKey = 0;

        try {
            //Prepare statement that can return generated keys for use in filling side tables when creating new item.        
            PreparedStatement statement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < data.size(); i++) {
                //System.out.println(data.get(i).getClass().getSimpleName());
                switch (data.get(i).getClass().getSimpleName()) {
                    case "String":
                        statement.setString(i + 1, (String) data.get(i));
                        break;
                    case "Integer":
                        statement.setInt(i + 1, (Integer) data.get(i));
                        break;
                    case "Float":
                        statement.setFloat(i + 1, (Float) data.get(i));
                        break;
                    case "Boolean":
                        statement.setBoolean(i + 1, (Boolean) data.get(i));
                        break;
                    case "Date":
                        statement.setDate(i + 1, (Date) data.get(i));
                        break;
                    //used for reading an image from disk into database
                    case "FileInputStream":
                        statement.setBlob(i + 1, (FileInputStream) data.get(i));
                        break;
                    //used for feeding blob from database back into database
                    case "MariaDbBlob":
                        statement.setBlob(i + 1, (Blob) data.get(i));
                        break;
                    case "NullParameter":
                        NullParameter np = (NullParameter) data.get(i);
                        statement.setNull(i + 1, np.getDataType());
                        break;
                }
            }

            //execute statement and get result set
            statement.execute();
            ResultSet result = statement.getResultSet();

            ResultSet lastKey = null;

            //Get last generated key. If a result was returned, store the key for retrieval
            lastKey = statement.getGeneratedKeys();
            if (lastKey.next()) {
                lastGeneratedKey = lastKey.getInt(1);
            } else {
                lastGeneratedKey = 0;
            }

            return result;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL error: " + e.getMessage(), "SQL error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    public int getLastGeneratedKey() {
        return lastGeneratedKey;
    }

    public String getUser() {
        return user;
    }
}
