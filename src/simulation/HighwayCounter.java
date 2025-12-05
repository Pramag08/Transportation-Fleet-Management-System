package simulation;

import java.util.concurrent.locks.ReentrantLock;

/**
 * HighwayCounter maintains a shared counter for total highway distance
 * travelled by all vehicles. Supports both synchronized and unsynchronized
 * modes to demonstrate race conditions.
 */
public class HighwayCounter {

    private double totalDistance = 0.0;
    private boolean useSynchronization = false;
    private final ReentrantLock lock = new ReentrantLock();
    private SyncMode syncMode = SyncMode.NONE;
    private int updateCount = 0;
    private int raceConditionsDetected = 0;

    public enum SyncMode {
        NONE, // No synchronization (demonstrates race condition)
        SYNCHRONIZED, // Using synchronized block
        REENTRANT_LOCK  // Using ReentrantLock
    }

    public HighwayCounter() {
        this(SyncMode.NONE);
    }

    public HighwayCounter(SyncMode mode) {
        this.syncMode = mode;
    }

    /**
     * Increment the total distance travelled
     */
    public void incrementDistance(double distance) {
        switch (syncMode) {
            case SYNCHRONIZED:
                incrementSynchronized(distance);
                break;
            case REENTRANT_LOCK:
                incrementWithLock(distance);
                break;
            case NONE:
            default:
                incrementUnsynchronized(distance);
                break;
        }
    }

    /**
     * Unsynchronized increment (demonstrates race condition)
     */
    private void incrementUnsynchronized(double distance) {
        // RACE CONDITION: Multiple threads can read the same value,
        // increment it, and write back, causing lost updates
        double temp = totalDistance;
        updateCount++;

        // Simulate some processing to increase likelihood of race
        try {
            Thread.sleep(0, 100); // Sleep for 100 nanoseconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        double expected = temp + distance;
        totalDistance = expected;

        // Detect if race condition occurred (another thread modified totalDistance)
        if (Math.abs(totalDistance - expected) > 0.001) {
            raceConditionsDetected++;
            System.err.println("[RACE] Race condition detected! Expected: " + expected + ", Actual: " + totalDistance);
        }
    }

    /**
     * Synchronized increment (fixes race condition)
     */
    private synchronized void incrementSynchronized(double distance) {
        double before = totalDistance;
        totalDistance += distance;
        updateCount++;

        if (updateCount % 10 == 0) { // Log every 10th update
            System.out.println("[SYNC] Synchronized update #" + updateCount + ": " + before + " -> " + totalDistance);
        }
    }

    /**
     * ReentrantLock-based increment (fixes race condition)
     */
    private void incrementWithLock(double distance) {
        lock.lock();
        try {
            double before = totalDistance;
            totalDistance += distance;
            updateCount++;

            if (updateCount % 10 == 0) { // Log every 10th update
                System.out.println("[LOCK] ReentrantLock update #" + updateCount + ": " + before + " -> " + totalDistance);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the current total distance
     */
    public synchronized double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Reset the counter
     */
    public synchronized void reset() {
        totalDistance = 0.0;
        updateCount = 0;
        raceConditionsDetected = 0;
        System.out.println("[INFO] Counter reset - Mode: " + syncMode);
    }

    /**
     * Set synchronization mode
     */
    public void setSyncMode(SyncMode mode) {
        this.syncMode = mode;
        System.out.println("[INFO] Synchronization mode changed to: " + mode);
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    /**
     * Get statistics
     */
    public int getUpdateCount() {
        return updateCount;
    }

    public int getRaceConditionsDetected() {
        return raceConditionsDetected;
    }

    /**
     * Print statistics summary
     */
    public void printStatistics() {
        System.out.println("\n========== Highway Counter Statistics ==========");
        System.out.println("Mode: " + syncMode);
        System.out.println("Total Updates: " + updateCount);
        System.out.println("Total Distance: " + String.format("%.2f km", totalDistance));
        if (syncMode == SyncMode.NONE) {
            System.out.println("Race Conditions Detected: " + raceConditionsDetected);
        }
        System.out.println("================================================\n");
    }
}
