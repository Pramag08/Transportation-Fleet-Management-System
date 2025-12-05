package fleet;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import vehicles.*;

public class VehicleFactory {

    public static Vehicle createVehicle(String[] data) throws OverloadException, InvalidOperationException {
        if (data == null || data.length < 5) {
            throw new IllegalArgumentException("Not enough fields to create a vehicle");
        }
        String type = data[0];
        String id = data[1];
        String model = data[2];
        double maxSpeed = Double.parseDouble(data[3]);
        double mileage = Double.parseDouble(data[4]);

        switch (type) {
            case "Car": {
                if (data.length < 8) {
                    throw new IllegalArgumentException("Car record requires 8 fields");
                }
                int numWheels = Integer.parseInt(data[5]);
                double fuelLevel = Double.parseDouble(data[6]);
                int currentPassengers = Integer.parseInt(data[7]);

                Car car = new Car(id, model, maxSpeed, mileage, numWheels);
                car.refuel(fuelLevel);
                car.boardPassengers(currentPassengers);
                return car;
            }
            case "Truck": {
                if (data.length < 8) {
                    throw new IllegalArgumentException("Truck record requires 8 fields");
                }
                int numWheels = Integer.parseInt(data[5]);
                double fuelLevel = Double.parseDouble(data[6]);
                double currentCargo = Double.parseDouble(data[7]);

                Truck truck = new Truck(id, model, maxSpeed, mileage, numWheels);
                truck.refuel(fuelLevel);
                truck.loadCargo(currentCargo);
                return truck;
            }
            case "Bus": {
                if (data.length < 9) {
                    throw new IllegalArgumentException("Bus record requires 9 fields");
                }
                int numWheels = Integer.parseInt(data[5]);
                double fuelLevel = Double.parseDouble(data[6]);
                int currentPassengers = Integer.parseInt(data[7]);
                double currentCargo = Double.parseDouble(data[8]);

                Bus bus = new Bus(id, model, maxSpeed, mileage, numWheels);
                bus.refuel(fuelLevel);
                bus.boardPassengers(currentPassengers);
                bus.loadCargo(currentCargo);
                return bus;
            }
            case "Airplane": {
                if (data.length < 9) {
                    throw new IllegalArgumentException("Airplane record requires 9 fields");
                }
                double maxAltitude = Double.parseDouble(data[5]);
                double fuelLevel = Double.parseDouble(data[6]);
                int currentPassengers = Integer.parseInt(data[7]);
                double currentCargo = Double.parseDouble(data[8]);

                Airplane airplane = new Airplane(id, model, maxSpeed, mileage, maxAltitude);
                airplane.refuel(fuelLevel);
                airplane.boardPassengers(currentPassengers);
                airplane.loadCargo(currentCargo);
                return airplane;
            }
            case "CargoShip": {
                if (data.length < 8) {
                    throw new IllegalArgumentException("CargoShip record requires 8 fields");
                }
                boolean hasSail = Boolean.parseBoolean(data[5]);
                double fuelLevel = Double.parseDouble(data[6]);
                double currentCargo = Double.parseDouble(data[7]);

                CargoShip ship = new CargoShip(id, model, maxSpeed, mileage, hasSail);
                if (!hasSail) {
                    ship.refuel(fuelLevel);
                }
                ship.loadCargo(currentCargo);
                return ship;
            }
            default:
                throw new IllegalArgumentException("Unknown vehicle type in file: " + type);
        }
    }
}
