package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Truck {
    private int id;
    private String make;
    private String model;
    private int year;
    private double odometer;
    private double fuelTankCapacity;
    private TyreType tyreType;

    public String toString() {
        return "Make:  " + make + "  Model:  " + model + "  Year:  " + year + "  Odometer:  " + odometer + "  Fuel capacity:  " + fuelTankCapacity + "  Tyre type:  " + tyreType;
    }


    public Truck(String make, String model, int year, double odometer, double fuelTankCapacity, TyreType tyreType) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.odometer = odometer;
        this.fuelTankCapacity = fuelTankCapacity;
        this.tyreType = tyreType;
    }
}
