Highway Simulator - Multithreading & GUI

## Overview

This project extends the Transportation Fleet Management System with a **Highway Simulator** that demonstrates concurrent programming concepts including multithreading, race conditions, and synchronization mechanisms.

## Key Features

- **Multithreaded Vehicle Simulation**: Each vehicle runs in its own thread, updating mileage and fuel consumption every second
- **Shared Highway Counter**: Tracks total distance travelled by all vehicles
- **Race Condition Demonstration**: Shows data corruption when threads access shared data without synchronization
- **Synchronization Solutions**: Implements both `synchronized` blocks and `ReentrantLock` to fix race conditions
- **Interactive GUI**: Swing-based interface with real-time vehicle status updates, pause/resume controls, and refuel functionality

---

## Compilation & Execution

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Windows/Linux/macOS with terminal access

### Compile the Project

```bash
cd "c:\ROAD TO SDE\iiitd\third sem\Transportation Fleet Management System"
javac -d bin -sourcepath src src/simulation/*.java src/main/Main.java
```

### Run the Application

```bash
java -cp bin main.Main
```

### Launch the Simulator

1. Start the main CLI application
2. Add vehicles to your fleet (Option 1)
3. Select **Option 25: Launch Highway Simulator (GUI)**
4. The GUI window will open with your fleet vehicles

---

## Project Structure

```
src/
├── simulation/
│   ├── VehicleThread.java          # Runnable wrapper for vehicles
│   ├── HighwayCounter.java         # Shared counter with sync modes
│   └── HighwaySimulator.java       # Swing GUI interface
├── vehicles/                        # Vehicle hierarchy (Car, Truck, etc.)
├── fleet/                          # FleetManager for vehicle management
├── interfaces/                     # FuelConsumable, PassengerCarrier, etc.
└── main/
    └── Main.java                   # CLI entry point with menu option 25
```

---

## Design Overview

### 1. VehicleThread (Runnable Implementation)

- **Purpose**: Wraps a `Vehicle` object to run concurrently in its own thread
- **Key Functionality**:
  - Updates vehicle mileage by 1 km every second
  - Consumes fuel proportionally to distance travelled
  - Increments shared `HighwayCounter` distance
  - Supports pause/resume/stop controls via volatile flags
  - Auto-pauses when vehicle runs out of fuel

**Code Highlights**:

```java
@Override
public void run() {
    while (running) {
        // Update vehicle state
        vehicle.setMileage(vehicle.getCurrentMileage() + DISTANCE_PER_UPDATE);
        consumeFuel(DISTANCE_PER_UPDATE);

        // Increment shared counter (potential race condition)
        counter.incrementDistance(DISTANCE_PER_UPDATE);

        Thread.sleep(UPDATE_INTERVAL_MS); // 1 second
    }
}
```

### 2. HighwayCounter (Shared Resource)

- **Purpose**: Maintains total highway distance travelled by all vehicles
- **Synchronization Modes**:
  - `NONE`: No synchronization (demonstrates race condition)
  - `SYNCHRONIZED`: Uses synchronized block for thread-safe access
  - `REENTRANT_LOCK`: Uses `ReentrantLock` for explicit locking

**Race Condition Example**:

```java
// UNSYNCHRONIZED - Multiple threads can corrupt totalDistance
private void incrementUnsynchronized(double distance) {
    double temp = totalDistance;
    // Context switch can occur here!
    totalDistance = temp + distance;
}
```

**Fixed with Synchronization**:

```java
// SYNCHRONIZED - Only one thread can execute at a time
private synchronized void incrementSynchronized(double distance) {
    totalDistance += distance;
}

// REENTRANT_LOCK - Explicit lock/unlock control
private void incrementWithLock(double distance) {
    lock.lock();
    try {
        totalDistance += distance;
    } finally {
        lock.unlock();
    }
}
```

### 3. HighwaySimulator (GUI)

- **Purpose**: Provides visual interface for simulation control and monitoring
- **Components**:
  - **Control Panel**: Start/Pause/Resume/Stop/Reset buttons
  - **Vehicle Panels**: Per-vehicle status (ID, mileage, fuel, status)
  - **Sync Mode Selector**: Dropdown to choose NONE/SYNCHRONIZED/REENTRANT_LOCK
  - **Total Distance Display**: Shows shared counter value in real-time
  - **Refuel Buttons**: Per-vehicle refuel functionality

**Thread Safety**:

- Uses `SwingUtilities.invokeLater()` to ensure GUI updates occur on Event Dispatch Thread (EDT)
- Timer updates UI every 100ms without blocking vehicle threads

---

## Race Condition Demonstration

### What is a Race Condition?

A race condition occurs when multiple threads access shared data concurrently, and at least one thread modifies the data, leading to unpredictable results.

### How to Observe the Race Condition

#### Step 1: Start with NONE Mode

1. Set **Sync Mode** to `NONE`
2. Add 5-10 vehicles with fuel (e.g., Cars, Trucks)
3. Click **Start All**
4. Let the simulation run for 30-60 seconds
5. Click **Stop All**

#### Expected Result (NONE Mode)

The **Total Highway Distance** will be **LESS** than the sum of individual vehicle mileages due to lost updates.

**Example**:

```
Vehicle 1 Mileage: 45.00 km
Vehicle 2 Mileage: 45.00 km
Vehicle 3 Mileage: 45.00 km
Expected Total: 135.00 km
Actual Total: 121.34 km  ❌ (Lost 13.66 km due to race condition)
```

#### Step 2: Fix with SYNCHRONIZED Mode

1. Click **Reset Counter**
2. Set **Sync Mode** to `SYNCHRONIZED`
3. Click **Start All**
4. Let run for same duration
5. Click **Stop All**

#### Expected Result (SYNCHRONIZED Mode)

The **Total Highway Distance** will **MATCH** the sum of individual vehicle mileages.

**Example**:

```
Vehicle 1 Mileage: 90.00 km
Vehicle 2 Mileage: 90.00 km
Vehicle 3 Mileage: 90.00 km
Expected Total: 270.00 km
Actual Total: 270.00 km  ✅ (No lost updates)
```

### Why Does NONE Mode Fail?

**Thread Interleaving Example**:

```
Thread 1 (Vehicle 1):                Thread 2 (Vehicle 2):
1. Read totalDistance = 100
2. Calculate 100 + 1 = 101
                                     3. Read totalDistance = 100 (stale!)
                                     4. Calculate 100 + 1 = 101
3. Write totalDistance = 101
                                     5. Write totalDistance = 101 (overwrites!)

❌ Result: Both increments occurred, but only 1 was recorded (lost update)
```

**With Synchronization**:

```
Thread 1 (Vehicle 1):                Thread 2 (Vehicle 2):
1. Acquire lock
2. Read totalDistance = 100
3. Calculate 100 + 1 = 101
4. Write totalDistance = 101
5. Release lock
                                     6. Acquire lock (waits until lock released)
                                     7. Read totalDistance = 101 (fresh data)
                                     8. Calculate 101 + 1 = 102
                                     9. Write totalDistance = 102
                                     10. Release lock

✅ Result: Both increments recorded correctly
```

---

## Screenshots

### NONE Mode (Race Condition)

![Race Condition Demo]

### SYNCHRONIZED Mode (Fixed)

![Synchronized Fix](screenshots/synchronized_fix.png)
_Note: Total distance (270.00 km) matches sum of individual mileages_

### GUI Overview

![Highway Simulator GUI](screenshots/gui_overview.png)
_Main interface showing vehicle panels, controls, and sync mode selector_

---

## Synchronization Methods Compared

| Feature                 | synchronized                     | ReentrantLock                                         |
| ----------------------- | -------------------------------- | ----------------------------------------------------- |
| **Syntax**              | `synchronized(lock) { ... }`     | `lock.lock(); try { ... } finally { lock.unlock(); }` |
| **Fairness**            | No guarantee                     | Can enable fair mode                                  |
| **Try Lock**            | No                               | Yes (`tryLock()`)                                     |
| **Interruptible**       | No                               | Yes (`lockInterruptibly()`)                           |
| **Condition Variables** | Single (`wait()/notify()`)       | Multiple (`newCondition()`)                           |
| **Performance**         | Slightly faster for simple cases | More flexible for complex scenarios                   |

**When to Use**:

- **synchronized**: Simple mutual exclusion, short critical sections
- **ReentrantLock**: Need fairness, timeouts, multiple conditions, or interruptibility

---

## Key Concepts Demonstrated

### 1. Multithreading

- Each `VehicleThread` runs independently in its own thread
- Threads execute concurrently, simulating real-world parallel vehicle movement
- Thread lifecycle: start → run → pause/resume → stop

### 2. Thread Communication

- **Volatile Flags**: `volatile boolean running/paused` for visibility across threads
- **wait()/notify()**: Pause/resume mechanism using intrinsic locks
- **Thread.interrupt()**: Clean shutdown signal

### 3. Shared State & Synchronization

- **Shared Resource**: `HighwayCounter.totalDistance` accessed by all threads
- **Critical Section**: Code that modifies shared data must be protected
- **Mutual Exclusion**: Only one thread can execute critical section at a time

### 4. GUI Thread Safety (Swing)

- Swing components are **not thread-safe**
- All GUI updates must occur on Event Dispatch Thread (EDT)
- Use `SwingUtilities.invokeLater()` to schedule GUI updates
- Timer runs on EDT to update labels safely

### 5. Defensive Copying

- `FleetManager.getFleetSnapshot()` returns `new ArrayList<>(fleet)`
- Prevents external modification of internal fleet collection
- Simulator works with copy, CLI continues with original

---

## Testing the Simulator

### Test Case 1: Race Condition Visibility

1. Add 10 vehicles (Cars with fuel)
2. Set Sync Mode: NONE
3. Start All → Wait 60 seconds → Stop All
4. Calculate: Sum of individual mileages vs Total Distance
5. **Expected**: Total Distance < Sum (race condition)

### Test Case 2: Synchronization Correctness

1. Reset Counter
2. Set Sync Mode: SYNCHRONIZED
3. Start All → Wait 60 seconds → Stop All
4. **Expected**: Total Distance == Sum (no race)

### Test Case 3: Pause/Resume Individual Vehicles

1. Start simulation
2. Pause Vehicle 1 → Note its mileage
3. Wait 10 seconds
4. Resume Vehicle 1
5. **Expected**: Vehicle 1 mileage unchanged during pause

### Test Case 4: Fuel Depletion

1. Add Car with 5 liters fuel
2. Start simulation
3. **Expected**: Vehicle auto-pauses when fuel reaches 0
4. Click Refuel → Enter 10 liters
5. **Expected**: Vehicle auto-resumes

### Test Case 5: ReentrantLock Mode

1. Reset Counter
2. Set Sync Mode: REENTRANT_LOCK
3. Start All → Wait 60 seconds → Stop All
4. **Expected**: Total Distance == Sum (equivalent to synchronized)

---

MADE BY PRAMAG 2024421
