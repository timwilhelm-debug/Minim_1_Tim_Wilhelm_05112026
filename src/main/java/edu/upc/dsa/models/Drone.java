package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class Drone {
    private String id;
    private String name;
    private String manufacturer;
    private String model;
    private double flightHours;
    private boolean inMaintenance;
    private List<FlightPlan> flightPlans;

    public Drone() {
        this.flightPlans = new ArrayList<>();
    }

    public Drone(String id, String name, String manufacturer, String model) {
        this();
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.flightHours = 0;
        this.inMaintenance = false;
    }

    public void addFlightPlan(FlightPlan plan) {
        this.flightPlans.add(plan);
        this.flightHours = this.flightHours + plan.getHours();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getFlightHours() {
        return flightHours;
    }

    public void setFlightHours(double flightHours) {
        this.flightHours = flightHours;
    }

    public boolean isInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public List<FlightPlan> getFlightPlans() {
        return flightPlans;
    }

    public void setFlightPlans(List<FlightPlan> flightPlans) {
        this.flightPlans = flightPlans;
    }

    @Override
    public String toString() {
        return "Drone [id=" + id + ", name=" + name + ", flightHours=" + flightHours
                + ", inMaintenance=" + inMaintenance + "]";
    }
}
