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
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        add(createControlPanel(), BorderLayout.NORTH);
        
        reportArea = new JTextArea();
        reportArea.setBackground(new Color(250, 250, 250));
        reportArea.setForeground(new Color(50, 50, 50));
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);
        
        generateReport();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        JLabel label = new JLabel("Select Month:");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        monthSpinner = new JSpinner(new SpinnerDateModel());
        monthSpinner.setEditor(new JSpinner.DateEditor(monthSpinner, "yyyy-MM"));
        monthSpinner.setValue(new java.util.Date());
        monthSpinner.addChangeListener(e -> generateReport());
        
        panel.add(label);
        panel.add(monthSpinner);
        
        return panel;
    }
    
    private void generateReport() {
        java.util.Date selectedDate = (java.util.Date) monthSpinner.getValue();
        LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        YearMonth selectedMonth = YearMonth.from(date);
        
        StringBuilder report = new StringBuilder();
        report.append("MONTHLY REPORT - ").append(selectedMonth).append("\n");
        report.append("=".repeat(50)).append("\n\n");
        
        double totalIncome = 0, totalExpense = 0;
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
        
        report.append("SUMMARY\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Total Income:     ₱%10.2f\n", totalIncome));
        report.append(String.format("Total Expenses:   ₱%10.2f\n", totalExpense));
        report.append(String.format("Balance:          ₱%10.2f\n\n", totalIncome - totalExpense));
        
        report.append("INCOME BREAKDOWN\n");
        report.append("-".repeat(50)).append("\n");
        if (incomeByCategory.isEmpty()) {
            report.append("No income transactions\n\n");
        } else {
            incomeByCategory.forEach((cat, amt) -> 
                report.append(String.format("%-30s ₱%10.2f\n", cat, amt)));
            report.append("\n");
        }
        
        report.append("EXPENSE BREAKDOWN\n");
        report.append("-".repeat(50)).append("\n");
        if (expenseByCategory.isEmpty()) {
            report.append("No expense transactions\n");
        } else {
            expenseByCategory.forEach((cat, amt) -> 
                report.append(String.format("%-30s ₱%10.2f\n", cat, amt)));
        }
        
        reportArea.setText(report.toString());
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        generateReport();
    }
}
