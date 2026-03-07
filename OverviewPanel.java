import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class OverviewPanel extends JPanel implements Refreshable {
    private JLabel totalIncomeLabel, totalExpenseLabel, balanceLabel;
    private JTextArea recentTransactionsArea;
    private List<Transaction> transactions;
    
    public OverviewPanel(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        
        add(createMonthPanel());
        add(Box.createVerticalStrut(20));
        add(createStatsPanel());
        add(Box.createVerticalStrut(20));
        add(createHistoryPanel());
        add(Box.createVerticalStrut(20));
    }
    
    private JPanel createMonthPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel label = new JLabel("Current Month: " + YearMonth.now());
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new GridLayout(1, 3, 20, 0));
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        panel.add(createStatCard("Total Income", new Color(46, 204, 113)));
        panel.add(createStatCard("Total Expenses", new Color(231, 76, 60)));
        panel.add(createStatCard("Balance", new Color(52, 152, 219)));
        
        updateStats();
        return panel;
    }
    
    private JPanel createStatCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(new JLabel(title));
        titlePanel.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        JLabel valueLabel = new JLabel("₱0.00");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setBackground(Color.WHITE);
        valuePanel.add(valueLabel);
        valuePanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        if (title.equals("Total Income")) totalIncomeLabel = valueLabel;
        else if (title.equals("Total Expenses")) totalExpenseLabel = valueLabel;
        else balanceLabel = valueLabel;
        
        return card;
    }
    
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(new Color(50, 50, 50));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        recentTransactionsArea = new JTextArea(10, 80);
        recentTransactionsArea.setBackground(new Color(250, 250, 250));
        recentTransactionsArea.setForeground(new Color(50, 50, 50));
        recentTransactionsArea.setEditable(false);
        recentTransactionsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        recentTransactionsArea.setMargin(new Insets(10, 10, 10, 10));
        
        updateRecentTransactions();
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsArea);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateStats() {
        YearMonth currentMonth = YearMonth.now();
        double totalIncome = 0, totalExpense = 0;
        
        for (Transaction t : transactions) {
            if (YearMonth.from(t.getDate()).equals(currentMonth)) {
                if ("Income".equals(t.getType())) totalIncome += t.getAmount();
                else totalExpense += t.getAmount();
            }
        }
        
        double balance = totalIncome - totalExpense;
        totalIncomeLabel.setText(String.format("₱%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("₱%.2f", totalExpense));
        balanceLabel.setText(String.format("₱%.2f", balance));
        balanceLabel.setForeground(balance >= 0 ? new Color(46, 204, 113) : new Color(231, 76, 60));
    }
    
    private void updateRecentTransactions() {
        StringBuilder sb = new StringBuilder();
        transactions.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(10)
                .forEach(t -> sb.append(t.toString()).append("\n"));
        
        if (sb.length() == 0) sb.append("No transactions yet.");
        recentTransactionsArea.setText(sb.toString());
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        updateStats();
        updateRecentTransactions();
    }
}
