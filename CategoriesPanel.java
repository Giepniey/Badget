import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesPanel extends JPanel implements Refreshable {
    private List<Transaction> transactions;
    private JPanel chartPanel;
    
    public CategoriesPanel(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartPanel.setBackground(new Color(15, 15, 25));
        
        updateCharts();
        add(chartPanel, BorderLayout.CENTER);
    }
    
    private void updateCharts() {
        chartPanel.removeAll();
        
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        
        for (Transaction t : transactions) {
            if ("Income".equals(t.getType())) {
                incomeByCategory.put(t.getCategory(),
                        incomeByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            } else {
                expenseByCategory.put(t.getCategory(),
                        expenseByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        
        chartPanel.add(createCategoryPanel("INCOME BY CATEGORY", incomeByCategory, new Color(76, 175, 80)));
        chartPanel.add(createCategoryPanel("EXPENSES BY CATEGORY", expenseByCategory, new Color(244, 67, 54)));
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private JPanel createCategoryPanel(String title, Map<String, Double> categories, Color color) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(20, 20, 35));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(20, 20, 35));
        
        if (categories.isEmpty()) {
            JLabel noDataLabel = new JLabel("No transactions in this category");
            noDataLabel.setForeground(new Color(150, 150, 170));
            noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            contentPanel.add(noDataLabel);
        } else {
            double total = categories.values().stream().mapToDouble(Double::doubleValue).sum();
            
            categories.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .forEach(entry -> {
                        double percentage = (entry.getValue() / total) * 100;
                        JPanel barPanel = createBarItem(entry.getKey(), entry.getValue(), percentage, color);
                        contentPanel.add(barPanel);
                        contentPanel.add(Box.createVerticalStrut(10));
                    });
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(20, 20, 35));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBarItem(String category, double amount, double percentage, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(20, 20, 35));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setForeground(new Color(200, 200, 210));
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel amountLabel = new JLabel(String.format("$%.2f (%.1f%%)", amount, percentage));
        amountLabel.setForeground(color);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JPanel barBackground = new JPanel();
        barBackground.setBackground(new Color(30, 30, 50));
        barBackground.setLayout(new BorderLayout());
        barBackground.setBorder(new LineBorder(new Color(50, 50, 70), 1));
        
        JPanel barFill = new JPanel();
        barFill.setBackground(color);
        barBackground.add(barFill, BorderLayout.WEST);
        barFill.setPreferredSize(new Dimension((int) (200 * percentage / 100), 20));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 20, 35));
        topPanel.add(categoryLabel, BorderLayout.WEST);
        topPanel.add(amountLabel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(barBackground, BorderLayout.CENTER);
        
        return panel;
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        updateCharts();
    }
}
