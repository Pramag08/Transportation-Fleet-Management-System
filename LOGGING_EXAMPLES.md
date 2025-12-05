# Logging Output Examples

## When Starting Simulation

```
========== SIMULATION STARTED ==========
Mode: NONE
Vehicles: 5
=========================================

[INFO] [Vehicle-C001] Started thread
[INFO] [Vehicle-T002] Started thread
[INFO] [Vehicle-B003] Started thread
[INFO] [Vehicle-C004] Started thread
[INFO] [Vehicle-T005] Started thread
```

## During Execution (NONE Mode - Race Condition)

```
[DEBUG] [Vehicle-C001] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 0.00 -> 1.00 km
[DEBUG] [Vehicle-T002] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 1.00 -> 2.00 km
[RACE] Race condition detected! Expected: 3.00, Actual: 2.00
[DEBUG] [Vehicle-B003] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 149.85 L | Counter: 2.00 -> 2.00 km
[DEBUG] [Vehicle-C004] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 2.00 -> 3.00 km
[RACE] Race condition detected! Expected: 4.00, Actual: 3.00
[DEBUG] [Vehicle-T005] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 3.00 -> 3.00 km
```

## During Execution (SYNCHRONIZED Mode)

```
[DEBUG] [Vehicle-C001] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 0.00 -> 1.00 km
[DEBUG] [Vehicle-T002] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 1.00 -> 2.00 km
[DEBUG] [Vehicle-B003] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 149.85 L | Counter: 2.00 -> 3.00 km
[DEBUG] [Vehicle-C004] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 3.00 -> 4.00 km
[DEBUG] [Vehicle-T005] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 4.00 -> 5.00 km
[SYNC] Synchronized update #10: 9.00 -> 10.00
```

## During Execution (REENTRANT_LOCK Mode)

```
[DEBUG] [Vehicle-C001] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 0.00 -> 1.00 km
[DEBUG] [Vehicle-T002] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 1.00 -> 2.00 km
[DEBUG] [Vehicle-B003] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 149.85 L | Counter: 2.00 -> 3.00 km
[DEBUG] [Vehicle-C004] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 99.85 L | Counter: 3.00 -> 4.00 km
[DEBUG] [Vehicle-T005] Travelled 1.0 km | Mileage: 1.00 km | Fuel: 199.85 L | Counter: 4.00 -> 5.00 km
[LOCK] ReentrantLock update #10: 9.00 -> 10.00
```

## When Vehicle Runs Out of Fuel

```
[DEBUG] [Vehicle-C001] Travelled 1.0 km | Mileage: 98.50 km | Fuel: 1.25 L | Counter: 492.50 -> 493.50 km
[DEBUG] [Vehicle-C001] Travelled 1.0 km | Mileage: 99.50 km | Fuel: 0.25 L | Counter: 493.50 -> 494.50 km
[WARN] [Vehicle-C001] Out of fuel - auto-paused
[WARN] [Vehicle-C001] No fuel detected - pausing
```

## When User Pauses/Resumes

```
[INFO] Pausing all vehicles
[INFO] [Vehicle-C001] Paused
[INFO] [Vehicle-T002] Paused
[INFO] [Vehicle-B003] Paused
[INFO] [Vehicle-C004] Paused
[INFO] [Vehicle-T005] Paused

[INFO] Resuming all vehicles
[INFO] [Vehicle-C001] Resumed
[INFO] [Vehicle-T002] Resumed
[INFO] [Vehicle-B003] Resumed
[INFO] [Vehicle-C004] Resumed
[INFO] [Vehicle-T005] Resumed
```

## When Stopping Simulation

```
[INFO] Stopping all vehicles...
[INFO] [Vehicle-C001] Stopped thread
[INFO] [Vehicle-C001] Thread interrupted - exiting gracefully
[INFO] [Vehicle-C001] Thread execution completed
[INFO] [Vehicle-T002] Stopped thread
[INFO] [Vehicle-T002] Thread interrupted - exiting gracefully
[INFO] [Vehicle-T002] Thread execution completed
[INFO] [Vehicle-B003] Stopped thread
[INFO] [Vehicle-B003] Thread interrupted - exiting gracefully
[INFO] [Vehicle-B003] Thread execution completed
[INFO] [Vehicle-C004] Stopped thread
[INFO] [Vehicle-C004] Thread interrupted - exiting gracefully
[INFO] [Vehicle-C004] Thread execution completed
[INFO] [Vehicle-T005] Stopped thread
[INFO] [Vehicle-T005] Thread interrupted - exiting gracefully
[INFO] [Vehicle-T005] Thread execution completed

========== Highway Counter Statistics ==========
Mode: NONE
Total Updates: 247
Total Distance: 234.50 km
Race Conditions Detected: 13
================================================

========== Individual Vehicle Summary ==========
Car (ID: C001) - Mileage: 50.00 km
Truck (ID: T002) - Mileage: 50.00 km
Bus (ID: B003) - Mileage: 50.00 km
Car (ID: C004) - Mileage: 50.00 km
Truck (ID: T005) - Mileage: 47.50 km
===============================================
Sum of Individual Mileages: 247.50 km
Shared Counter Total: 234.50 km
Difference (Lost Updates): 13.00 km
===============================================

========== SIMULATION STOPPED ==========
```

## When Changing Sync Mode

```
[INFO] Synchronization mode changed to: SYNCHRONIZED
[INFO] Counter reset - Mode: SYNCHRONIZED
[INFO] Counter has been reset
```

## Key Log Levels

- **[INFO]**: General informational messages (start, stop, pause, resume)
- **[DEBUG]**: Detailed per-update information (mileage, fuel, counter changes)
- **[WARN]**: Warning messages (fuel depletion, auto-pause events)
- **[ERROR]**: Error messages (unexpected failures)
- **[RACE]**: Race condition detection (only in NONE mode)
- **[SYNC]**: Synchronized mode update tracking (every 10th update)
- **[LOCK]**: ReentrantLock mode update tracking (every 10th update)
