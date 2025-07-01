package GUI;

import fileSystem.FileSystem;
import operatingSystem.*;
import javax.swing.*;
import java.awt.*;


public class OperatingSystemGUI {
    private final JFrame frame;
    private final JTextArea output;
    private final FileSystem fs;
    private final DiskManager diskManager;
    private final Scheduler scheduler;
    private final RAMManager ramManager;
    private final CPUManager cpuManager;


    // Constructor
    public OperatingSystemGUI(FileSystem fs, DiskManager diskManager, RAMManager ramManager, CPUManager cpuManager, Scheduler scheduler) {
        this.fs = fs;
        this.diskManager = diskManager;
        this.scheduler = scheduler;
        this.ramManager = ramManager;
        this.cpuManager = cpuManager;
        frame = new JFrame("MiniOS Simulator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Top panel: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton showQueueButton = new JButton("Show Process Queue");
        JButton showSharedResourceButton = new JButton("Show Shared Resource");
        JButton resetSharedResourceButton = new JButton("Reset Shared Resource");

        // Output area
        output = new JTextArea(10, 40);
        output.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(output);

        // Storage graph panel
        JPanel storagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStorageBars(g);
            }
        };
        storagePanel.setPreferredSize(new Dimension(200, 200));
        // Refresh graph every 0.5s
        new Timer(500, e -> storagePanel.repaint()).start();


        showQueueButton.addActionListener(e -> {
            output.setText("");
            String queue = scheduler.getProcessQueue();
            output.append("Processes Queue:\n");
            output.append(queue+"\n");
        });

        showSharedResourceButton.addActionListener(e -> {
            output.setText("");
            output.append("Shared Resource Value: " + cpuManager.getSharedResource());
        });

        resetSharedResourceButton.addActionListener(e -> {
            output.setText("");
            cpuManager.resetSharedResource();
            output.append("Shared resource reset to 0");
        });


        // Layout
        buttonPanel.add(showQueueButton);
        buttonPanel.add(showSharedResourceButton);
        buttonPanel.add(resetSharedResourceButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(storagePanel, BorderLayout.EAST);

        frame.setVisible(true);
    }


    // Methods
    private void drawStorageBars(Graphics g) {
        long diskOccupied = diskManager.getOccupiedSpace(); // Placeholder
        long diskAvailable = diskManager.getTotalSpace() - diskOccupied;
        long ramOccupied = ramManager.getOccupiedSpace(); // Placeholder
        long ramAvailable = ramManager.getTotalSpace() - ramOccupied;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = 150, height = 50;
        int x = 25, yDisk = 20, yRam = 150;

        // Disk bar: Occupied (red) and Available (green)
        g2d.setColor(Color.RED);
        double diskOccupiedRatio = (double) diskOccupied / diskManager.getTotalSpace();
        g2d.fillRect(x, yDisk, (int) (width * diskOccupiedRatio), height);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x + (int) (width * diskOccupiedRatio), yDisk, (int) (width * (1 - diskOccupiedRatio)), height);

        // RAM bar: Occupied (red) and Available (green)
        g2d.setColor(Color.RED);
        double ramOccupiedRatio = (double) ramOccupied / ramManager.getTotalSpace();
        g2d.fillRect(x, yRam, (int) (width * ramOccupiedRatio), height);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x + (int) (width * ramOccupiedRatio), yRam, (int) (width * (1 - ramOccupiedRatio)), height);

        // Draw labels
        g2d.setColor(Color.BLACK);
        g2d.drawString("HDD:", x, yDisk - 5);
        g2d.drawString("Occupied: " + diskOccupied + " bytes", x, yDisk + height + 15);
        g2d.drawString("Available: " + diskAvailable + " bytes", x, yDisk + height + 30);
        g2d.drawString("RAM:", x, yRam - 5);
        g2d.drawString("Occupied: " + ramOccupied + " bytes", x, yRam + height + 15);
        g2d.drawString("Available: " + ramAvailable + " bytes", x, yRam + height + 30);
    }
}