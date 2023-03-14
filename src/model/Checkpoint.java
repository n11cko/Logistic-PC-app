package model;

import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Checkpoint {
    private int id;
    private String title;
    private boolean longStop;
    private TemporaryManagerType temporaryManagerType;
    private LocalDate dateArrived;
    private ObservableList<Destination> linkedDestinations;

    public String toString() {
        if(!longStop){
            return "Checkpoint city:  " + title + "  Type of stop:  " + "short stop" +"  Date of checkpoint  " + dateArrived + "  " + temporaryManagerType;
        } else return "Checkpoint city:  " + title + "  Type of stop:  " + "long stop" +"  Date of checkpoint  " + dateArrived + "  "+ "  Responsible manager:  " + temporaryManagerType;
    }

    public Checkpoint(String title, boolean longStop, LocalDate dateArrived) {
        this.title = title;
        this.longStop = longStop;
        this.dateArrived = dateArrived;
    }

    public Checkpoint(String title, boolean longStop) {
        this.title = title;
        this.longStop = longStop;
    }

    public Checkpoint(String title, boolean longStop,LocalDate dateArrived, TemporaryManagerType temporaryManagerType) {
        this.title = title;
        this.longStop = longStop;
        this.dateArrived=dateArrived;
        this.temporaryManagerType = temporaryManagerType;
    }
    public void setLinkedDestinations(ObservableList<Destination> destinations) {
        linkedDestinations = destinations;
    }
}
