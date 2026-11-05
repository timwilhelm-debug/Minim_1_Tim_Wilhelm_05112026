package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.Drone;
import edu.upc.dsa.models.FlightPlan;
import edu.upc.dsa.models.Pilot;
import edu.upc.dsa.models.Position;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DroneManagerTest {
    DroneManager dm;
    List<Position> positions;

    @Before
    public void setUp() {
        this.dm = DroneManagerImpl.getInstance();
        this.dm.addDrone("D1", "Falcon", "DJI", "Mavic 3");
        this.dm.addDrone("D2", "Eagle", "Parrot", "Anafi");
        this.dm.addDrone("D3", "Hawk", "DJI", "Mini 4");
        this.dm.addPilot("P1", "Anna", "Garcia");
        this.dm.addPilot("P2", "Pau", "Martinez");

        this.positions = new ArrayList<>();
        this.positions.add(new Position(41.27, 1.98));
        this.positions.add(new Position(41.30, 2.00));
    }

    @After
    public void tearDown() {
        this.dm.clear();
    }

    @Test
    public void storeAndRepairDroneTest() throws Exception {
        Assert.assertEquals(0, dm.numDronesInStorage());

        dm.storeDrone("D1");
        dm.storeDrone("D2");
        dm.storeDrone("D3");
        Assert.assertEquals(3, dm.numDronesInStorage());

        Drone d = dm.repairDrone();
        Assert.assertEquals("D3", d.getId());
        Assert.assertFalse(d.isInMaintenance());
        Assert.assertEquals("D2", dm.repairDrone().getId());
        Assert.assertEquals("D1", dm.repairDrone().getId());
        Assert.assertEquals(0, dm.numDronesInStorage());

        Assert.assertThrows(EmptyStorageException.class, () -> dm.repairDrone());
        Assert.assertThrows(DroneNotFoundException.class, () -> dm.storeDrone("XXX"));
    }

    @Test
    public void addFlightPlanTest() throws Exception {
        FlightPlan plan = dm.addFlightPlan("D1", "P1", "2026-11-05 10:00", 2, positions);
        Assert.assertEquals("D1", plan.getDroneId());
        Assert.assertEquals(2, plan.getPositions().size());

        Assert.assertThrows(FlightPlanOverlapException.class, () ->
                dm.addFlightPlan("D1", "P2", "2026-11-05 11:00", 1, positions));
        Assert.assertThrows(FlightPlanOverlapException.class, () ->
                dm.addFlightPlan("D2", "P1", "2026-11-05 11:30", 1, positions));
        dm.addFlightPlan("D1", "P1", "2026-11-05 12:00", 1, positions);

        dm.storeDrone("D3");
        Assert.assertThrows(DroneInMaintenanceException.class, () ->
                dm.addFlightPlan("D3", "P2", "2026-11-06 10:00", 1, positions));

        Assert.assertThrows(DroneNotFoundException.class, () ->
                dm.addFlightPlan("XXX", "P1", "2026-11-07 10:00", 1, positions));
        Assert.assertThrows(PilotNotFoundException.class, () ->
                dm.addFlightPlan("D2", "XXX", "2026-11-07 10:00", 1, positions));
        Assert.assertThrows(WrongDateException.class, () ->
                dm.addFlightPlan("D2", "P2", "dema al mati", 1, positions));
    }

    @Test
    public void sortByFlightHoursTest() throws Exception {
        dm.addFlightPlan("D2", "P2", "2026-11-05 10:00", 5, positions);
        dm.addFlightPlan("D3", "P1", "2026-11-05 10:00", 2, positions);
        dm.addFlightPlan("D3", "P1", "2026-11-06 10:00", 1, positions);

        List<Drone> drones = dm.getDronesByFlightHours();
        Assert.assertEquals("D2", drones.get(0).getId());
        Assert.assertEquals("D3", drones.get(1).getId());
        Assert.assertEquals("D1", drones.get(2).getId());
        Assert.assertEquals(5, drones.get(0).getFlightHours(), 0.01);

        List<Pilot> pilots = dm.getPilotsByFlightHours();
        Assert.assertEquals("P2", pilots.get(0).getId());
        Assert.assertEquals("P1", pilots.get(1).getId());
    }

    @Test
    public void getFlightPlansTest() throws Exception {
        dm.addFlightPlan("D1", "P1", "2026-11-05 10:00", 1, positions);
        dm.addFlightPlan("D2", "P1", "2026-11-06 10:00", 1, positions);
        dm.addFlightPlan("D1", "P2", "2026-11-07 10:00", 1, positions);

        Assert.assertEquals(2, dm.getFlightPlansByPilot("P1").size());
        Assert.assertEquals(1, dm.getFlightPlansByPilot("P2").size());
        Assert.assertEquals(2, dm.getFlightPlansByDrone("D1").size());
        Assert.assertEquals(0, dm.getFlightPlansByDrone("D3").size());

        Assert.assertThrows(PilotNotFoundException.class, () -> dm.getFlightPlansByPilot("XXX"));
        Assert.assertThrows(DroneNotFoundException.class, () -> dm.getFlightPlansByDrone("XXX"));
    }
}
