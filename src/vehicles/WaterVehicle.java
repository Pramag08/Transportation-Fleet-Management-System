package vehicles;

//• Property: private boolean hasSail (affects fuel efficiency).
//• Constructor: Call super, initialize hasSail.
//• Override: estimateJourneyTime(double distance): Add 15% time for currents (base time × 1.15).
public abstract class WaterVehicle extends Vehicle {

    private boolean hasSail;

    public WaterVehicle(String id, String model, double maxSpeed, double currentMileage, boolean hasSail) {
        super(id, model, maxSpeed, currentMileage);
        this.hasSail = hasSail;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double basetime = distance / (getMaxSpeed());
        return basetime * (1.15);
    }

    public boolean getHasSail() {
        return this.hasSail;
    }
}
