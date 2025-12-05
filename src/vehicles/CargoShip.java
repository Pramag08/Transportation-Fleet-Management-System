package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    //Properties: cargoCapacity (50000 kg), currentCargo, maintenanceNeeded, fuelLevel (if fueled).
    //– Override move: “Sailing with cargo...”.
    //– calculateFuelEfficiency(): 4.0 km/l if fueled, else 0.
    private double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double fuelLevel;
    private double mileageAtLastMaintenance;
    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail) {
        super(id, model, maxSpeed, currentMileage, hasSail);
        this.cargoCapacity = 50000.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be negative.");
        }
        if (!getHasSail()) {
            consumeFuel(distance);
        }
        setMileage(getCurrentMileage() + distance);
        System.out.println("CargoShip ID: " + getId() + " is sailing with cargo for " + distance + " km.");
    }


    @Override
    public double calculateFuelEfficiency() {
        if (!getHasSail()) {
            return 4.0;
        }
        return 0;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) {
            throw new OverloadException("Attempt to load " + weight + " kg exceeds remaining capacity. " +  "Current: " + this.currentCargo + " kg, " + "Capacity: " + this.cargoCapacity + " kg.");

        }
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) {
            throw new InvalidOperationException("Cannot unload " + weight + " kg. " + "Only " + this.currentCargo + " kg is on board.");
        }
        this.currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return this.cargoCapacity;

    }

    @Override
    public double getCurrentCargo() {
        return this.currentCargo;

    }

    @Override
    public void scheduleMaintenance() {
        this.maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return (getCurrentMileage() - mileageAtLastMaintenance > 10000) || this.maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        this.mileageAtLastMaintenance = getCurrentMileage();
        System.out.println("Maintenance performed on CargoShip ID: " + getId());
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (getHasSail()) {
            throw new InvalidOperationException("A sailboat cannot be refueled.");
        }
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel amount must be positive.");
        }
        this.fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        if (getHasSail()) {
            return 0;
        }
        return this.fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (getHasSail()) {
            return 0;
        }
        double fuelConsumed = distance / calculateFuelEfficiency();
        if (fuelConsumed > this.fuelLevel) {
            throw new InsufficientFuelException("Not enough fuel for the journey.");
        }
        this.fuelLevel -= fuelConsumed;
        return fuelConsumed;
    }
}
