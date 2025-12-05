package interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;

//– void loadCargo(double weight): Loads if ≤ capacity; throws OverloadException if exceeded.
//– void unloadCargo(double weight): Unloads; throws InvalidOperationException if weight > current cargo.
//– double getCargoCapacity(): Returns max capacity.
//– double getCurrentCargo(): Returns current cargo.

public interface CargoCarrier {
    void loadCargo(double weight) throws OverloadException;
    void unloadCargo(double weight) throws InvalidOperationException;
    double getCargoCapacity();
    double getCurrentCargo();
}
