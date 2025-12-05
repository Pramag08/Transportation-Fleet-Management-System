package interfaces;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;

//– void refuel(double amount): Adds fuel;
// throws InvalidOperationException if amount ≤ 0.
//– double getFuelLevel(): Returns current fuel level.
//– double consumeFuel(double distance): Reduces fuel based on efficiency; returns
//consumed amount; throws InsufficientFuelException if not enough fuel.

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
}
