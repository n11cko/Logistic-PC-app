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
public class Cargo {
    private int id;
    private String title = null;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private double weight;
    private CargoType cargoType;
    private String description;
    private String customer;

    public Cargo(String title, double weight, CargoType cargoType, String description, String customer) {
        this.title = title;
        this.weight = weight;
        this.cargoType = cargoType;
        this.description = description;
        this.customer = customer;
        this.dateCreated=LocalDate.now();
    }

    public Cargo(int id, String title, double weight, CargoType cargoType, String description, String customer) {
        this.id = id;
        this.title = title;
        this.weight = weight;
        this.cargoType = cargoType;
        this.description = description;
        this.customer = customer;
    }

    public String toString() {
        return "Title:  " + title + "  Weight of cargo:  " + weight + "  Type of cargo:  " + cargoType + "  Description:  " + description + "  Customer:  " + customer + "  Date of creation:  " + dateCreated + "  Date of update:  " + dateUpdated;
    }

    public Cargo(String title, double weight, CargoType cargoType, String description, String customer, LocalDate dateCreated, LocalDate dateUpdated) {
        this.title = title;
        this.weight = weight;
        this.cargoType = cargoType;
        this.description = description;
        this.customer = customer;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }


}
