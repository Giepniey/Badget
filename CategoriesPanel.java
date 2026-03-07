import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesPanel extends JPanel implements Refreshable {
    private List<Transaction> transactions;
    
    public CategoriesPanel(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        updateCharts();
    }
    
    private void updateCharts() {
        removeAll();
        
        Map<String, Double> income = new HashMap<>();
        Map<String, Double> expense = new HashMap<>();
        
        for (Transaction t : transactions) {
            if ("Income".equals(t.getType())) {
                income.put(t.getCategory(), income.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            } else {
                expense.put(t.getCategory(), expense.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        
        add(createPanel("Income", income, new Color(46, 204, 113)));
        add(createPanel("Expenses", expense, new Color(231, 76, 60)));
        revalidate();
        repaint();
    }
    
    private JPanel createPanel(String title, Map<String, Double> data, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        if (data.isEmpty()) {
            JLabel empty = new JLabel("No transactions");
            empty.setFont(new Font("Arial", Font.ITALIC, 11));
            empty.setForeground(new Color(150, 150, 150));
            contentPanel.add(empty);
        } else {
            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            data.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> contentPanel.add(createItem(e.getKey(), e.getValue(), total, color)));
        }
        
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createItem(String category, double amount, double total, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        double pct = (amount / total) * 100;
        
        JLabel catLabel = new JLabel(category);
        catLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel amtLabel = new JLabel(String.format("₱%.2f (%.1f%%)", amount, pct));
        amtLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        amtLabel.setForeground(color);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(catLabel, BorderLayout.WEST);
        topPanel.add(amtLabel, BorderLayout.EAST);
        
        JPanel barPanel = new JPanel();
        barPanel.setBackground(new Color(240, 240, 240));
        barPanel.setLayout(new BorderLayout());
        barPanel.setPreferredSize(new Dimension(0, 8));
        
        JPanel bar = new JPanel();
        bar.setBackground(color);
        bar.setPreferredSize(new Dimension((int) (200 * pct / 100), 8));
        barPanel.add(bar, BorderLayout.WEST);
        
        item.add(topPanel, BorderLayout.NORTH);
        item.add(barPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        updateCharts();
    }
}
