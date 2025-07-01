package GUI;


import fileSystem.*;

import javax.swing.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.*;

import operatingSystem.*;
import operatingSystem.Process;


public class FileSystemGUI {
    private final FileSystem fs;
    private final JFrame frame;
    private final JTextField pathField;
    private final JPanel displayPanel;
    private final DiskManager disk;
    private final Scheduler scheduler;

    public FileSystemGUI(FileSystem fs, DiskManager disk, Scheduler scheduler) {
        this.fs = fs;
        this.disk = disk;
        this.scheduler = scheduler;
        frame = new JFrame("File System Explorer");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton rootButton = new JButton("Root");
        rootButton.addActionListener(e -> goRoot());
        pathField = new JTextField("/");
        JButton refreshBtn = new JButton("List Contents");
        refreshBtn.addActionListener(e -> refreshList());

        topPanel.add(rootButton, BorderLayout.WEST);
        topPanel.add(pathField, BorderLayout.CENTER);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        JPanel createPanel = new JPanel();
        JTextField nameField = new JTextField(10);
        JButton createFileBtn = new JButton("New File");
        JButton createDirBtn = new JButton("New Dir");
        JButton deleteBtn = new JButton("Delete");

        createFileBtn.addActionListener(e -> {
            if (isInvalidName(nameField.getText())) return;
            createNode(nameField.getText(), false);
            nameField.setText("");
        });

        createDirBtn.addActionListener(e -> {
            if (isInvalidName(nameField.getText())) return;
            createNode(nameField.getText(), true);
            nameField.setText("");
        });

        deleteBtn.addActionListener(e -> deleteFile());

        createPanel.add(new JLabel("Name:"));
        createPanel.add(nameField);
        createPanel.add(createFileBtn);
        createPanel.add(createDirBtn);
        createPanel.add(deleteBtn);

        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(displayPanel), BorderLayout.CENTER);
        frame.add(createPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        refreshList();
    }

    private boolean isInvalidName(String name) {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a file name");
            return true;
        }
        if (name.matches(".*[<>:\"/\\\\|?*].*")) {
            JOptionPane.showMessageDialog(frame, "File name cannot contain these characters: \\ / : * ? < > |");
            return true;
        }
        return false;
    }

    private void goRoot() {
        pathField.setText("/");
        refreshList();
    }

    private void refreshList() {
        displayPanel.removeAll();
        try {
            List<FileSystemNode> contents = fs.list(pathField.getText());
            for (FileSystemNode node : contents) {
                String timestamp = Instant.ofEpochMilli(node.getCreationTime()).atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                String buttonText = "<html><table width='100%' cellpadding='2'><tr>" +
                        "<td align='center' style='text: center'>" + node.getName() + (node.isDirectory() ? "/" : ".exe") + "</td>" +
                        "<td colspan='100'></td" +
                        "<td align='center' style='text: center'>" + timestamp + "</td>" +
                        "</tr></table></html>";

                JButton btn = new JButton(buttonText);
                // Set button to expand to full width
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
                if (!node.isDirectory()) {
                    btn.addActionListener(e -> executeFile(node));
                } else {
                    btn.addActionListener(e -> {
                        pathField.setText(pathField.getText().endsWith("/") ? pathField.getText() + node.getName() : pathField.getText() + "/" + node.getName());
                        refreshList();
                    });
                }
                displayPanel.add(btn);
            }
        } catch (Exception ex) {
            displayPanel.add(new JLabel("Error: " + ex.getMessage()));
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void createNode(String name, boolean isDir) {
        try {
            FileSystemNode node = fs.create(name, pathField.getText(), isDir);
            if (!isDir) {
                disk.storeProcess(new Process(node.getId(), node.getSize()));
            }
            refreshList();
        } catch (Exception ex) {
            displayPanel.removeAll();
            displayPanel.add(new JLabel("Error: " + ex.getMessage()));
            displayPanel.revalidate();
            displayPanel.repaint();
        }
    }

    private void executeFile(FileSystemNode fileNode) {
        Process process = disk.getProcessById(fileNode.getId());

        if (process.getState() == Process.State.NEW) {
            scheduler.addProcess(disk.getProcessById(fileNode.getId()));
        }
        scheduler.schedule();
    }

    private void deleteFile() {
        String fileName = JOptionPane.showInputDialog(frame, "Input file/directory name", "Delete from current directory", JOptionPane.PLAIN_MESSAGE);
        try {
            List<FileSystemNode> contents = fs.list(pathField.getText());
            for (FileSystemNode node : contents) {
                if (!fileName.equals(node.getName())) {
                    continue;
                }
                String response = fs.delete(fileName, pathField.getText());
                JOptionPane.showMessageDialog(frame, response);
                disk.removeProcess(node.getId());
                refreshList();
                break;
            }
        } catch (Exception ex) {
            displayPanel.removeAll();
            displayPanel.add(new JLabel("Error: " + ex.getMessage()));
        }
    }
}