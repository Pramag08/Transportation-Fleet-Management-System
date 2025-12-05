package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    //Properties: fuelLevel, passengerCapacity (50), currentPassengers, cargoCapacity
    //(500 kg), currentCargo, maintenanceNeeded.
    //– Override move: “Transporting passengers and cargo...”.
    //– calculateFuelEfficiency(): 10.0 km/l.
    private double fuelLevel;
    private int passengerCapacity;
    private int currentPassengers;
    private double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastMaintenance;

    public Bus(String id, String model, double maxSpeed, double currentMileage, int numWheels) {
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.passengerCapacity = 50;
        this.cargoCapacity = 500.0;
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
        System.out.println("Maintenance performed on Bus ID: " + getId());
    }

    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers + count > this.passengerCapacity) {
            throw new OverloadException("Passenger capacity of " + this.passengerCapacity + " exceeded.");
        }
        this.currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > this.currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than are on board.");
        }
        this.currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return this.passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return this.currentPassengers;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be negative.");
        }
        consumeFuel(distance);
        setMileage(getCurrentMileage() + distance);
        System.out.println("Bus ID: " + getId() + " is transporting passengers and cargo for " + distance + " km.");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 10.0;
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
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel amount must not be negative.");
        }
        this.fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelConsumed = distance / calculateFuelEfficiency();
        if (fuelConsumed > this.fuelLevel) {
            throw new InsufficientFuelException("Not enough fuel for the journey.");
        }
        this.fuelLevel -= fuelConsumed;
        return fuelConsumed;
    }
}
