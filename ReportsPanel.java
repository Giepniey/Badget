import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel implements Refreshable {
    private List<Transaction> transactions;
    private JSpinner monthSpinner;
    private JTextArea reportArea;
    
    public ReportsPanel(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        reportArea = new JTextArea();
        reportArea.setBackground(new Color(20, 20, 35));
        reportArea.setForeground(new Color(200, 200, 210));
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        reportArea.setLineWrap(false);
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBackground(new Color(20, 20, 35));
        add(scrollPane, BorderLayout.CENTER);
        
        generateReport();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setBackground(new Color(20, 20, 35));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel label = new JLabel("Select Month:");
        label.setForeground(new Color(200, 200, 210));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        monthSpinner = new JSpinner(new SpinnerDateModel());
        monthSpinner.setEditor(new JSpinner.DateEditor(monthSpinner, "yyyy-MM"));
        monthSpinner.setValue(new java.util.Date());
        monthSpinner.setPreferredSize(new Dimension(120, 30));
        monthSpinner.addChangeListener(e -> generateReport());
        
        JButton exportButton = new JButton("Export Report");
        exportButton.setBackground(new Color(100, 150, 200));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exportButton.setBorder(BorderFactory.createEmptyBorder());
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.addActionListener(e -> exportReport());
        
        panel.add(label);
        panel.add(monthSpinner);
        panel.add(exportButton);
        
        return panel;
    }
    
    private void generateReport() {
        java.util.Date selectedDate = (java.util.Date) monthSpinner.getValue();
        LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        YearMonth selectedMonth = YearMonth.from(date);
        
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════\n");
        report.append(String.format("MONTHLY REPORT - %s\n", selectedMonth));
        report.append("═══════════════════════════════════════════════════════\n\n");
        
        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        
        for (Transaction t : transactions) {
            if (YearMonth.from(t.getDate()).equals(selectedMonth)) {
                if ("Income".equals(t.getType())) {
                    totalIncome += t.getAmount();
                    incomeByCategory.put(t.getCategory(),
                            incomeByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                } else {
                    totalExpense += t.getAmount();
                    expenseByCategory.put(t.getCategory(),
                            expenseByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                }
            }
        }
        
        final double finalTotalExpense = totalExpense;
        
        // Summary
        report.append("SUMMARY\n");
        report.append("───────────────────────────────────────────────────────\n");
        report.append(String.format("Total Income:          $%10.2f\n", totalIncome));
        report.append(String.format("Total Expenses:        $%10.2f\n", totalExpense));
        report.append(String.format("Net Balance:           $%10.2f\n\n", totalIncome - totalExpense));
        
        // Income breakdown
        report.append("INCOME BREAKDOWN\n");
        report.append("───────────────────────────────────────────────────────\n");
        if (incomeByCategory.isEmpty()) {
            report.append("No income transactions this month.\n\n");
        } else {
            incomeByCategory.forEach((category, amount) ->
                    report.append(String.format("%-30s $%10.2f\n", category, amount))
            );
            report.append("\n");
        }
        
        // Expense breakdown
        report.append("EXPENSE BREAKDOWN\n");
        report.append("───────────────────────────────────────────────────────\n");
        if (expenseByCategory.isEmpty()) {
            report.append("No expense transactions this month.\n\n");
        } else {
            expenseByCategory.forEach((category, amount) ->
                    report.append(String.format("%-30s $%10.2f\n", category, amount))
            );
            report.append("\n");
        }
        
        // Expense percentage
        if (totalExpense > 0) {
            report.append("EXPENSE PERCENTAGE BY CATEGORY\n");
            report.append("───────────────────────────────────────────────────────\n");
            expenseByCategory.forEach((category, amount) -> {
                double percentage = (amount / finalTotalExpense) * 100;
                report.append(String.format("%-30s %5.1f%%\n", category, percentage));
            });
        }
        
        reportArea.setText(report.toString());
    }
    
    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report As");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".txt")) {
                    filePath += ".txt";
                }
                java.nio.file.Files.write(java.nio.file.Paths.get(filePath), 
                        reportArea.getText().getBytes());
                JOptionPane.showMessageDialog(this, "Report exported successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        generateReport();
    }
}
