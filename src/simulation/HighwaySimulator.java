package simulation;

import vehicles.Vehicle;
import interfaces.FuelConsumable;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HighwaySimulator provides a GUI for simulating concurrent vehicle movement on
 * a shared highway. Demonstrates race conditions and synchronization.
 */
public class HighwaySimulator extends JFrame {

    private List<VehicleThread> vehicleThreads;
    private HighwayCounter counter;
    private Timer uiUpdateTimer;

    // UI Components
    private JPanel vehiclePanelsContainer;
    private List<VehiclePanel> vehiclePanels;
    private JLabel totalDistanceLabel;
    private JLabel syncModeLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;
    private JButton resetButton;
    private JComboBox<HighwayCounter.SyncMode> syncModeCombo;

    private boolean simulationStarted = false;

    public HighwaySimulator(List<Vehicle> vehicles) {
        super("Highway Simulator - Fleet Management");

        // Initialize counter and threads
        counter = new HighwayCounter(HighwayCounter.SyncMode.NONE);
        vehicleThreads = new ArrayList<>();
        vehiclePanels = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            vehicleThreads.add(new VehicleThread(vehicle, counter));
        }

        // Setup UI
        setupUI();

        // Setup UI update timer (refresh every 100ms)
        uiUpdateTimer = new Timer(100, e -> updateUI());
        uiUpdateTimer.start();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Add window listener to cleanup threads
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopAllThreads();
                uiUpdateTimer.stop();
            }
        });
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - Controls
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Center panel - Vehicle status
        vehiclePanelsContainer = new JPanel();
        vehiclePanelsContainer.setLayout(new BoxLayout(vehiclePanelsContainer, BoxLayout.Y_AXIS));
        vehiclePanelsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (VehicleThread vt : vehicleThreads) {
            VehiclePanel panel = new VehiclePanel(vt);
            vehiclePanels.add(panel);
            vehiclePanelsContainer.add(panel);
            vehiclePanelsContainer.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(vehiclePanelsContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Vehicle Status"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Total distance
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));

        startButton = new JButton("Start All");
        startButton.addActionListener(e -> startSimulation());

        pauseButton = new JButton("Pause All");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> pauseAllThreads());

        resumeButton = new JButton("Resume All");
        resumeButton.setEnabled(false);
        resumeButton.addActionListener(e -> resumeAllThreads());

        stopButton = new JButton("Stop All");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopAllThreads());

        resetButton = new JButton("Reset Counter");
        resetButton.addActionListener(e -> resetCounter());

        // Synchronization mode selector
        JLabel syncLabel = new JLabel("Sync Mode:");
        syncModeCombo = new JComboBox<>(HighwayCounter.SyncMode.values());
        syncModeCombo.setSelectedItem(HighwayCounter.SyncMode.NONE);
        syncModeCombo.addActionListener(e -> {
            HighwayCounter.SyncMode mode = (HighwayCounter.SyncMode) syncModeCombo.getSelectedItem();
            counter.setSyncMode(mode);
            updateSyncModeLabel();
        });

        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(resumeButton);
        panel.add(stopButton);
        panel.add(resetButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(syncLabel);
        panel.add(syncModeCombo);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total distance display
        totalDistanceLabel = new JLabel("Total Highway Distance: 0.0 km", SwingConstants.CENTER);
        totalDistanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalDistanceLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Sync mode info
        syncModeLabel = new JLabel("Mode: NONE (Race Condition Demo)", SwingConstants.CENTER);
        syncModeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        syncModeLabel.setForeground(Color.RED);

        panel.add(totalDistanceLabel, BorderLayout.CENTER);
        panel.add(syncModeLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void startSimulation() {
        if (!simulationStarted) {
            System.out.println("\n========== SIMULATION STARTED ==========");
            System.out.println("Mode: " + counter.getSyncMode());
            System.out.println("Vehicles: " + vehicleThreads.size());
            System.out.println("=========================================\n");

            for (VehicleThread vt : vehicleThreads) {
                vt.start();
            }
            simulationStarted = true;
        } else {
            System.out.println("[INFO] Resuming simulation");
            resumeAllThreads();
        }

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(true);
        syncModeCombo.setEnabled(false); // Lock sync mode during simulation
    }

    private void pauseAllThreads() {
        System.out.println("[INFO] Pausing all vehicles");
        for (VehicleThread vt : vehicleThreads) {
            vt.pause();
        }

        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
    }

    private void resumeAllThreads() {
        System.out.println("[INFO] Resuming all vehicles");
        for (VehicleThread vt : vehicleThreads) {
            vt.resume();
        }

        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
    }

    private void stopAllThreads() {
        System.out.println("\n[INFO] Stopping all vehicles...");
        for (VehicleThread vt : vehicleThreads) {
            vt.stop();
        }

        // Print statistics
        counter.printStatistics();

        // Calculate and display individual vs total comparison
        double totalIndividualMileage = 0.0;
        System.out.println("========== Individual Vehicle Summary ==========");
        for (VehicleThread vt : vehicleThreads) {
            Vehicle v = vt.getVehicle();
            double mileage = v.getCurrentMileage();
            totalIndividualMileage += mileage;
            System.out.println(String.format("%s (ID: %s) - Mileage: %.2f km",
                    v.getClass().getSimpleName(), v.getId(), mileage));
        }
        System.out.println("===============================================");
        System.out.println(String.format("Sum of Individual Mileages: %.2f km", totalIndividualMileage));
        System.out.println(String.format("Shared Counter Total: %.2f km", counter.getTotalDistance()));
        double difference = totalIndividualMileage - counter.getTotalDistance();
        System.out.println(String.format("Difference (Lost Updates): %.2f km", difference));
        System.out.println("===============================================\n");

        System.out.println("========== SIMULATION STOPPED ==========\n");

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        syncModeCombo.setEnabled(true);
        simulationStarted = false;
    }

    private void resetCounter() {
        counter.reset();
        System.out.println("[INFO] Counter has been reset");
        updateUI();
    }

    private void updateUI() {
        // Update total distance
        totalDistanceLabel.setText(String.format("Total Highway Distance: %.2f km", counter.getTotalDistance()));

        // Update vehicle panels
        for (VehiclePanel panel : vehiclePanels) {
            panel.update();
        }
    }

    private void updateSyncModeLabel() {
        HighwayCounter.SyncMode mode = counter.getSyncMode();
        switch (mode) {
            case NONE:
                syncModeLabel.setText("Mode: NONE (Race Condition Demo)");
                syncModeLabel.setForeground(Color.RED);
                break;
            case SYNCHRONIZED:
                syncModeLabel.setText("Mode: SYNCHRONIZED (Thread-Safe)");
                syncModeLabel.setForeground(new Color(0, 150, 0));
                break;
            case REENTRANT_LOCK:
                syncModeLabel.setText("Mode: REENTRANT_LOCK (Thread-Safe)");
                syncModeLabel.setForeground(new Color(0, 150, 0));
                break;
        }
    }

    /**
     * Panel displaying status for a single vehicle
     */
    private class VehiclePanel extends JPanel {

        private VehicleThread vehicleThread;
        private JLabel idLabel;
        private JLabel mileageLabel;
        private JLabel fuelLabel;
        private JLabel statusLabel;
        private JButton refuelButton;
        private JButton pauseButton;
        private JButton resumeButton;

        public VehiclePanel(VehicleThread vt) {
            this.vehicleThread = vt;

            setLayout(new BorderLayout(10, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            // Left panel - Vehicle info
            JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            Vehicle v = vt.getVehicle();
            idLabel = new JLabel("ID: " + v.getId() + " | " + v.getClass().getSimpleName());
            idLabel.setFont(new Font("Arial", Font.BOLD, 14));
            mileageLabel = new JLabel("Mileage: 0.0 km");
            fuelLabel = new JLabel("Fuel: N/A");
            statusLabel = new JLabel("Status: Stopped");

            infoPanel.add(idLabel);
            infoPanel.add(mileageLabel);
            infoPanel.add(fuelLabel);
            infoPanel.add(statusLabel);

            // Right panel - Controls
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

            pauseButton = new JButton("Pause");
            pauseButton.setEnabled(false);
            pauseButton.addActionListener(e -> {
                vt.pause();
                pauseButton.setEnabled(false);
                resumeButton.setEnabled(true);
            });

            resumeButton = new JButton("Resume");
            resumeButton.setEnabled(false);
            resumeButton.addActionListener(e -> {
                vt.resume();
                pauseButton.setEnabled(true);
                resumeButton.setEnabled(false);
            });

            refuelButton = new JButton("Refuel");
            refuelButton.addActionListener(e -> refuelVehicle());

            controlPanel.add(pauseButton);
            controlPanel.add(resumeButton);
            controlPanel.add(refuelButton);

            add(infoPanel, BorderLayout.CENTER);
            add(controlPanel, BorderLayout.EAST);
        }

        public void update() {
            Vehicle v = vehicleThread.getVehicle();

            // Update mileage
            mileageLabel.setText(String.format("Mileage: %.2f km", v.getCurrentMileage()));

            // Update fuel
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                fuelLabel.setText(String.format("Fuel: %.2f L", fc.getFuelLevel()));
            } else {
                fuelLabel.setText("Fuel: N/A");
            }

            // Update status
            String status = vehicleThread.getStatus();
            statusLabel.setText("Status: " + status);

            // Update status color
            if (status.equals("Running")) {
                statusLabel.setForeground(new Color(0, 150, 0));
            } else if (status.equals("Out of Fuel")) {
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setForeground(Color.ORANGE);
            }

            // Enable/disable controls based on state
            if (simulationStarted) {
                pauseButton.setEnabled(vehicleThread.isRunning());
                resumeButton.setEnabled(vehicleThread.isPaused());
            }
        }

        private void refuelVehicle() {
            Vehicle v = vehicleThread.getVehicle();
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;

                String input = JOptionPane.showInputDialog(this,
                        "Enter amount to refuel (liters):",
                        "Refuel Vehicle " + v.getId(),
                        JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        double amount = Double.parseDouble(input.trim());
                        if (amount > 0) {
                            fc.refuel(amount);
                            JOptionPane.showMessageDialog(this,
                                    String.format("Refueled %.2f liters", amount),
                                    "Refuel Success",
                                    JOptionPane.INFORMATION_MESSAGE);

                            // Auto-resume if paused due to fuel
                            if (vehicleThread.isPaused() && !vehicleThread.isRunning()) {
                                vehicleThread.resume();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Amount must be positive!",
                                    "Refuel Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Invalid amount entered!",
                                "Refuel Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                                "Refuel failed: " + ex.getMessage(),
                                "Refuel Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "This vehicle doesn't use fuel!",
                        "Refuel Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create sample vehicles for testing
            List<Vehicle> testVehicles = new ArrayList<>();
            // This would be populated from FleetManager in actual usage

            HighwaySimulator simulator = new HighwaySimulator(testVehicles);
            simulator.setVisible(true);
        });
    }
}
