# Transportation Fleet Management System — Full Documentation

This document provides comprehensive developer-oriented documentation for the
Transportation Fleet Management System. It covers the project purpose, repository
layout, class and interface summaries, CLI usage, CSV persistence format,
parsing rules, design decisions and extension instructions.

## Contents

1. Project overview
2. Repository layout and purpose of key files
3. Build & run (PowerShell and IDE)
4. Command-Line Interface (menu) and expected behaviors
5. Detailed class & interface reference (brief API for each)
6. Persistence & CSV format (complete spec + examples)
7. VehicleFactory: parsing rules and error handling
8. Sorting, ordering and comparators
9. Tests and the smoke test
10. Extension guide: add a new vehicle type
11. Exceptions and error modes
12. Troubleshooting & common messages
13. File listing (quick reference)

14. Project overview

---

This project models a fleet of vehicles (cars, trucks, buses, airplanes, ships)
and provides operations to add/remove vehicles, start journeys, refuel, maintain,
query, sort, and persist the fleet to/from CSV files. The design favors clear
separation of concerns:

- `vehicles/` contains the model hierarchy and concrete implementations.
- `fleet/` contains fleet management and persistence logic (save/load).
- `main/` contains the CLI entrypoint.
- `tests/` contains small smoke tests and shims (non-JUnit by default).

2. Repository layout and purpose of key files

---

- `src/main/Main.java` — CLI with a menu for interactive operations.
- `src/fleet/FleetManager.java` — Core fleet operations: add/remove/sort/report,
  save/load CSV, query by type, get fastest/slowest vehicles, and aggregate stats.
- `src/fleet/VehicleFactory.java` — Factory to construct `Vehicle` objects from
  CSV tokens.
- `src/vehicles/Vehicle.java` — Abstract base class for all vehicles. Implements
  Comparable<Vehicle> using fuel efficiency (descending).
- `src/vehicles/LandVehicle.java`, `AirVehicle.java`, `WaterVehicle.java` —
  Intermediate abstract classes adding behavior to groups of vehicles.
- `src/vehicles/Car.java`, `Truck.java`, `Bus.java`, `Airplane.java`, `CargoShip.java` —
  Concrete vehicle implementations with fuel, cargo and passenger logic.
- `src/interfaces/` — Interfaces: `FuelConsumable`, `Maintainable`,
  `PassengerCarrier`, `CargoCarrier`.
- `src/exceptions/` — Custom exceptions: `InsufficientFuelException`,
  `InvalidOperationException`, `OverloadException`.
- `src/tests/` — Smoke tests and helper shims (runnable main programs that
  exercise save/load functionality).
- `README.md` and `DOCUMENTATION.md` — Human-facing developer documentation.
- `fleet_sample.csv` — Sample CSV file demonstrating the expected CSV format.

3. Build & run (PowerShell and IDE)

---

PowerShell (from repository root):

```powershell
if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin | Out-Null }
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d bin $files
java -cp bin main.Main                # run interactive CLI
java -cp bin tests.CSVRoundTripTest   # run non-interactive smoke test
```

IDE (IntelliJ):

- Open folder as project. Ensure JDK is configured. Run `main.Main` from the
  IDE run configuration.

4. Command-Line Interface (menu) and expected behaviors

---

When you run `main.Main`, the program displays a menu with options (1..24).
Major options and behaviors:

1. Add Vehicle — prompts for vehicle type and specific fields, creates the
   object, and calls `FleetManager.addVehicle()`.
2. Remove Vehicle — removes by ID, throws if not found.
3. Start Journey — calls `move(distance)` on each vehicle; fuel/maintenance
   checks applied per vehicle.
4. Refuel All — attempts to refuel all `FuelConsumable` vehicles by a given
   amount. Sail-powered `CargoShip` instances are intentionally skipped.
5. Perform Maintenance — calls `performMaintenance()` for vehicles that need it.
6. Generate Report — prints `FleetManager.generateReport()` with aggregates.
7. Save Fleet — writes CSV using `FleetManager.saveToFile()`.
8. Load Fleet — loads CSV using `FleetManager.loadFromFile()`; parsing errors
   skip offending lines and continue.
9. Search by Type — search by class or interface (e.g., Car, FuelConsumable).
10. List Vehicles Needing Maintenance — filtered list.
11. Display Distinct Models — sorted model list (alphabetically).
12. Show Fastest and Slowest Vehicles — by `maxSpeed`.
13. Sort Fleet by Model — sorts and prints the sorted list.
14. Sort Fleet by Speed — sorts and prints the sorted list.
15. Sort Fleet by Fuel Efficiency — uses `Vehicle.compareTo()` and prints.
16. Sort Fleet by Mileage — sorts by mileage and prints the sorted list.
17. Sort Fleet by ID — sorts by vehicle ID and prints the sorted list.
18. Estimate Journey Time for a Vehicle — uses `estimateJourneyTime(distance)`.
19. Show Total Fuel Remaining — sums fuel across the fleet.
20. Load Cargo into Vehicle — prompts for vehicle ID and weight (kg); calls
    `loadCargo(double)` on vehicles implementing `CargoCarrier`.
21. Unload Cargo from Vehicle — prompts for vehicle ID and weight (kg); calls
    `unloadCargo(double)` on `CargoCarrier` vehicles.
22. Board Passengers onto Vehicle — prompts for vehicle ID and passenger
    count; calls `boardPassengers(int)` on `PassengerCarrier` vehicles.
23. Disembark Passengers from Vehicle — prompts for vehicle ID and passenger
    count; calls `disembarkPassengers(int)` on `PassengerCarrier` vehicles.
24. Exit — terminates the CLI (now the final menu option).

Input validation: The CLI uses simple numeric parsing and catches invalid menu
entries. Individual field parsing may throw NumberFormatException and be caught by
outer error handlers; interactive UX is basic but robust for well-formed input.

5. Detailed class & interface reference

---

This is a condensed API summary. For full method lists, open the source files.

5.1 `vehicles.Vehicle` (abstract)

- Fields (private): `id`, `model`, `maxSpeed`, `currentMileage`.
- Key abstract methods:
  - `void move(double distance)` — update mileage and perform fuel checks.
  - `double calculateFuelEfficiency()` — km per liter (or 0 for sail-only).
  - `double estimateJourneyTime(double distance)` — time (hours).
- Implements `Comparable<Vehicle>`: natural ordering is by fuel efficiency,
  descending (higher efficiency sorts first).
- Concrete utility methods: `displayInfo()`, getters for `id`, `model`, `maxSpeed`.

  5.2 `LandVehicle`, `AirVehicle`, `WaterVehicle` (abstract)

- Provide default `estimateJourneyTime` implementations that modify base time
  for traffic/currents/directness (e.g., land adds 10%, water +15%).
- LandVehicle adds `numWheels`; AirVehicle adds `maxAltitude`; WaterVehicle adds
  `hasSail`.

  5.3 Concrete vehicles

- `Car` (LandVehicle) — implements `FuelConsumable`, `PassengerCarrier`, `Maintainable`.
  - Fuel efficiency: 15 km/l.
  - Passenger capacity default: 5.
- `Truck` (LandVehicle) — `FuelConsumable`, `CargoCarrier`, `Maintainable`.
  - Base fuel efficiency: 8 km/l, reduced by 10% when cargo > 50% capacity.
- `Bus` (LandVehicle) — `FuelConsumable`, `PassengerCarrier`, `CargoCarrier`, `Maintainable`.
  - Efficiency: 10 km/l.
- `Airplane` (AirVehicle) — `FuelConsumable`, `PassengerCarrier`, `CargoCarrier`, `Maintainable`.
  - Efficiency: 5 km/l.
- `CargoShip` (WaterVehicle) — `CargoCarrier`, `Maintainable`, `FuelConsumable`.

  - If `hasSail` is true, fuel methods return 0 and `refuel()` throws
    `InvalidOperationException` (sailboats are not refuelable).
  - Efficiency: 4 km/l when fueled; 0 when sail-only.

    5.4 Interfaces

- `FuelConsumable` — `refuel(double)`, `getFuelLevel()`, `consumeFuel(double)`.
- `PassengerCarrier` — `boardPassengers`, `disembarkPassengers`, capacities.
- `CargoCarrier` — `loadCargo`, `unloadCargo`, cargo capacity methods.
- `Maintainable` — `scheduleMaintenance()`, `needsMaintenance()`, `performMaintenance()`.

  5.5 Exceptions

- `InsufficientFuelException` — thrown when a move requires more fuel than available.
- `InvalidOperationException` — thrown for invalid operations (e.g., negative refuel).
- `OverloadException` — thrown when loading passengers/cargo exceeds capacity.

6. Persistence & CSV format (complete spec + examples)

---

6.1 Header

- The writer emits a header line: `Type,ID,Model,MaxSpeed,Mileage,ExtraFields...`.
  This helps human readers. The loader ignores a header line starting with
  `type,` (case-insensitive).

  6.2 Per-type columns

- Car: Car,C1,Toyota,120.0,1000.0,4,50.0,2

  - 4 => numWheels
  - 50.0 => fuelLevel
  - 2 => currentPassengers

- Truck: Truck,T1,Volvo,90.0,5000.0,6,120.0,2000.0

  - 6 => numWheels
  - 120.0 => fuelLevel
  - 2000.0 => currentCargo

- Bus: Bus,B1,Mercedes,80.0,7000.0,6,200.0,30,100.0

  - 6 => numWheels
  - 200.0 => fuelLevel
  - 30 => currentPassengers
  - 100.0 => currentCargo

- Airplane: Airplane,A1,Boeing,900.0,10000.0,35000.0,5000.0,150,2000.0

  - 35000.0 => maxAltitude
  - 5000.0 => fuelLevel
  - 150 => currentPassengers
  - 2000.0 => currentCargo

- CargoShip: CargoShip,S1,Maersk,30.0,2000.0,false,500.0,10000.0

  - false => hasSail
  - 500.0 => fuelLevel (ignored if hasSail==true)
  - 10000.0 => currentCargo

    6.3 Parsing rules

- Lines are split by `,` and tokens are trimmed.
- `VehicleFactory` checks token counts for each type and throws
  `IllegalArgumentException` when insufficient fields are present; `loadFromFile`
  catches exceptions, logs a skip message and continues.
- The loader builds a temporary list first and only commits it (replaces the
  fleet) after the file has been completely parsed successfully (atomic swap).

7. VehicleFactory: parsing rules and error handling

---

- `VehicleFactory.createVehicle(String[] data)` expects at least 5 tokens
  (Type,ID,Model,MaxSpeed,Mileage). For each type it validates the exact number
  of expected tokens; if missing, it throws `IllegalArgumentException`.
- For `CargoShip` where `hasSail` is true, the factory still reads the fuel
  field but conditionally calls `refuel()` only when `hasSail` is `false`.
- `FleetManager.loadFromFile()` catches creation exceptions and logs a message:
  "Skipping malformed line in <file>: <line> (<error>)".

8. Sorting, ordering and comparators

---

- Natural ordering (`Comparable<Vehicle>`) is implemented in `Vehicle` to sort
  by fuel efficiency descending. `FleetManager.sortFleetByEfficiency()` calls
  `Collections.sort(fleet)` to apply that order.
- Additional sort methods:
  - `sortFleetByModel()` — alphabetical ascending (nulls last).
  - `sortFleetBySpeed()` — descending by `maxSpeed`.
- `getFastestVehicle()` and `getSlowestVehicle()` use streams with
  `Comparator.comparingDouble` to return max/min by speed.

9. Tests and the smoke test

---

- The repository includes a smoke test `tests.CSVRoundTripTest` (a runnable
  `main()` program) that demonstrates adding vehicles, saving to CSV, loading
  back, and printing a report. This is the primary automated verification
  included without JUnit.

10. Extension guide: add a new vehicle type

---

1. Create the new vehicle class in `src/vehicles/` extending the appropriate
   base (e.g., `LandVehicle`). Implement interfaces as needed.
2. Implement `move()`, `calculateFuelEfficiency()`, and `estimateJourneyTime()`.
3. Update `VehicleFactory.createVehicle()` to parse your new type and create an
   instance. Add field-count validation and default handling where appropriate.
4. Optionally, add interactive prompts to `Main.addVehicle()` for the new type.
5. Add a CSV example line for the type to `fleet_sample.csv`.

6. Exceptions and error modes

---

- The system uses specific exceptions for expected error modes:
  - `InsufficientFuelException` when fuel is insufficient.
  - `InvalidOperationException` for illegal state changes (negative refuel).
  - `OverloadException` when loading passengers/cargo beyond capacity.
- Loading a file that does not exist prints a clear message and returns without
  crashing. Malformed rows are skipped with a logged message.

12. Troubleshooting & common messages

---

- "Vehicle with ID X already exists." — attempt to add a duplicate ID.
- "File not found: <file>" — load path incorrect or missing file.
- "Skipping malformed line" — CSV row did not match expected format; check
  field counts and numeric formatting.
- "A sailboat cannot be refueled." — you're trying to refuel a sail-powered ship.

13. File listing (quick reference)

---

- src/
  - exceptions/
    - InsufficientFuelException.java
    - InvalidOperationException.java
    - OverloadException.java
  - fleet/
    - FleetManager.java
    - VehicleFactory.java
  - interfaces/
    - CargoCarrier.java
    - FuelConsumable.java
    - Maintainable.java
    - PassengerCarrier.java
  - main/
    - Main.java
  - tests/
    - CSVRoundTripTest.java
    - CSVRoundTripJUnitShim.java
    - FleetManagerJUnitTest.java (non-JUnit main shim)
    - JUnitAdapterRunner.java (empty shim)
    - RunAllSimpleTests.java (empty shim)
  - vehicles/
    - Vehicle.java
    - LandVehicle.java
    - AirVehicle.java
    - WaterVehicle.java
    - Car.java
    - Truck.java
    - Bus.java
    - Airplane.java
    - CargoShip.java

---

End of documentation.
