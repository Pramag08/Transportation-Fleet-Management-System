# Screenshot Placeholder

To complete the README documentation, please capture the following screenshots:

## Required Screenshots

### 1. race_condition_none.png

- Launch Highway Simulator
- Set Sync Mode to NONE
- Add 5-10 vehicles with fuel
- Run for 60 seconds
- Capture screenshot showing Total Distance < Sum of Individual Mileages

### 2. synchronized_fix.png

- Reset counter
- Set Sync Mode to SYNCHRONIZED
- Run same vehicles for 60 seconds
- Capture screenshot showing Total Distance == Sum of Individual Mileages

### 3. gui_overview.png

- General view of the Highway Simulator window
- Show all panels: Controls, Vehicle Status, Total Distance display
- Capture with multiple vehicles in different states (Running, Paused, Out of Fuel)

## How to Capture Screenshots

1. Run the application: `java -cp bin main.Main`
2. Add vehicles and launch simulator (Option 25)
3. Set up the desired scenario
4. Use Windows Snipping Tool or Print Screen
5. Save images in this directory with the exact filenames listed above
6. Update README.md image paths if needed

## Notes

- Screenshots should be clear and readable
- Include vehicle status panels showing mileage differences
- Highlight the race condition by showing numerical discrepancy
- Ensure sync mode selector is visible in each screenshot
