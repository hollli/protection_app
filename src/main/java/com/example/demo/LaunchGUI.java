package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.*;


public class LaunchGUI extends JFrame {
    private static final String LAUNCH_ATTEMPTS_FILE = "launch_attempts.txt";
    private JLabel dayLabel, monthLabel, yearLabel, launchAttemptsLabel;
    private JTextField dayTextField, monthTextField, yearTextField;
    private JButton changeDateButton;

    public LaunchGUI() {
        super("༼ つ ◕_◕ ༽つ \n Veretilnyk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        dayLabel = new JLabel("Day:");
        monthLabel = new JLabel("Month:");
        yearLabel = new JLabel("Year:");
        launchAttemptsLabel = new JLabel("Launch Attempts Left: " + getLaunchAttempts());

        dayTextField = new JTextField(5);
        monthTextField = new JTextField(5);
        yearTextField = new JTextField(5);

        changeDateButton = new JButton("Change File Creation Date");
        changeDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeFileCreationDate();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(dayLabel, gbc);
        gbc.gridx = 1;
        panel.add(dayTextField, gbc);
        gbc.gridx = 2;
        panel.add(monthLabel, gbc);
        gbc.gridx = 3;
        panel.add(monthTextField, gbc);
        gbc.gridx = 4;
        panel.add(yearLabel, gbc);
        gbc.gridx = 5;
        panel.add(yearTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        panel.add(changeDateButton, gbc);
        gbc.gridy = 2;
        panel.add(launchAttemptsLabel, gbc);

        add(panel);
        setVisible(true);
    }

    private int getLaunchAttempts() {
        try {
            File file = new File(LAUNCH_ATTEMPTS_FILE);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(file)) {
                        writer.print("10");
                    }
                } else {
                    throw new IOException("Failed to create file.");
                }
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return Integer.parseInt(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void setLaunchAttempts(int attempts) {
        try (PrintWriter writer = new PrintWriter(LAUNCH_ATTEMPTS_FILE)) {
            writer.print(attempts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeFileCreationDate() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Path filePath = Paths.get(selectedFile.getPath());

                int day = Integer.parseInt(dayTextField.getText());
                int month = Integer.parseInt(monthTextField.getText());
                int year = Integer.parseInt(yearTextField.getText());

                FileTime newCreationTime = FileTime.from(LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC));
                Files.getFileAttributeView(filePath, BasicFileAttributeView.class).setTimes(newCreationTime, null, null);
                JOptionPane.showMessageDialog(this,
                        "File creation date successfully changed!",
                        "༼ つ ◕_◕ ༽つ\n", JOptionPane.INFORMATION_MESSAGE);

                int attempts = getLaunchAttempts() - 1;
                setLaunchAttempts(attempts);
                launchAttemptsLabel.setText("Launch Attempts Left: " + attempts);

                if (attempts <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "No more launch attempts left. Exiting program.");
                    System.exit(0);
                }
            } catch (NumberFormatException | IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error changing file creation date: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        File attemptsFile = new File(LAUNCH_ATTEMPTS_FILE);
        if (!attemptsFile.exists()) {
            try (PrintWriter writer = new PrintWriter(attemptsFile)) {
                writer.print("10");
            } catch (IOException e) {
                throw new RuntimeException("Error creating attempts file: " + e.getMessage());
            }
        }
        int attempts = 0;
        try {
            attempts = Integer.parseInt(new BufferedReader(new FileReader(new File(LAUNCH_ATTEMPTS_FILE))).readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (attempts <= 0) {
            JOptionPane.showMessageDialog(null, "No more launch attempts left. Exiting program.",
                    "༼ つ ◕_◕ ༽つ\n", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            SwingUtilities.invokeLater(LaunchGUI::new);
        }
    }
}
