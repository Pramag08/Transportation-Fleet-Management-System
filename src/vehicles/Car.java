package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;
//– Properties: private double fuelLevel (init 0), private int passengerCapacity (5), private int currentPassengers, private boolean maintenanceNeeded.
//– Override move(double distance): “Driving on road...”, consume fuel, update mileage; check fuel sufficiency.
//– calculateFuelEfficiency(): 15.0 km/l.
//– Implement all interface methods.

public class Car extends LandVehicle implements FuelConsumable , PassengerCarrier, Maintainable {

    private double fuelLevel;
    private int passengerCapacity;
    private int currentPassengers;
    private boolean maintenanceNeeded;
    private double mileageAtLastMaintenance;


    public Car(String id, String model, double maxSpeed, double currentMileage, int numWheels) {
        super(id, model, maxSpeed, currentMileage,numWheels);
        this.fuelLevel = 0;
        this.passengerCapacity = 5;
    }
    // FUEL CONSUMABLE INTERFACE
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
    //MAINTAINABLE INTERFACE
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
        System.out.println("Maintenance performed on Car ID: "+this.getId());
    }
    //PASSENGER CARRIER INTERFACE
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers+count > this.passengerCapacity) {
            throw new OverloadException("Passenger Capacity Exceeded!!");
        }
        else{
            this.currentPassengers += count;
        }
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if(count>this.currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers");
        }
        else{
            this.currentPassengers -= count;
        }
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
    public void move(double distance) throws InvalidOperationException,InsufficientFuelException {
        if(distance<0){
            throw new InvalidOperationException("Distance Can't be Negative");
        }
        else{
            consumeFuel(distance);
            setMileage(getCurrentMileage() + distance);
            System.out.println("CAR with ID: "+this.getId()+" is driving for "+distance+"km.");
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }
}
