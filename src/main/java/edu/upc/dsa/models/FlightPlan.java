package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class FlightPlan {
    private String droneId;
    private String pilotId;
    private String date;
    private double hours;
    private List<Position> positions;

    public FlightPlan() {
        this.positions = new ArrayList<>();
    }

    public FlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions) {
        this.droneId = droneId;
        this.pilotId = pilotId;
        this.date = date;
        this.hours = hours;
        this.positions = positions;
    }

    public String getDroneId() {
        return droneId;
    }

    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "FlightPlan [drone=" + droneId + ", pilot=" + pilotId + ", date=" + date
                + ", hours=" + hours + ", positions=" + positions + "]";
    }
}
