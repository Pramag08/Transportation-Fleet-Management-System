package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
//Properties: fuelLevel, private double cargoCapacity (5000 kg), private double currentCargo, maintenanceNeeded.
//– Override move: “Hauling cargo...”, adjust fuel consumption if loaded (> 50%
//capacity reduces efficiency by 10%).
//– calculateFuelEfficiency(): 8.0 km/l (adjusted for cargo).
    private double fuelLevel;
    private double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastMaintenance;

    public Truck(String id, String model, double maxSpeed, double currentMileage, int numWheels) {
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.cargoCapacity = 5000.0;
    }
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) {
            throw new OverloadException("Cargo capacity of " + this.cargoCapacity + " kg exceeded.");
        }
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) {
            throw new InvalidOperationException("Cannot unload more cargo than is on board.");
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
    public void refuel(double amount) throws InvalidOperationException {
        if(amount<=0) {
            throw new InvalidOperationException("Fuel is not sufficient.");
        }else{
            this.fuelLevel += amount;
        }
    }

    @Override
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelConsumed=(distance/calculateFuelEfficiency());
        if(fuelConsumed>this.fuelLevel) {
            throw new InsufficientFuelException("Not enough Fuel to drive");
        }
        else{
            this.fuelLevel -= fuelConsumed;
            return fuelConsumed;
        }
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
        System.out.println("Maintenance performed on Truck ID: "+this.getId());
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if(distance<0){
            throw new InvalidOperationException("Distance cannot be less than zero");
        }
        consumeFuel(distance);
        setMileage(getCurrentMileage() + distance);
        System.out.println("Truck ID: " + getId() + " is hauling cargo for " + distance + " km.");
    }

    @Override
    public double calculateFuelEfficiency() {
        if (this.currentCargo > (this.cargoCapacity * 0.5)) {
            return 8.0 * 0.9;
        }
        return 8.0;
    }
}
