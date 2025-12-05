package interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;

//– void boardPassengers(int count): Boards if ≤ capacity; throws OverloadException.
//– void disembarkPassengers(int count): Disembarks; throws InvalidOperationException if count > current passengers.
//– int getPassengerCapacity(): Returns max capacity.
//– int getCurrentPassengers(): Returns current passengers.

public interface PassengerCarrier {
    void boardPassengers(int count)  throws OverloadException;
    void disembarkPassengers(int count) throws InvalidOperationException;
    int getPassengerCapacity();
    int getCurrentPassengers();
}
