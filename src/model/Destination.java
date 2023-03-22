package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static utils.DbUtils.connectToBd;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class Destination {
    private int id;
    private String startCity;
    private long startLn;
    private long startLat;
    private String endCity;
    private long endLn;
    private long endLat;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    //List<Checkpoint> checkpointList
    private String checkpointList;
    private Truck truck;
    private Manager responsibleManagers;
    //private TemporaryManagerType temporaryManagerType;
    private Cargo cargo;

    public Destination(int id, String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, LocalDate dateCreated, LocalDate dateUpdated) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.dateCreated = LocalDate.now();
        this.dateUpdated = LocalDate.now();
    }


    public String toString() {
        return "Start city:  " + startCity + "  Start longitude:  " + startLn + "  Start longitude:  " + startLat + "  City of destination:  " + endCity + "  End longitude:  " + endLn + "  End latitude:  " + endLat + "  Start of the route:  " + dateCreated + "  Updated route date:  " + dateUpdated;
    }

    public Destination(int id, String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, LocalDate dateCreated, LocalDate dateUpdated, Truck truck, Manager responsibleManagers, Cargo cargo) {
        this.id = id;
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.truck = truck;
        this.responsibleManagers = responsibleManagers;
        this.cargo = cargo;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, String checkpointList, Manager responsibleManagers) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.checkpointList = checkpointList;
        this.responsibleManagers = responsibleManagers;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, String checkpointList, Truck truck, Manager responsibleManagers, Cargo cargo) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.checkpointList = checkpointList;
        this.truck = truck;
        this.responsibleManagers = responsibleManagers;
        this.cargo = cargo;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, LocalDate dateCreated, LocalDate dateUpdated, String checkpointList, Manager responsibleManagers) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.checkpointList = checkpointList;
        this.responsibleManagers = responsibleManagers;
    }

    public Destination(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Manager getResponsibleManagers() {
        return responsibleManagers;
    }

    public void setResponsibleManagers(Manager responsibleManagers) {
        this.responsibleManagers = responsibleManagers;
    }

    // update valueOf method
    public Destination(int id, String startCity, long startLn, long startLat, String endCity, long endLn, long endLat,
                       LocalDate dateCreated, LocalDate dateUpdated, String checkpointList, Truck truck,
                       List<Manager> responsibleManagers, Cargo cargo) {
        this.id = id;
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.checkpointList = checkpointList;
        this.truck = truck;
        this.responsibleManagers = (Manager) responsibleManagers;
        this.cargo = cargo;

        // Create a list of managers based on the foreign key manager_id
        List<Manager> managers = new ArrayList<>();
        try {
            Connection conn = connectToBd();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE id IN (SELECT manager_id FROM destination WHERE id = ?)");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Manager manager = new Manager(rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6).toLocalDate(),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9).toLocalDate(),
                        rs.getBoolean(10));
                managers.add(manager);
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.responsibleManagers = (Manager) managers;
    }
}


