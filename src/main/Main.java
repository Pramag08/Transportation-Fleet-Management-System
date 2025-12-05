package main;

import exceptions.*;
import fleet.FleetManager;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import simulation.HighwaySimulator;
import vehicles.*;

public class Main {

    public static void main(String[] args) throws InvalidOperationException, IOException, OverloadException {
        FleetManager manager = new FleetManager();
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run) {
            printMenu();
            int choice = readInt(scanner, "Enter your choice: ", 1, 25);
            try {
                switch (choice) {
                    case 1:
                        addVehicle(scanner, manager);
                        break;
                    case 2:
                        System.out.print("Enter ID of vehicle to remove: ");
                        String removeId = scanner.nextLine();
                        manager.removeVehicle(removeId);
                        System.out.println("Vehicle removed successfully.");
                        break;

                    case 3:
                        double distance = readDouble(scanner, "Enter distance for the journey: ", 0.0, null, false);
                        manager.startAllJourneys(distance);
                        break;

                    case 4:
                        double refuelAmount = readDouble(scanner, "Enter amount to refuel all vehicles: ", 0.0, null, false);
                        manager.refuelAll(refuelAmount);

                        break;

                    case 5:
                        manager.maintainAll();
                        System.out.println("Maintenance completed for all applicable vehicles.");
                        break;

                    case 6:
                        System.out.println(manager.generateReport());
                        break;

                    case 7:
                        System.out.print("Enter filename to save (e.g., my_fleet.csv): ");
                        String saveFile = scanner.nextLine();
                        manager.saveToFile(saveFile);
                        break;

                    case 8:
                        System.out.print("Enter filename to load(e.g., my_fleet.csv): ");
                        String loadFile = scanner.nextLine().trim();
                        manager.loadFromFile(loadFile);
                        System.out.println("Fleet loaded from " + loadFile);
                        break;

                    case 9:
                        System.out.print("Enter type to search (e.g., Car, Truck, FuelConsumable, CargoCarrier): ");
                        String searchTypeStr = scanner.nextLine();
                        searchByType(searchTypeStr, manager);
                        break;
                    case 10:
                        List<Vehicle> needsMaintainence = manager.getVehiclesNeedingMaintenance();
                        if (needsMaintainence.isEmpty()) {
                            System.out.println("No vehicles currently need maintenance.");
                        } else {
                            System.out.println("--- Vehicles Needing Maintenance ---");
                            for (Vehicle v : needsMaintainence) {
                                v.displayInfo();
                            }
                        }
                        break;

                    case 11:
                        // Display fleet sorted by model (show vehicles in same format as sort)
                        manager.sortFleetByModel();
                        System.out.println("--- Fleet (sorted by model) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                        break;

                    case 12:
                        // Fastest / Slowest
                        Vehicle fastest = manager.getFastestVehicle();
                        Vehicle slowest = manager.getSlowestVehicle();
                        System.out.println("--- Fastest Vehicle ---");
                        if (fastest != null) {
                            fastest.displayInfo();
                        } else {
                            System.out.println("None");
                        }
                        System.out.println("--- Slowest Vehicle ---");
                        if (slowest != null) {
                            slowest.displayInfo();
                        } else {
                            System.out.println("None");
                        }
                        break;

                    case 13: {
                        boolean asc = readOrder(scanner, "Sort by model: Ascending (A) or Descending (D)? ");
                        manager.sortFleetByModel(asc);
                        System.out.println("Fleet sorted by model (" + (asc ? "ascending" : "descending") + ").");
                        System.out.println("--- Fleet (sorted by model) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;

                    case 14: {
                        boolean asc = readOrder(scanner, "Sort by speed: Ascending (A) or Descending (D)? ");
                        manager.sortFleetBySpeed(asc);
                        System.out.println("Fleet sorted by max speed (" + (asc ? "ascending" : "descending") + ").");
                        System.out.println("--- Fleet (sorted by speed) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;

                    case 15: {
                        boolean asc = readOrder(scanner, "Sort by fuel efficiency: Ascending (A) or Descending (D)? ");
                        manager.sortFleetByEfficiency(asc);
                        System.out.println("Fleet sorted by fuel efficiency (" + (asc ? "ascending" : "descending") + ").");
                        System.out.println("--- Fleet (sorted by fuel efficiency) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;
                    case 16: {
                        boolean asc = readOrder(scanner, "Sort by mileage: Ascending (A) or Descending (D)? ");
                        manager.sortFleetByMileage(asc);
                        System.out.println("Fleet sorted by mileage (" + (asc ? "ascending" : "descending") + ").");
                        System.out.println("--- Fleet (sorted by mileage) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;

                    case 17: {
                        boolean asc = readOrder(scanner, "Sort by ID: Ascending (A) or Descending (D)? ");
                        manager.sortFleetById(asc);
                        System.out.println("Fleet sorted by ID (" + (asc ? "ascending" : "descending") + ").");
                        System.out.println("--- Fleet (sorted by ID) ---");
                        for (Vehicle sv : manager.getFleetSnapshot()) {
                            sv.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;
                    case 18:
                        System.out.print("Enter vehicle ID to estimate journey time for: ");
                        String vid = scanner.nextLine().trim();
                        double dist = readDouble(scanner, "Enter distance (km): ", 0.0, null, false);
                        Vehicle v = manager.getVehicleById(vid);
                        if (v == null) {
                            System.out.println("Vehicle with ID " + vid + " not found.");
                        } else {
                            double eta = v.estimateJourneyTime(dist);
                            System.out.printf("Estimated journey time for Vehicle ID: %s over %.2f km: %.2f hours\n", vid, dist, eta);
                        }
                        break;

                    case 19:
                        double totalFuel = manager.getTotalFuelRemaining();
                        System.out.printf("Total fuel remaining across fleet: %.2f liters\n", totalFuel);
                        break;

                    case 20: {
                        System.out.print("Enter vehicle ID to load cargo into: ");
                        String loadId = scanner.nextLine().trim();
                        Vehicle lv = manager.getVehicleById(loadId);
                        if (lv == null) {
                            System.out.println("Vehicle with ID " + loadId + " not found.");
                            break;
                        }
                        if (!(lv instanceof CargoCarrier)) {
                            System.out.println("Vehicle with ID " + loadId + " cannot carry cargo.");
                            break;
                        }
                        double loadWeight = readDouble(scanner, "Enter weight to load (kg): ", 0.0, null, true);
                        try {
                            ((CargoCarrier) lv).loadCargo(loadWeight);
                            System.out.println("Loaded " + loadWeight + " kg into vehicle " + loadId + ".");
                        } catch (OverloadException e) {
                            System.err.println("Load failed: " + e.getMessage());
                        }
                    }
                    break;

                    case 21: {
                        System.out.print("Enter vehicle ID to unload cargo from: ");
                        String unloadId = scanner.nextLine().trim();
                        Vehicle uv = manager.getVehicleById(unloadId);
                        if (uv == null) {
                            System.out.println("Vehicle with ID " + unloadId + " not found.");
                            break;
                        }
                        if (!(uv instanceof CargoCarrier)) {
                            System.out.println("Vehicle with ID " + unloadId + " cannot carry cargo.");
                            break;
                        }
                        double unloadWeight = readDouble(scanner, "Enter weight to unload (kg): ", 0.0, null, true);
                        try {
                            ((CargoCarrier) uv).unloadCargo(unloadWeight);
                            System.out.println("Unloaded " + unloadWeight + " kg from vehicle " + unloadId + ".");
                        } catch (InvalidOperationException e) {
                            System.err.println("Unload failed: " + e.getMessage());
                        }
                    }
                    break;

                    case 22: {
                        System.out.print("Enter vehicle ID to board passengers onto: ");
                        String boardId = scanner.nextLine().trim();
                        Vehicle bv = manager.getVehicleById(boardId);
                        if (bv == null) {
                            System.out.println("Vehicle with ID " + boardId + " not found.");
                            break;
                        }
                        if (!(bv instanceof PassengerCarrier)) {
                            System.out.println("Vehicle with ID " + boardId + " does not carry passengers.");
                            break;
                        }
                        int boardCount = readInt(scanner, "Enter number of passengers to board: ", 0, Integer.MAX_VALUE);
                        try {
                            ((PassengerCarrier) bv).boardPassengers(boardCount);
                            System.out.println("Boarded " + boardCount + " passengers onto vehicle " + boardId + ".");
                        } catch (OverloadException e) {
                            System.err.println("Boarding failed: " + e.getMessage());
                        }
                    }
                    break;

                    case 23: {
                        System.out.print("Enter vehicle ID to disembark passengers from: ");
                        String disembarkId = scanner.nextLine().trim();
                        Vehicle dv = manager.getVehicleById(disembarkId);
                        if (dv == null) {
                            System.out.println("Vehicle with ID " + disembarkId + " not found.");
                            break;
                        }
                        if (!(dv instanceof PassengerCarrier)) {
                            System.out.println("Vehicle with ID " + disembarkId + " does not carry passengers.");
                            break;
                        }
                        int disembarkCount = readInt(scanner, "Enter number of passengers to disembark: ", 0, Integer.MAX_VALUE);
                        try {
                            ((PassengerCarrier) dv).disembarkPassengers(disembarkCount);
                            System.out.println("Disembarked " + disembarkCount + " passengers from vehicle " + disembarkId + ".");
                        } catch (InvalidOperationException e) {
                            System.err.println("Disembark failed: " + e.getMessage());
                        }
                    }
                    break;

                    case 24:
                        run = false;
                        break;

                    case 25:
                        // Launch GUI Highway Simulator
                        launchHighwaySimulator(manager);
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 25.");
                }
            } catch (InvalidOperationException | OverloadException | IOException e) {
                System.err.println("Operation Failed: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void addVehicle(Scanner scanner, FleetManager manager) throws InvalidOperationException, OverloadException {
        System.out.print("Enter vehicle type (Car, Truck, Bus, Airplane, CargoShip): ");
        String type = scanner.nextLine().trim();
        System.out.print("Enter ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine().trim();
        double maxSpeed = readDouble(scanner, "Enter Max Speed: ", 0.0, null, true);
        double currentMileage = readDouble(scanner, "Enter Current Mileage: ", 0.0, null, true);

        switch (type.toLowerCase()) {
            case "car":
                int carWheels = readInt(scanner, "Enter number of wheels: ", 0, Integer.MAX_VALUE);
                double fuelLevel = readDouble(scanner, "Enter fuelLevel for the journey: ", 0.0, null, true);
                int carCurrentPassengers = readInt(scanner, "Enter currentPassengers in Car: ", 0, Integer.MAX_VALUE);
                Car car = new Car(id, model, maxSpeed, currentMileage, carWheels);
                car.refuel(fuelLevel);
                car.boardPassengers(carCurrentPassengers);
                manager.addVehicle(car);
                break;

            case "truck":
                int truckWheels = readInt(scanner, "Enter number of wheels: ", 0, Integer.MAX_VALUE);
                double truckFuelLevel = readDouble(scanner, "Enter fuelLevel for the journey: ", 0.0, null, true);
                double truckCurrentCargo = readDouble(scanner, "Enter currentCargo in Truck: ", 0.0, null, true);

                Truck truck = new Truck(id, model, maxSpeed, currentMileage, truckWheels);
                truck.refuel(truckFuelLevel);
                truck.loadCargo(truckCurrentCargo);
                manager.addVehicle(truck);
                break;

            case "bus":
                int busWheels = readInt(scanner, "Enter number of wheels: ", 0, Integer.MAX_VALUE);
                double busFuelLevel = readDouble(scanner, "Enter fuelLevel for the journey: ", 0.0, null, true);
                int busCurrentPassengers = readInt(scanner, "Enter currentPassengers in Bus: ", 0, Integer.MAX_VALUE);
                double busCurrentCargo = readDouble(scanner, "Enter currentCargo in Bus: ", 0.0, null, true);

                Bus bus = new Bus(id, model, maxSpeed, currentMileage, busWheels);
                bus.refuel(busFuelLevel);
                bus.boardPassengers(busCurrentPassengers);
                bus.loadCargo(busCurrentCargo);
                manager.addVehicle(bus);
                break;

            case "airplane":
                double maxAltitude = readDouble(scanner, "Enter Max Altitude: ", 0.0, null, true);
                double airplaneFuelLevel = readDouble(scanner, "Enter fuelLevel for the journey: ", 0.0, null, true);
                int airplaneCurrentPassengers = readInt(scanner, "Enter currentPassengers in Airplane: ", 0, Integer.MAX_VALUE);
                double airplaneCurrentCargo = readDouble(scanner, "Enter currentCargo in Airplane: ", 0.0, null, true);

                Airplane airplane = new Airplane(id, model, maxSpeed, currentMileage, maxAltitude);
                airplane.refuel(airplaneFuelLevel);
                airplane.boardPassengers(airplaneCurrentPassengers);
                airplane.loadCargo(airplaneCurrentCargo);
                manager.addVehicle(airplane);
                break;
            case "cargoship":
                boolean hasSail = readBoolean(scanner, "Does it have a sail? (y/n): ");
                double cargoshipFuelLevel = 0.0;
                if (!hasSail) {
                    cargoshipFuelLevel = readDouble(scanner, "Enter fuelLevel for the journey: ", 0.0, null, true);
                } else {
                    System.out.println("Note: This is a sail-powered ship; fuel will be ignored.");
                }
                double cargoshipCurrentCargo = readDouble(scanner, "Enter currentCargo in cargoship: ", 0.0, null, true);

                CargoShip cargoship = new CargoShip(id, model, maxSpeed, currentMileage, hasSail);
                if (!hasSail) {
                    cargoship.refuel(cargoshipFuelLevel);
                }
                cargoship.loadCargo(cargoshipCurrentCargo);
                manager.addVehicle(cargoship);
                break;
            default:
                throw new InvalidOperationException("Invalid vehicle type entered.");
        }
    }

    private static void searchByType(String searchTypeStr, FleetManager manager) {
        Class<?> searchType;
        switch (searchTypeStr.toLowerCase()) {
            case "car":
                searchType = Car.class;
                break;
            case "truck":
                searchType = Truck.class;
                break;
            case "bus":
                searchType = Bus.class;
                break;
            case "airplane":
                searchType = Airplane.class;
                break;
            case "cargoship":
                searchType = CargoShip.class;
                break;
            case "landvehicle":
                searchType = LandVehicle.class;
                break;
            case "airvehicle":
                searchType = AirVehicle.class;
                break;
            case "watervehicle":
                searchType = WaterVehicle.class;
                break;
            case "fuelconsumable":
                searchType = FuelConsumable.class;
                break;
            case "cargocarrier":
                searchType = CargoCarrier.class;
                break;
            case "passengercarrier":
                searchType = PassengerCarrier.class;
                break;
            case "maintainable":
                searchType = Maintainable.class;
                break;
            default:
                System.out.println("Unknown or unsupported type for searching.");
                return;
        }
        List<Vehicle> results = manager.searchByType(searchType);
        if (results.isEmpty()) {
            System.out.println("No vehicles found of type " + searchType.getSimpleName());
        } else {
            System.out.println("--- Search Results for " + searchType.getSimpleName() + " ---");
            for (Vehicle v : results) {
                v.displayInfo();
                System.out.println("--------------------");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Fleet Management System Menu ---");
        System.out.println("=================================\n");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey");
        System.out.println("4. Refuel All");
        System.out.println("5. Perform Maintenance");
        System.out.println("6. Generate Report");
        System.out.println("7. Save Fleet");
        System.out.println("8. Load Fleet");
        System.out.println("9. Search by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Display Distinct Models");
        System.out.println("12. Show Fastest and Slowest Vehicles");
        System.out.println("13. Sort Fleet by Model");
        System.out.println("14. Sort Fleet by Speed");
        System.out.println("15. Sort Fleet by Fuel Efficiency");
        System.out.println("16. Sort Fleet by Mileage");
        System.out.println("17. Sort Fleet by ID");
        System.out.println("18. Estimate Journey Time for a Vehicle");
        System.out.println("19. Show Total Fuel Remaining");
        System.out.println("20. Load Cargo into Vehicle");
        System.out.println("21. Unload Cargo from Vehicle");
        System.out.println("22. Board Passengers onto Vehicle");
        System.out.println("23. Disembark Passengers from Vehicle");
        System.out.println("24. Exit");
        System.out.println("25. Launch Highway Simulator (GUI)");
        System.out.println("=================================\n");
    }

    // Helper methods for robust user input prompting. They re-prompt until
    // valid input is provided, preventing NumberFormatExceptions from
    // bubbling up and aborting the CLI flow.
    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("Please enter a number between %d and %d.\n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer, please try again.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt, Double min, Double max, boolean minInclusive) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (min != null) {
                    if (minInclusive) {
                        if (value < min) {
                            System.out.printf("Please enter a number >= %.2f.\n", min);
                            continue;
                        }
                    } else {
                        if (value <= min) {
                            System.out.printf("Please enter a number > %.2f.\n", min);
                            continue;
                        }
                    }
                }
                if (max != null && value > max) {
                    System.out.printf("Please enter a number <= %.2f.\n", max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please try again.");
            }
        }
    }

    private static boolean readBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes") || line.equals("true") || line.equals("t")) {
                return true;
            }
            if (line.equals("n") || line.equals("no") || line.equals("false") || line.equals("f")) {
                return false;
            }
            System.out.println("Please answer 'y' or 'n'.");
        }
    }

    private static boolean readOrder(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.isEmpty()) {
                continue;
            }
            char c = line.charAt(0);
            if (c == 'a') {
                return true;
            }
            if (c == 'd') {
                return false;
            }
            System.out.println("Please enter 'A' for ascending or 'D' for descending.");
        }
    }

    /**
     * Launch the Highway Simulator GUI with current fleet vehicles
     */
    private static void launchHighwaySimulator(FleetManager manager) {
        List<Vehicle> vehicles = manager.getFleetSnapshot();

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles in fleet. Please add vehicles before launching simulator.");
            return;
        }

        System.out.println("Launching Highway Simulator...");
        System.out.println("Note: The simulator demonstrates race conditions and synchronization.");
        System.out.println("Select 'NONE' mode to see race condition, 'SYNCHRONIZED' or 'REENTRANT_LOCK' to fix it.\n");

        SwingUtilities.invokeLater(() -> {
            HighwaySimulator simulator = new HighwaySimulator(vehicles);
            simulator.setVisible(true);
        });
    }
}
