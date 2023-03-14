package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Driver;
import model.Manager;
import model.User;

import java.sql.*;

public class DbUtils {
    public static Connection connectToBd() {
        Connection conn = null;

        String DB_URL = "jdbc:mysql://localhost/transport_system";
        String USER = "root";
        String PASS = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return conn;

    }

    //I should connect in to one method getting data from drivers and managers
    public static ObservableList<Driver> getDataDrivers() {
        Connection conn = connectToBd();
        ObservableList<Driver> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM drivers");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Driver(rs.getInt("id"), rs.getString("login"), rs.getString("password"), rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(10), rs.getDate(7).toLocalDate(), rs.getString(8), rs.getString(9)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static ObservableList<Manager> getDataManagers() {
        Connection conn = connectToBd();
        ObservableList<Manager> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Manager(rs.getInt("id"), rs.getString("login"), rs.getString("password"), rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(7), rs.getString(8), rs.getDate(9).toLocalDate(), rs.getBoolean(10)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void disconnect(Connection connection, Statement statement) {
        try {
            if (connection != null && statement != null) {
                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static User validateUser(String login, String password) throws SQLException {
        Connection connection = connectToBd();
        String sql = "SELECT * FROM drivers WHERE login=? AND password=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        ResultSet rs = preparedStatement.executeQuery();
        User user = null;
        while (rs.next()) {
            user = new Driver(rs.getString("login"), rs.getString("password"), rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(10), rs.getDate(7).toLocalDate(), rs.getString(8), rs.getString(9));
        }
        if (user == null) { // if user not found in drivers table, check managers table
            sql = "SELECT * FROM managers WHERE login=? AND password=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user = new Manager(rs.getString("login"), rs.getString("password"), rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(7), rs.getString(8), rs.getBoolean(10));
            }
        }
        disconnect(connection, preparedStatement);
        return user;
    }


}



