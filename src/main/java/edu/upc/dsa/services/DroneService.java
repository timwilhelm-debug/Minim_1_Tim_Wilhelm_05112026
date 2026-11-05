package edu.upc.dsa.services;

import edu.upc.dsa.DroneManager;
import edu.upc.dsa.DroneManagerImpl;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/fleet", description = "Servei de gestio d'una flota de drons")
@Path("/fleet")
public class DroneService {

    private DroneManager dm;

    public DroneService() throws Exception {
        this.dm = DroneManagerImpl.getInstance();
        if (dm.numDrones() == 0) {
            this.dm.addDrone("D1", "Falcon", "DJI", "Mavic 3");
            this.dm.addDrone("D2", "Eagle", "Parrot", "Anafi");
            this.dm.addDrone("D3", "Hawk", "DJI", "Mini 4");
            this.dm.addPilot("P1", "Anna", "Garcia");
            this.dm.addPilot("P2", "Pau", "Martinez");
            List<Position> positions = new ArrayList<>();
            positions.add(new Position(41.27, 1.98));
            positions.add(new Position(41.30, 2.00));
            this.dm.addFlightPlan("D2", "P2", "2026-11-05 10:00", 3, positions);
        }
    }

    @GET
    @ApiOperation(value = "Llistat de drons ordenat per hores de vol (de mes a menys)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DroneTO.class, responseContainer = "List")
    })
    @Path("/drones")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDrones() {
        List<Drone> drones = this.dm.getDronesByFlightHours();
        List<DroneTO> result = new ArrayList<>();
        for (Drone d : drones) {
            result.add(new DroneTO(d));
        }
        GenericEntity<List<DroneTO>> entity = new GenericEntity<List<DroneTO>>(result) {};
        return Response.status(200).entity(entity).build();
    }

    @PUT
    @ApiOperation(value = "Guardar un dron al magatzem per fer-li manteniment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dron guardat al magatzem"),
            @ApiResponse(code = 404, message = "Dron no trobat"),
            @ApiResponse(code = 409, message = "El dron ja es al magatzem")
    })
    @Path("/drones/{id}/storage")
    public Response storeDrone(@PathParam("id") String id) {
        try {
            this.dm.storeDrone(id);
            return Response.status(200).build();
        } catch (DroneNotFoundException e) {
            return Response.status(404).build();
        } catch (DroneInMaintenanceException e) {
            return Response.status(409).build();
        }
    }

    @PUT
    @ApiOperation(value = "Reparar el dron mes proper a la porta i treure'l del magatzem")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dron reparat", response = DroneTO.class),
            @ApiResponse(code = 404, message = "El magatzem es buit")
    })
    @Path("/storage/repair")
    @Produces(MediaType.APPLICATION_JSON)
    public Response repairDrone() {
        try {
            Drone d = this.dm.repairDrone();
            return Response.status(200).entity(new DroneTO(d)).build();
        } catch (EmptyStorageException e) {
            return Response.status(404).build();
        }
    }

    @POST
    @ApiOperation(value = "Crear un pla de vol (data amb format yyyy-MM-dd HH:mm)")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Pla de vol creat", response = FlightPlan.class),
            @ApiResponse(code = 400, message = "Falten dades o la data es incorrecta"),
            @ApiResponse(code = 404, message = "Dron o pilot no trobat"),
            @ApiResponse(code = 409, message = "Dron en manteniment o franja horaria ocupada")
    })
    @Path("/flightplans")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlightPlan(FlightPlan plan) {
        if (plan.getDroneId() == null || plan.getPilotId() == null || plan.getHours() <= 0) {
            return Response.status(400).build();
        }
        try {
            FlightPlan p = this.dm.addFlightPlan(plan.getDroneId(), plan.getPilotId(), plan.getDate(),
                    plan.getHours(), plan.getPositions());
            return Response.status(201).entity(p).build();
        } catch (WrongDateException e) {
            return Response.status(400).build();
        } catch (DroneNotFoundException | PilotNotFoundException e) {
            return Response.status(404).build();
        } catch (DroneInMaintenanceException | FlightPlanOverlapException e) {
            return Response.status(409).build();
        }
    }

}
