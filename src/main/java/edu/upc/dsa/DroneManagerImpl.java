package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.Drone;
import edu.upc.dsa.models.FlightPlan;
import edu.upc.dsa.models.Pilot;
import edu.upc.dsa.models.Position;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DroneManagerImpl implements DroneManager {
    private static DroneManager instance;
    final static Logger logger = Logger.getLogger(DroneManagerImpl.class);

    private HashMap<String, Drone> drones;
    private HashMap<String, Pilot> pilots;
    private Stack<Drone> storage;

    private DroneManagerImpl() {
        this.drones = new HashMap<>();
        this.pilots = new HashMap<>();
        this.storage = new Stack<>();
    }

    public static DroneManager getInstance() {
        if (instance == null) instance = new DroneManagerImpl();
        return instance;
    }

    @Override
    public void addDrone(String id, String name, String manufacturer, String model) {
        logger.info("addDrone(" + id + ", " + name + ", " + manufacturer + ", " + model + ")");
        this.drones.put(id, new Drone(id, name, manufacturer, model));
        logger.info("addDrone(" + id + "): afegit");
    }

    @Override
    public void addPilot(String id, String name, String surname) {
        logger.info("addPilot(" + id + ", " + name + ", " + surname + ")");
        this.pilots.put(id, new Pilot(id, name, surname));
        logger.info("addPilot(" + id + "): afegit");
    }

    @Override
    public List<Drone> getDronesByFlightHours() {
        logger.info("getDronesByFlightHours()");
        List<Drone> result = new ArrayList<>(this.drones.values());
        Collections.sort(result, new Comparator<Drone>() {
            @Override
            public int compare(Drone d1, Drone d2) {
                return Double.compare(d2.getFlightHours(), d1.getFlightHours());
            }
        });
        logger.info("getDronesByFlightHours(): " + result);
        return result;
    }

    @Override
    public List<Pilot> getPilotsByFlightHours() {
        logger.info("getPilotsByFlightHours()");
        List<Pilot> result = new ArrayList<>(this.pilots.values());
        Collections.sort(result, new Comparator<Pilot>() {
            @Override
            public int compare(Pilot p1, Pilot p2) {
                return Double.compare(p2.getFlightHours(), p1.getFlightHours());
            }
        });
        logger.info("getPilotsByFlightHours(): " + result);
        return result;
    }

    @Override
    public void storeDrone(String droneId) throws DroneNotFoundException, DroneInMaintenanceException {
        logger.info("storeDrone(" + droneId + ")");
        Drone drone = this.drones.get(droneId);
        if (drone == null) {
            logger.error("Dron no trobat: " + droneId);
            throw new DroneNotFoundException("Dron no trobat: " + droneId);
        }
        if (drone.isInMaintenance()) {
            logger.error("El dron ja es al magatzem: " + droneId);
            throw new DroneInMaintenanceException("El dron ja es al magatzem: " + droneId);
        }
        drone.setInMaintenance(true);
        this.storage.push(drone);
        logger.info("storeDrone(" + droneId + "): guardat. Drons al magatzem: " + this.storage.size());
    }

    @Override
    public Drone repairDrone() throws EmptyStorageException {
        logger.info("repairDrone()");
        if (this.storage.isEmpty()) {
            logger.error("El magatzem es buit");
            throw new EmptyStorageException("El magatzem es buit");
        }
        Drone drone = this.storage.pop();
        drone.setInMaintenance(false);
        logger.info("repairDrone(): " + drone);
        return drone;
    }

    @Override
    public FlightPlan addFlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions)
            throws DroneNotFoundException, PilotNotFoundException, DroneInMaintenanceException,
            FlightPlanOverlapException, WrongDateException {
        logger.info("addFlightPlan(" + droneId + ", " + pilotId + ", " + date + ", " + hours + ", " + positions + ")");

        toMillis(date);

        Drone drone = this.drones.get(droneId);
        if (drone == null) {
            logger.error("Dron no trobat: " + droneId);
            throw new DroneNotFoundException("Dron no trobat: " + droneId);
        }
        Pilot pilot = this.pilots.get(pilotId);
        if (pilot == null) {
            logger.error("Pilot no trobat: " + pilotId);
            throw new PilotNotFoundException("Pilot no trobat: " + pilotId);
        }

        if (drone.isInMaintenance()) {
            logger.error("El dron es en manteniment: " + droneId);
            throw new DroneInMaintenanceException("El dron es en manteniment: " + droneId);
        }

        FlightPlan newPlan = new FlightPlan(droneId, pilotId, date, hours, positions);
        for (FlightPlan p : drone.getFlightPlans()) {
            if (overlaps(newPlan, p)) {
                logger.error("El dron " + droneId + " ja te un pla en aquesta franja: " + p);
                throw new FlightPlanOverlapException("El dron " + droneId + " ja te un pla en aquesta franja");
            }
        }
        for (FlightPlan p : pilot.getFlightPlans()) {
            if (overlaps(newPlan, p)) {
                logger.error("El pilot " + pilotId + " ja te un pla en aquesta franja: " + p);
                throw new FlightPlanOverlapException("El pilot " + pilotId + " ja te un pla en aquesta franja");
            }
        }

        drone.addFlightPlan(newPlan);
        pilot.addFlightPlan(newPlan);

        logger.info("addFlightPlan(): " + newPlan);
        return newPlan;
    }

    @Override
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) throws PilotNotFoundException {
        logger.info("getFlightPlansByPilot(" + pilotId + ")");
        Pilot pilot = this.pilots.get(pilotId);
        if (pilot == null) {
            logger.error("Pilot no trobat: " + pilotId);
            throw new PilotNotFoundException("Pilot no trobat: " + pilotId);
        }
        List<FlightPlan> result = pilot.getFlightPlans();
        logger.info("getFlightPlansByPilot(" + pilotId + "): " + result);
        return result;
    }

    @Override
    public List<FlightPlan> getFlightPlansByDrone(String droneId) throws DroneNotFoundException {
        logger.info("getFlightPlansByDrone(" + droneId + ")");
        Drone drone = this.drones.get(droneId);
        if (drone == null) {
            logger.error("Dron no trobat: " + droneId);
            throw new DroneNotFoundException("Dron no trobat: " + droneId);
        }
        List<FlightPlan> result = drone.getFlightPlans();
        logger.info("getFlightPlansByDrone(" + droneId + "): " + result);
        return result;
    }

    @Override
    public int numDrones() {
        return this.drones.size();
    }

    @Override
    public int numPilots() {
        return this.pilots.size();
    }

    @Override
    public int numDronesInStorage() {
        return this.storage.size();
    }

    @Override
    public void clear() {
        this.drones.clear();
        this.pilots.clear();
        this.storage.clear();
    }


    private long toMillis(String date) throws WrongDateException {
        if (date == null) {
            logger.error("Data buida");
            throw new WrongDateException("Data buida");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            logger.error("Data incorrecta: " + date);
            throw new WrongDateException("Data incorrecta: " + date);
        }
    }

    private boolean overlaps(FlightPlan a, FlightPlan b) throws WrongDateException {
        long startA = toMillis(a.getDate());
        long endA = startA + (long) (a.getHours() * 3600 * 1000);
        long startB = toMillis(b.getDate());
        long endB = startB + (long) (b.getHours() * 3600 * 1000);
        return startA < endB && startB < endA;
    }
}
