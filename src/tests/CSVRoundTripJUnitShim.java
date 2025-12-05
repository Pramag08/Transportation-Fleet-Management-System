package tests;

import fleet.FleetManager;
import interfaces.FuelConsumable;
import vehicles.*;

public class CSVRoundTripJUnitShim {

    // This shim emulates a JUnit-style smoke test but runs as a normal main program.
    // It returns exit code 0 on success, non-zero on failure and prints diagnostic messages.
    public static void main(String[] args) {
        try {
            FleetManager fm = new FleetManager();

            Car car = new Car("C1", "Toyota", 120.0, 1000.0, 4);
            car.refuel(50.0);
            car.boardPassengers(2);
            fm.addVehicle(car);

            Truck truck = new Truck("T1", "Volvo", 90.0, 5000.0, 6);
            truck.refuel(120.0);
            truck.loadCargo(2000.0);
            fm.addVehicle(truck);

            CargoShip sail = new CargoShip("S1", "OldSail", 15.0, 12000.0, true);
            CargoShip nonSail = new CargoShip("S2", "Maersk", 30.0, 8000.0, false);
            fm.addVehicle(sail);
            fm.addVehicle(nonSail);

            // Save and load
            String filename = "shim_fleet.csv";
            fm.saveToFile(filename);

            FleetManager loader = new FleetManager();
            loader.loadFromFile(filename);

            // Assertions
            assertExists(loader, "C1");
            assertExists(loader, "T1");
            // refuelAll should skip sail-powered ship
            loader.refuelAll(100.0);
            Vehicle s1 = loader.getVehicleById("S1");
            Vehicle s2 = loader.getVehicleById("S2");
            if (s1 == null || s2 == null) {
                System.err.println("Error: ships not found after load");
                System.exit(2);
            }
            double s1Fuel = 0.0;
            if (s1 instanceof FuelConsumable) {
                s1Fuel = ((FuelConsumable) s1).getFuelLevel();
            }
            double s2Fuel = 0.0;
            if (s2 instanceof FuelConsumable) {
                s2Fuel = ((FuelConsumable) s2).getFuelLevel();
            }

            if (s1Fuel != 0.0) {
                System.err.println("Failure: sail-powered ship was refueled (should be 0). fuel=" + s1Fuel);
                System.exit(3);
            }
            if (s2Fuel <= 0.0) {
                System.err.println("Failure: non-sail ship was not refueled properly (fuel=" + s2Fuel + ")");
                System.exit(4);
            }

            System.out.println("All shim assertions passed.");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Shim test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertExists(FleetManager loader, String id) {
        if (loader.getVehicleById(id) == null) {
            System.err.println("Assertion failed: expected vehicle " + id + " to exist after load");
            System.exit(2);
        }
    }
}
