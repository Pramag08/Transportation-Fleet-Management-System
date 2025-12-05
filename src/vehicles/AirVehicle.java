package vehicles;
//• Constructor: Call super, initialize maxAltitude.
//• Override: estimateJourneyTime(double distance): Reduce 5% time for direct paths (base time × 0.95).

public abstract class AirVehicle extends Vehicle {

    private double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double currentMileage, double maxAltitude) {
        super(id, model, maxSpeed, currentMileage);
        this.maxAltitude = maxAltitude;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double basetime = distance / (getMaxSpeed());
        return basetime * (0.95);
    }

    public double getMaxAltitude() {
        return this.maxAltitude;
    }
}
