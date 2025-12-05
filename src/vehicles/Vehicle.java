package vehicles;

//Role: Root of the hierarchy.
//• Properties: private String id, private String model, private double maxSpeed, private double currentMileage (tracks total distance traveled).
//• Constructor: Initialize all properties; validate ID (non-empty).
//• Abstract Methods:
//– abstract void move(double distance): Updates mileage, prints type-specific movement; throws InvalidOperationException if distance < 0.
//– abstract double calculateFuelEfficiency(): Returns km per liter (or 0 for non-fuel vehicles).
//– abstract double estimateJourneyTime(double distance): Returns time in hours (distance / maxSpeed, adjusted by type).
//• Concrete Methods:
//– void displayInfo(): Prints all properties in a formatted string.
//– double getCurrentMileage(): Getter for mileage.
//– String getId(): Getter for ID.
import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
// Comparable for FleetManager to be able to sort by fuel efficiency.

public abstract class Vehicle implements Comparable<Vehicle> {

    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed, double currentMileage) {
        if ((id == null) || (id.isEmpty())) {
            throw new IllegalArgumentException("Please enter a valid vehicle ID.");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = currentMileage;
    }

    ;
    public abstract void move(double distance) throws InvalidOperationException, InsufficientFuelException;

    public abstract double calculateFuelEfficiency();

    public abstract double estimateJourneyTime(double distance);

    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Model: " + model);
        System.out.println("Max Speed: " + maxSpeed);
        System.out.println("Current Mileage: " + currentMileage);
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public String getId() {
        return id;
    }

    public int compareTo(Vehicle other) {
        /**
         * Natural ordering for Vehicle: by fuel efficiency.
         *
         * Note: ordering is descending (vehicles with higher fuel efficiency
         * come before lower-efficiency vehicles). This is intentional so that
         * Collections.sort(list) produces a fleet ordered from most to least
         * fuel-efficient.
         *
         * Tie-breakers are not specified here; if deterministic ordering for
         * equal efficiencies is required add a secondary comparison (e.g., ID).
         */
        int primary = Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
        if (primary != 0) {
            return primary;
        }
        // Tie-breaker: fall back to lexicographic ordering by ID to ensure
        // deterministic sort order when two vehicles have equal efficiency.
        if (this.getId() == null && other.getId() == null) {
            return 0;
        }
        if (this.getId() == null) {
            return -1;
        }
        if (other.getId() == null) {
            return 1;
        }
        return this.getId().compareTo(other.getId());
    }

    // getter method created for maxspeed.
    public double getMaxSpeed() {
        return maxSpeed;
    }

    //Function to update mileage
    public void setMileage(double newMileage) {
        this.currentMileage = newMileage;
    }

    // getter method created for model.
    public String getModel() {
        return model;
    }
}
