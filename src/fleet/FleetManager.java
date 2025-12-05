package fleet;
//FleetManager Class
//• Properties: private List<Vehicle> fleet (use ArrayList<Vehicle>).

import exceptions.InvalidOperationException;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import vehicles.*;

//• Methods (leverage polymorphism):
//void addVehicle(Vehicle v): Check ID uniqueness; throw InvalidOperationException if duplicate.
//void removeVehicle(String id): Remove by ID; throw InvalidOperationException if not found.
//void startAllJourneys(double distance): Call move(distance) on each; handle exceptions.
//double getTotalFuelConsumption(double distance): Sum consumeFuel(distance) for FuelConsumable vehicles.
//void maintainAll(): Call performMaintenance() if needsMaintenance().
//List<Vehicle> searchByType(Class<?> type): Return vehicles instanceof type (e.g., Car.class, FuelConsumable.class).
//void sortFleetByEfficiency(): Implement Comparable<Vehicle> in Vehicle (compare by calculateFuelEfficiency()), use Collections.sort(fleet).
//String generateReport(): Summary of fleet stats (total vehicles, count by type, average efficiency, total mileage, maintenance status).
//List<Vehicle> getVehiclesNeedingMaintenance(): Filter vehicles where needsMaintenance() is true.
public class FleetManager {

    private List<Vehicle> fleet;
    private Set<String> modelSet;

    // private List<Vehicle> fleet (use ArrayList<Vehicle>)
    public FleetManager() {
        this.fleet = new ArrayList<>();
        this.modelSet = new HashSet<>();
    }

    // void addVehicle(Vehicle v): Check ID uniqueness; throw InvalidOperationException if duplicate.
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle vehicle : fleet) {
            if (vehicle.getId().equals(v.getId())) {
                throw new InvalidOperationException("Vehicle with ID " + v.getId() + " already exists.");
            }
        }
        fleet.add(v);
        if (v.getModel() != null) {
            modelSet.add(v.getModel());
        }
        System.out.println(v.getClass().getSimpleName() + " with ID " + v.getId() + " added to the fleet.");
    }

    //void removeVehicle(String id): Remove by ID; throw InvalidOperationException if not found
    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(vehicle -> vehicle.getId().equals(id));
        if (!removed) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        }
        // rebuild model set
        modelSet.clear();
        for (Vehicle vehicle : fleet) {
            if (vehicle.getModel() != null) {
                modelSet.add(vehicle.getModel());
            }
        }
        System.out.println("Vehicle with ID " + id + " removed.");
    }

    //void startAllJourneys(double distance): Call move(distance) on each; handle exceptions.
    public void startAllJourneys(double distance) {
        System.out.println("\nStarting all journeys of " + distance + " km...");
        for (Vehicle vehicle : fleet) {
            try {
                vehicle.move(distance);
            } catch (Exception e) {
                System.err.println("Could not start journey for " + vehicle.getId() + ": " + e.getMessage());
            }
        }
    }

    //double getTotalFuelConsumption(double distance): Sum consumeFuel(distance) for FuelConsumable vehicles.
    public double getTotalFuelConsumption(double distance) {
        double totalFuelConsumption = 0.0;
        for (Vehicle vehicle : fleet) {
            if (vehicle instanceof FuelConsumable) {
                double efficiency = vehicle.calculateFuelEfficiency();
                if (efficiency > 0) {
                    totalFuelConsumption += (distance / efficiency);
                }
            }
        }
        return totalFuelConsumption;
    }

    //List<Vehicle> getVehiclesNeedingMaintenance(): Filter vehicles where needsMaintenance() is true.
    public List<Vehicle> getVehiclesNeedingMaintenance() {
        return fleet.stream().filter(v -> v instanceof Maintainable && ((Maintainable) v).needsMaintenance()).collect(Collectors.toList());
    }

    //void maintainAll(): Call performMaintenance() if needsMaintenance().
    public void maintainAll() {
        System.out.println("\nPerforming maintenance on vehicles.");
        for (Vehicle vehicle : fleet) {
            if ((vehicle instanceof interfaces.Maintainable)) {
                boolean needsMaintenance = ((Maintainable) vehicle).needsMaintenance();
                if (needsMaintenance) {
                    ((interfaces.Maintainable) vehicle).performMaintenance();
                }
            }
        }

    }

    //List<Vehicle> searchByType(Class<?> type): Return vehicles instanceof type
    public List<Vehicle> searchByType(Class<?> type) {
        return fleet.stream().filter(type::isInstance).collect(Collectors.toList());
    }

    //void sortFleetByEfficiency(): Implement Comparable<Vehicle> in Vehicle (compare by calculateFuelEfficiency()), use Collections.sort(fleet).
    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }

    // Additional utilities:
    // Get distinct vehicle models in the fleet using TreeSet for sorted order
    public Set<String> getDistinctModels() {
        return new TreeSet<>(modelSet);
    }

    public Vehicle getFastestVehicle() {
        return fleet.stream().max(Comparator.comparingDouble(Vehicle::getMaxSpeed)).orElse(null);
    }

    public Vehicle getSlowestVehicle() {
        return fleet.stream().min(Comparator.comparingDouble(Vehicle::getMaxSpeed)).orElse(null);
    }

    public void sortFleetByModel() {
        sortFleetByModel(true);
    }

    /**
     * Sort fleet by model name. If ascending is true, sorts A->Z, otherwise
     * Z->A.
     */
    public void sortFleetByModel(boolean ascending) {
        Comparator<String> modelCmp = Comparator.nullsLast(String::compareTo);
        Comparator<Vehicle> cmp = Comparator.comparing(Vehicle::getModel, modelCmp);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        fleet.sort(cmp);
    }

    public void sortFleetBySpeed() {
        sortFleetBySpeed(false);
    }

    /**
     * Sort fleet by max speed. If ascending is true sorts low->high, otherwise
     * high->low.
     */
    public void sortFleetBySpeed(boolean ascending) {
        Comparator<Vehicle> cmp = Comparator.comparingDouble(Vehicle::getMaxSpeed);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        fleet.sort(cmp);
    }

    public void sortFleetByMileage() {
        // Sort by current mileage descending (highest mileage first)
        sortFleetByMileage(false);
    }

    /**
     * Sort fleet by current mileage. If ascending is true sorts low->high,
     * otherwise high->low.
     */
    public void sortFleetByMileage(boolean ascending) {
        Comparator<Vehicle> cmp = Comparator.comparingDouble(Vehicle::getCurrentMileage);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        fleet.sort(cmp);
    }

    public void sortFleetById() {
        sortFleetById(true);
    }

    /**
     * Sort fleet by ID. If ascending is true sorts A->Z, otherwise Z->A.
     */
    public void sortFleetById(boolean ascending) {
        Comparator<Vehicle> cmp = Comparator.comparing(Vehicle::getId, Comparator.nullsLast(String::compareTo));
        if (!ascending) {
            cmp = cmp.reversed();
        }
        fleet.sort(cmp);
    }

    public double getTotalFuelRemaining() {
        double total = 0.0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                total += ((FuelConsumable) v).getFuelLevel();
            }
        }
        return total;
    }

    public Vehicle getVehicleById(String id) {
        return fleet.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    // Return a snapshot copy of the fleet list for safe external iteration/display
    public List<Vehicle> getFleetSnapshot() {
        return new ArrayList<>(fleet);
    }

    //String generateReport(): Summary of fleet stats (total vehicles, count by type, average efficiency, total mileage, maintenance status).
    public String generateReport() {
        if (fleet.isEmpty()) {
            return "The fleet is Empty.";
        }
        int totalVehicles = 0, totalFuelVehicles = 0;
        Map<String, Integer> countByType = new HashMap<>();
        double totalEfficiency = 0, totalMileage = 0;
        for (Vehicle vehicle : fleet) {
            // increment the mileage by current vehicle current mileage
            totalMileage += vehicle.getCurrentMileage();
            //increment the number of vehicles
            totalVehicles++;
            // increment the class type
            String typeName = vehicle.getClass().getSimpleName();
            countByType.put(typeName, countByType.getOrDefault(typeName, 0) + 1);
            //increment efficiency of fuel
            double efficiency = vehicle.calculateFuelEfficiency();
            if (efficiency > 0) {
                totalEfficiency += efficiency;
                totalFuelVehicles++;
            }
        }
        double averageEfficiency = 0;
        if (totalFuelVehicles > 0) {
            averageEfficiency = totalEfficiency / totalFuelVehicles;
        }

        StringBuilder report = new StringBuilder();
        report.append("--- Fleet Status Report ---\n");
        report.append("=================================\n");
        report.append(String.format("Total Vehicles: %d\n", totalVehicles));
        report.append("Vehicles by Type:\n");
        for (Map.Entry<String, Integer> entry : countByType.entrySet()) {
            report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
        }
        report.append(String.format("Total Fleet Mileage: %.2f km\n", totalMileage));
        report.append(String.format("Distinct Models: %d\n", modelSet.size()));
        report.append(String.format("Total Fuel Remaining: %.2f liters\n", getTotalFuelRemaining()));
        report.append(String.format("Average Fuel Efficiency: %.2f km/l\n", averageEfficiency));
        report.append("Maintenance status:\n");
        for (Vehicle v : fleet) {
            String status;
            if (v instanceof Maintainable) {
                if (((Maintainable) v).needsMaintenance()) {
                    status = "Needs Maintenance";
                } else {
                    status = "Doesn't need Maintenance";
                }
            } else {
                status = "Not Maintainable";
            }
            report.append(String.format("  - %s (%s): %s\n", v.getId(), v.getModel(), status));
        }
        report.append("=================================\n");
        // fastest / slowest summary
        Vehicle fastest = getFastestVehicle();
        Vehicle slowest = getSlowestVehicle();
        if (fastest != null || slowest != null) {
            report.append("--- Speed summary ---\n");
            if (fastest != null) {
                report.append(String.format("Fastest: %s (%s) - %.2f km/h\n", fastest.getId(), fastest.getModel(), fastest.getMaxSpeed()));
            }
            if (slowest != null) {
                report.append(String.format("Slowest: %s (%s) - %.2f km/h\n", slowest.getId(), slowest.getModel(), slowest.getMaxSpeed()));
            }
            report.append("=================================\n");
        }
        return report.toString();
    }

    public void refuelAll(double refuelAmount) throws InvalidOperationException {
        if (refuelAmount <= 0) {
            throw new InvalidOperationException("Refuel amount must be positive.");
        }
        for (Vehicle vehicle : fleet) {
            if (vehicle instanceof FuelConsumable) {
                if (vehicle instanceof CargoShip) {
                    if (((CargoShip) vehicle).getHasSail()) {
                        // skip sail-powered ships
                        continue;
                    }
                }
                try {
                    ((FuelConsumable) vehicle).refuel(refuelAmount);
                } catch (InvalidOperationException e) {
                    // This should not happen because we've validated refuelAmount > 0,
                    // but log to stderr and continue to next vehicle.
                    System.err.println("Failed to refuel vehicle " + vehicle.getId() + ": " + e.getMessage());
                }

            }
        }
        System.out.println("All compatible vehicles refueled.");
    }

    /**
     * Sort fleet by fuel efficiency. If ascending is true sorts low->high,
     * otherwise high->low.
     */
    public void sortFleetByEfficiency(boolean ascending) {
        Comparator<Vehicle> cmp = Comparator.comparingDouble(Vehicle::calculateFuelEfficiency);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        fleet.sort(cmp);
    }
    // PERSISTENCE
    // void saveToFile(String filename): Save fleet to CSV (e.g., “Car,V001,Toyota,120.0,4,50.0,5,0” for a Car).
    //• void loadFromFile(String filename): Load from CSV, recreate vehicles (use factory method for type parsing).
    //• Handle IOExceptions with user-friendly messages.

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Explicit header to document common fields. Extra fields vary by type and
            // are appended after the common columns.
            writer.println("Type,ID,Model,MaxSpeed,Mileage,ExtraFields...");
            for (Vehicle vehicle : fleet) {
                // Common properties (VEHICLE)
                String commonData = String.join(",",
                        vehicle.getClass().getSimpleName(),
                        vehicle.getId(),
                        vehicle.getModel(),
                        String.valueOf(vehicle.getMaxSpeed()),
                        String.valueOf(vehicle.getCurrentMileage())
                );
                String vehicleSpecificData = "";
                if (vehicle instanceof Car car) {
                    // Format: NumWheels,FuelLevel,CurrentPassengers
                    vehicleSpecificData = String.join(",",
                            String.valueOf(car.getNumWheels()),
                            String.valueOf(car.getFuelLevel()),
                            String.valueOf(car.getCurrentPassengers())
                    );
                } else if (vehicle instanceof Truck truck) {
                    // Format: NumWheels,FuelLevel,CurrentCargo
                    vehicleSpecificData = String.join(",",
                            String.valueOf(truck.getNumWheels()),
                            String.valueOf(truck.getFuelLevel()),
                            String.valueOf(truck.getCurrentCargo())
                    );
                } else if (vehicle instanceof Bus bus) {
                    // Format: NumWheels,FuelLevel,CurrentPassengers,CurrentCargo
                    vehicleSpecificData = String.join(",",
                            String.valueOf(bus.getNumWheels()),
                            String.valueOf(bus.getFuelLevel()),
                            String.valueOf(bus.getCurrentPassengers()),
                            String.valueOf(bus.getCurrentCargo())
                    );
                } else if (vehicle instanceof Airplane airplane) {
                    // Format: MaxAltitude,FuelLevel,CurrentPassengers,CurrentCargo
                    vehicleSpecificData = String.join(",",
                            String.valueOf(airplane.getMaxAltitude()),
                            String.valueOf(airplane.getFuelLevel()),
                            String.valueOf(airplane.getCurrentPassengers()),
                            String.valueOf(airplane.getCurrentCargo())
                    );
                } else if (vehicle instanceof CargoShip ship) {
                    // Format: HasSail,FuelLevel,CurrentCargo
                    vehicleSpecificData = String.join(",",
                            String.valueOf(ship.getHasSail()),
                            String.valueOf(ship.getFuelLevel()),
                            String.valueOf(ship.getCurrentCargo())
                    );
                }
                if (vehicleSpecificData.isEmpty()) {
                    writer.println(commonData);
                } else {
                    writer.println(commonData + "," + vehicleSpecificData);
                }
            }
            System.out.println("\nFleet saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving fleet to file: " + e.getMessage());
            throw e;
        }
    }

    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<Vehicle> tempList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                // Skip header if present
                if (trimmed.toLowerCase().startsWith("type,")) {
                    continue;
                }
                String[] data = line.split(",");
                // Trim individual tokens to be robust to whitespace
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim();
                }
                try {
                    Vehicle v = VehicleFactory.createVehicle(data);
                    if (v != null) {
                        tempList.add(v);
                    }
                } catch (Exception e) {
                    System.err.println("Skipping malformed line in " + filename + ": " + line + " (" + e.getMessage() + ")");
                }
            }

            // Replace fleet atomically with parsed vehicles only if parsing completed.
            fleet.clear();
            fleet.addAll(tempList);
            // rebuild modelSet
            modelSet.clear();
            for (Vehicle vehicle : fleet) {
                if (vehicle.getModel() != null) {
                    modelSet.add(vehicle.getModel());
                }
            }

            System.out.println("Fleet loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Load failed: File not found: " + filename);
        } catch (IOException e) {
            System.err.println("Error loading fleet from file: " + e.getMessage());
            throw e;
        }
    }
}
