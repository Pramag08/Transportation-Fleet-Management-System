package tests;

import fleet.FleetManager;
import vehicles.*;

public class CSVRoundTripTest {

    public static void main(String[] args) {
        try {
            String filename = "smoketest_fleet.csv";
            FleetManager fm = new FleetManager();

            // Create vehicles and set states
            Car car = new Car("C1", "Toyota", 120.0, 1000.0, 4);
            car.refuel(50.0);
            car.boardPassengers(2);
            fm.addVehicle(car);

            Truck truck = new Truck("T1", "Volvo", 90.0, 5000.0, 6);
            truck.refuel(120.0);
            truck.loadCargo(2000.0);
            fm.addVehicle(truck);

            Bus bus = new Bus("B1", "Mercedes", 80.0, 2000.0, 6);
            bus.refuel(80.0);
            bus.boardPassengers(20);
            bus.loadCargo(500.0);
            fm.addVehicle(bus);

            Airplane plane = new Airplane("A1", "Boeing", 900.0, 15000.0, 35000.0);
            plane.refuel(5000.0);
            plane.boardPassengers(100);
            plane.loadCargo(2000.0);
            fm.addVehicle(plane);

            CargoShip shipFueled = new CargoShip("S1", "Maersk", 30.0, 8000.0, false);
            shipFueled.refuel(1000.0);
            shipFueled.loadCargo(10000.0);
            fm.addVehicle(shipFueled);

            CargoShip shipSail = new CargoShip("S2", "OldSail", 15.0, 12000.0, true);
            // sail-powered: do not refuel
            shipSail.loadCargo(5000.0);
            fm.addVehicle(shipSail);

            // Save to CSV
            System.out.println("Saving fleet to: " + filename);
            fm.saveToFile(filename);

            // Load into a fresh FleetManager
            FleetManager fm2 = new FleetManager();
            System.out.println("Loading fleet from: " + filename);
            fm2.loadFromFile(filename);

            // Print report of loaded fleet
            System.out.println("\n--- Report from loaded fleet ---");
            System.out.println(fm2.generateReport());

            System.out.println("CSV round-trip smoke test completed.");
        } catch (Exception e) {
            System.err.println("CSV smoke test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
