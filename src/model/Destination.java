package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    //List<Manager> responsibleManagers
    private TemporaryManagerType temporaryManagerType;
    private Cargo cargo;

    public String toString() {
        return "Start city:  " + startCity + "  Start longitude:  " + startLn + "  Start longitude:  " + startLat + "  City of destination:  " + endCity + "  End longitude:  " + endLn + "  End latitude:  " + endLat + "  Start of the route:  " + dateCreated + "  Updated route date:  " + dateUpdated;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, String checkpointList, TemporaryManagerType temporaryManagerType) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.checkpointList = checkpointList;
        this.temporaryManagerType = temporaryManagerType;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, String checkpointList, Truck truck, TemporaryManagerType temporaryManagerType, Cargo cargo) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.checkpointList = checkpointList;
        this.truck = truck;
        this.temporaryManagerType = temporaryManagerType;
        this.cargo = cargo;
    }

    public Destination(String startCity, long startLn, long startLat, String endCity, long endLn, long endLat, LocalDate dateCreated, LocalDate dateUpdated, String checkpointList, TemporaryManagerType temporaryManagerType) {
        this.startCity = startCity;
        this.startLn = startLn;
        this.startLat = startLat;
        this.endCity = endCity;
        this.endLn = endLn;
        this.endLat = endLat;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.checkpointList = checkpointList;
        this.temporaryManagerType = temporaryManagerType;
    }

    public Destination(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }
}
