import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

public class GoalsPanel extends JPanel implements Refreshable {
    private List<Goal> goals;
    @SuppressWarnings("unused")
    private DashboardPanel parentPanel;
    private JPanel goalsContainer;
    private DataManager dataManager;
    
    public GoalsPanel(DashboardPanel parentPanel, List<Goal> goals) {
        this.parentPanel = parentPanel;
        this.goals = goals;
        this.dataManager = new DataManager();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(createGoalsPanel());
        scrollPane.setBackground(new Color(15, 15, 25));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(20, 20, 35));
        
        JLabel title = new JLabel("Financial Goals");
        title.setForeground(new Color(100, 200, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton addBtn = new JButton("+ Add Goal");
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addBtn.setBorder(BorderFactory.createEmptyBorder());
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> showAddGoalDialog());
        
        panel.add(title);
        panel.add(Box.createHorizontalGlue());
        panel.add(addBtn);
        
        return panel;
    }
    
    private JPanel createGoalsPanel() {
        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setBackground(new Color(15, 15, 25));
        
        for (Goal goal : goals) {
            goalsContainer.add(createGoalCard(goal));
            goalsContainer.add(Box.createVerticalStrut(15));
        }
        
        if (goals.isEmpty()) {
            JLabel empty = new JLabel("No goals yet. Click 'Add Goal' to create one!");
            empty.setForeground(new Color(150, 150, 170));
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            goalsContainer.add(empty);
        }
        
        goalsContainer.add(Box.createVerticalGlue());
        return goalsContainer;
    }
    
    private JPanel createGoalCard(Goal goal) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(25, 25, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 150, 200), 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JPanel leftPanel = new JPanel(new BorderLayout(10, 5));
        leftPanel.setBackground(new Color(25, 25, 40));
        leftPanel.setPreferredSize(new Dimension(150, 180));
        
        JLabel imageLabel = new JLabel();
        if (goal.getImageBase64() != null && !goal.getImageBase64().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(goal.getImageBase64());
                ImageIcon icon = new ImageIcon(decodedBytes);
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imageLabel.setText("No Image");
                imageLabel.setForeground(new Color(150, 150, 170));
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setForeground(new Color(150, 150, 170));
        }
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(imageLabel, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(25, 25, 40));
        
        JLabel nameLabel = new JLabel(goal.getName());
        nameLabel.setForeground(new Color(100, 200, 255));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(nameLabel);
        
        JLabel amountLabel = new JLabel(String.format("$%.2f / $%.2f", 
                goal.getCurrentAmount(), goal.getTargetAmount()));
        amountLabel.setForeground(new Color(200, 200, 210));
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoPanel.add(amountLabel);
        
        JLabel dateLabel = new JLabel("Target: " + goal.getTargetDate());
        dateLabel.setForeground(new Color(150, 150, 170));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoPanel.add(dateLabel);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) goal.getProgress());
        progressBar.setBackground(new Color(30, 30, 50));
        progressBar.setForeground(goal.isCompleted() ? new Color(76, 175, 80) : new Color(100, 150, 200));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(progressBar);
        
        JLabel progressLabel = new JLabel(String.format("%.1f%% Complete", goal.getProgress()));
        progressLabel.setForeground(new Color(200, 200, 210));
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoPanel.add(progressLabel);
        
        infoPanel.add(Box.createVerticalGlue());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(new Color(25, 25, 40));
        
        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(100, 150, 200));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        editBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> showEditGoalDialog(goal));
        buttonPanel.add(editBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        deleteBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteGoal(goal));
        buttonPanel.add(deleteBtn);
        
        infoPanel.add(buttonPanel);
        
        card.add(leftPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showAddGoalDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Goal", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 15, 25));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel nameLabel = new JLabel("Goal Name:");
        nameLabel.setForeground(new Color(200, 200, 210));
        JTextField nameField = new JTextField(20);
        styleTextField(nameField);
        
        JLabel amountLabel = new JLabel("Target Amount ($):");
        amountLabel.setForeground(new Color(200, 200, 210));
        JSpinner amountSpinner = new JSpinner(new SpinnerNumberModel(1000.0, 1.0, 999999.99, 100.0));
        styleSpinner(amountSpinner);
        
        JLabel dateLabel = new JLabel("Target Date:");
        dateLabel.setForeground(new Color(200, 200, 210));
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        styleSpinner(dateSpinner);
        
        JLabel imageLabel = new JLabel("Goal Image:");
        imageLabel.setForeground(new Color(200, 200, 210));
        JButton imageBtn = new JButton("Choose Image");
        imageBtn.setBackground(new Color(100, 150, 200));
        imageBtn.setForeground(Color.WHITE);
        
        final String[] selectedImage = {null};
        imageBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    selectedImage[0] = Base64.getEncoder().encodeToString(
                        java.nio.file.Files.readAllBytes(chooser.getSelectedFile().toPath()));
                    imageBtn.setText("✓ Image Selected");
                    imageBtn.setBackground(new Color(76, 175, 80));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(imageLabel, gbc);
        gbc.gridx = 1;
        panel.add(imageBtn, gbc);
        
        JButton saveBtn = new JButton("Save Goal");
        saveBtn.setBackground(new Color(76, 175, 80));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a goal name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double amount = (double) amountSpinner.getValue();
            LocalDate date = new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime()).toLocalDate();
            
            Goal newGoal = new Goal(name, amount, date, selectedImage[0]);
            goals.add(newGoal);
            dataManager.saveGoals(goals);
            reloadGoals();
            dialog.dispose();
        });
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showEditGoalDialog(Goal goal) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Goal", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 15, 25));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = new JTextField(goal.getName(), 20);
        styleTextField(nameField);
        
        JSpinner amountSpinner = new JSpinner(new SpinnerNumberModel(goal.getTargetAmount(), 1.0, 999999.99, 100.0));
        styleSpinner(amountSpinner);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setValue(java.sql.Date.valueOf(goal.getTargetDate()));
        styleSpinner(dateSpinner);
        
        JButton imageBtn = new JButton(goal.getImageBase64() != null ? "✓ Change Image" : "Choose Image");
        imageBtn.setBackground(goal.getImageBase64() != null ? new Color(76, 175, 80) : new Color(100, 150, 200));
        imageBtn.setForeground(Color.WHITE);
        
        final String[] selectedImage = {goal.getImageBase64()};
        imageBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    selectedImage[0] = Base64.getEncoder().encodeToString(
                        java.nio.file.Files.readAllBytes(chooser.getSelectedFile().toPath()));
                    imageBtn.setText("✓ Image Updated");
                    imageBtn.setBackground(new Color(76, 175, 80));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Goal Name:");
        nameLabel.setForeground(new Color(200, 200, 210));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel amountLabel = new JLabel("Target Amount ($):");
        amountLabel.setForeground(new Color(200, 200, 210));
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Target Date:");
        dateLabel.setForeground(new Color(200, 200, 210));
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel imageLabel = new JLabel("Goal Image:");
        imageLabel.setForeground(new Color(200, 200, 210));
        panel.add(imageLabel, gbc);
        gbc.gridx = 1;
        panel.add(imageBtn, gbc);
        
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(76, 175, 80));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.addActionListener(e -> {
            goal.setName(nameField.getText());
            goal.setTargetAmount((double) amountSpinner.getValue());
            goal.setTargetDate(new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime()).toLocalDate());
            goal.setImageBase64(selectedImage[0]);
            
            dataManager.saveGoals(goals);
            reloadGoals();
            dialog.dispose();
        });
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteGoal(Goal goal) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this goal?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            goals.remove(goal);
            dataManager.saveGoals(goals);
            reloadGoals();
        }
    }
    
    private void reloadGoals() {
        removeAll();
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createGoalsPanel()), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(25, 25, 40));
        field.setForeground(new Color(200, 200, 210));
        field.setBorder(new LineBorder(new Color(60, 60, 80), 1));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setBackground(new Color(25, 25, 40));
        spinner.setForeground(new Color(200, 200, 210));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        // Reload goals from DataManager instead of using the transaction parameter
        this.goals = dataManager.loadGoals();
        reloadGoals();
    }
}
