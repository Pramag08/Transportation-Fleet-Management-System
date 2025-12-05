package simulation;

import interfaces.FuelConsumable;
import vehicles.Vehicle;

/**
 * VehicleThread represents a vehicle running concurrently on a simulated
 * highway. Each vehicle updates its own state (mileage, fuel) and contributes
 * to a shared highway distance counter.
 */
public class VehicleThread implements Runnable {

    private final Vehicle vehicle;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private final HighwayCounter counter;
    private Thread thread;

    // Fuel consumption rate: km per liter
    private static final double FUEL_CONSUMPTION_RATE = 0.1; // consumes 0.1L per km
    private static final int UPDATE_INTERVAL_MS = 1000; // 1 second
    private static final double DISTANCE_PER_UPDATE = 1.0; // 1 km per second

    public VehicleThread(Vehicle vehicle, HighwayCounter counter) {
        this.vehicle = vehicle;
        this.counter = counter;
    }

    /**
     * Start the vehicle thread
     */
    public void start() {
        if (thread == null || !thread.isAlive()) {
            running = true;
            paused = false;
            thread = new Thread(this, "Vehicle-" + vehicle.getId());
            thread.start();
            logInfo("Started thread");
        }
    }

    /**
     * Pause the vehicle thread
     */
    public void pause() {
        paused = true;
        logInfo("Paused");
    }

    /**
     * Resume the vehicle thread
     */
    public void resume() {
        synchronized (this) {
            paused = false;
            this.notifyAll();
            logInfo("Resumed");
        }
    }

    /**
     * Stop the vehicle thread
     */
    public void stop() {
        running = false;
        resume(); // Unblock if paused
        if (thread != null) {
            thread.interrupt();
        }
        logInfo("Stopped thread");
    }

    /**
     * Check if vehicle has fuel
     */
    private boolean hasFuel() {
        if (vehicle instanceof FuelConsumable) {
            return ((FuelConsumable) vehicle).getFuelLevel() > 0;
        }
        return true; // Non-fuel vehicles always have "fuel"
    }

    /**
     * Consume fuel for the distance travelled
     */
    private void consumeFuel(double distance) {
        if (vehicle instanceof FuelConsumable) {
            FuelConsumable fuelVehicle = (FuelConsumable) vehicle;
            double fuelNeeded = distance * FUEL_CONSUMPTION_RATE;
            double currentFuel = fuelVehicle.getFuelLevel();

            if (currentFuel >= fuelNeeded) {
                try {
                    fuelVehicle.consumeFuel(distance);
                } catch (Exception e) {
                    // Fuel consumption failed, pause this vehicle
                    paused = true;
                    logWarning("Fuel consumption failed: " + e.getMessage());
                }
            } else {
                // Out of fuel
                paused = true;
                logWarning("Out of fuel - auto-paused");
            }
        }
    }

    /**
     * Main thread execution loop
     */
    @Override
    public void run() {
        while (running) {
            try {
                // Check if paused
                synchronized (this) {
                    while (paused && running) {
                        this.wait();
                    }
                }

                if (!running) {
                    break;
                }

                // Check fuel
                if (!hasFuel()) {
                    if (!paused) {
                        paused = true;
                        logWarning("No fuel detected - pausing");
                    }
                    continue;
                }

                // Update vehicle state
                double distance = DISTANCE_PER_UPDATE;

                // Update mileage
                vehicle.setMileage(vehicle.getCurrentMileage() + distance);

                // Consume fuel
                consumeFuel(distance);

                // Update shared highway counter
                double beforeCounter = counter.getTotalDistance();
                counter.incrementDistance(distance);
                double afterCounter = counter.getTotalDistance();

                logDebug(String.format("Travelled %.1f km | Mileage: %.2f km | Fuel: %.2f L | Counter: %.2f -> %.2f km",
                        distance, vehicle.getCurrentMileage(),
                        (vehicle instanceof FuelConsumable ? ((FuelConsumable) vehicle).getFuelLevel() : 0.0),
                        beforeCounter, afterCounter));

                // Sleep for update interval
                Thread.sleep(UPDATE_INTERVAL_MS);

            } catch (InterruptedException e) {
                // Thread interrupted, exit gracefully
                logInfo("Thread interrupted - exiting gracefully");
                break;
            } catch (Exception e) {
                logError("Unexpected error: " + e.getMessage());
            }
        }
        logInfo("Thread execution completed");
    }

    /**
     * Logging helper methods
     */
    private void logInfo(String message) {
        System.out.println("[INFO] [" + thread.getName() + "] " + message);
    }

    private void logDebug(String message) {
        System.out.println("[DEBUG] [" + thread.getName() + "] " + message);
    }

    private void logWarning(String message) {
        System.out.println("[WARN] [" + thread.getName() + "] " + message);
    }

    private void logError(String message) {
        System.err.println("[ERROR] [" + thread.getName() + "] " + message);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public boolean isRunning() {
        return running && !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public String getStatus() {
        if (!running) {
            return "Stopped";
        }
        if (paused) {
            if (!hasFuel()) {
                return "Out of Fuel";
            }
            return "Paused";
        }
        return "Running";
    }
}
