package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public static ObservableList<Cargo> getCargo() throws SQLException {
        Connection conn = connectToBd();
        ObservableList<Cargo> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM cargo");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Create a new Cargo object from the database row
                list.add(new Cargo(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("weight"),
                        CargoType.valueOf(rs.getString("cargoType").toUpperCase()),
                        rs.getString("description"),
                        rs.getString("customer")));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return list;
    }

    public static ObservableList<Truck> getTruckData() throws SQLException {
        Connection conn = connectToBd();
        ObservableList<Truck> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM truck");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Create a new Cargo object from the database row
                list.add(new Truck(rs.getInt("id"),
                                rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("odometer"),
                        rs.getDouble("fuelTankCapacity"),
                        TyreType.valueOf(rs.getString("tyreType").toUpperCase())
                        ));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return list;
    }

    public static ObservableList<Destination> getDestinationData() throws SQLException {
        Connection conn = connectToBd();
        ObservableList<Destination> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM destination");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Truck truck = getTruckById(rs.getInt("truck_id"));
                Cargo cargo = getCargoById(rs.getInt("cargo_id"));
                Manager manager=getManagerById(rs.getInt("manager_id"));
                //List<Manager> managers = getManagersByDestinationId(rs.getInt("id")); // get managers from the database
                Destination destination = new Destination(
                        rs.getInt("id"),
                        rs.getString("startCity"),
                        rs.getLong("startLn"),
                        rs.getLong("startLat"),
                        rs.getString("endCity"),
                        rs.getLong("endLn"),
                        rs.getLong("endLat"),
                        rs.getObject("dateCreated", LocalDate.class),
                        rs.getObject("dateUpdated", LocalDate.class),
                        truck,
                        manager, // pass managers to valueOf method
                        cargo
                );
                list.add(destination);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return list;
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
    public static Manager getManagerById(int id) throws SQLException {
        Connection conn = connectToBd();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Manager(rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6).toLocalDate(),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9).toLocalDate(),
                        rs.getBoolean(10));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Truck getTruckById(int id) throws SQLException {
        Connection conn = connectToBd();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM truck WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Truck(
                        rs.getInt("id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("odometer"),
                        rs.getDouble("fuelTankCapacity"),
                        TyreType.valueOf(rs.getString("tyreType").toUpperCase())
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Cargo getCargoById(int id) throws SQLException {
        Connection conn = connectToBd();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM cargo WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cargo(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("weight"),
                        CargoType.valueOf(rs.getString("cargoType").toUpperCase()),
                        rs.getString("description"),
                        rs.getString("customer"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void setResponsibleManagers(Destination destination, List<Integer> managerIds) {
        try {
            Connection conn = connectToBd(); // implement this method to get a database connection
            PreparedStatement statement = conn.prepareStatement("INSERT INTO destination (id, manager_id) VALUES (?, ?)");

            for (Integer managerId : managerIds) {
                statement.setInt(1, destination.getId());
                statement.setInt(2, managerId);
                statement.executeUpdate();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Manager> getManagersByDestinationId(int destinationId) throws SQLException {
        Connection conn = connectToBd();
        List<Manager> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE id IN (SELECT manager_id FROM destination WHERE id = ?)");
            ps.setInt(1, destinationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Manager(rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6).toLocalDate(),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9).toLocalDate(),
                        rs.getBoolean(10)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }



}



