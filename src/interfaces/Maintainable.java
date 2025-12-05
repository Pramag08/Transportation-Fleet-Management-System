package interfaces;

//– void scheduleMaintenance(): Sets maintenance flag.
//– boolean needsMaintenance(): True if mileage > 10000 km.
//– void performMaintenance(): Resets flag, prints message.

public interface Maintainable {
    void scheduleMaintenance();
    boolean needsMaintenance();
    void performMaintenance();
}
