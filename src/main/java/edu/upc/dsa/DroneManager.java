package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.Drone;
import edu.upc.dsa.models.FlightPlan;
import edu.upc.dsa.models.Pilot;
import edu.upc.dsa.models.Position;

import java.util.List;

public interface DroneManager {

    void addDrone(String id, String name, String manufacturer, String model);

    void addPilot(String id, String name, String surname);

    List<Drone> getDronesByFlightHours();

    List<Pilot> getPilotsByFlightHours();

    void storeDrone(String droneId) throws DroneNotFoundException, DroneInMaintenanceException;

    Drone repairDrone() throws EmptyStorageException;

    FlightPlan addFlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions)
            throws DroneNotFoundException, PilotNotFoundException, DroneInMaintenanceException,
            FlightPlanOverlapException, WrongDateException;

    List<FlightPlan> getFlightPlansByPilot(String pilotId) throws PilotNotFoundException;

    List<FlightPlan> getFlightPlansByDrone(String droneId) throws DroneNotFoundException;

    int numDrones();

    int numPilots();

    int numDronesInStorage();

    void clear();
}
