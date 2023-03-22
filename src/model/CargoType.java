package model;

public enum CargoType {
    PAPER, ALCOHOL, MIX, ELECTRONICS, CONSUMER_ELECTRONICS, FOOD;

    public String getValue() {
        return name();
    }
}
