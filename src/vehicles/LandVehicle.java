package vehicles;
//• Constructor: Call super, initialize numWheels.
//• Override: estimateJourneyTime(double distance): Add 10% time for traffic (base time × 1.1).
//• Keep move and calculateFuelEfficiency abstract.

public abstract class LandVehicle extends Vehicle {

    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, double currentMileage, int numWheels) {
        super(id, model, maxSpeed, currentMileage);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double basetime = distance / (getMaxSpeed());
        return basetime * (1.1);
    }

    public int getNumWheels() {
        return this.numWheels;
    }
}
