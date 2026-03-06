import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class OverviewPanel extends JPanel implements Refreshable {
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel balanceLabel;
    private JLabel monthLabel;
    private JTextArea recentTransactionsArea; // Store reference to avoid fragile lookups
    private List<Transaction> transactions;
    
    public OverviewPanel(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(15, 15, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Month selector
        JPanel monthPanel = createMonthPanel();
        add(monthPanel, BorderLayout.NORTH);
        
        // Statistics cards
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);
        
        // Recent transactions
        JPanel recentPanel = createRecentTransactionsPanel();
        add(recentPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMonthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(20, 20, 35));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel label = new JLabel("Current Month:");
        label.setForeground(new Color(200, 200, 210));
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        monthLabel = new JLabel(YearMonth.now().toString());
        monthLabel.setForeground(new Color(100, 200, 255));
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        panel.add(label);
        panel.add(monthLabel);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(15, 15, 25));
        
        // Income card
        JPanel incomeCard = createStatCard("Total Income", new Color(76, 175, 80));
        totalIncomeLabel = (JLabel) incomeCard.getComponent(1);
        panel.add(incomeCard);
        
        // Expense card
        JPanel expenseCard = createStatCard("Total Expenses", new Color(244, 67, 54));
        totalExpenseLabel = (JLabel) expenseCard.getComponent(1);
        panel.add(expenseCard);
        
        // Balance card
        JPanel balanceCard = createStatCard("Balance", new Color(100, 200, 255));
        balanceLabel = (JLabel) balanceCard.getComponent(1);
        panel.add(balanceCard);
        
        updateStats();
        return panel;
    }
    
    private JPanel createStatCard(String title, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(new Color(25, 25, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(150, 150, 170));
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel valueLabel = new JLabel("$0.00");
        valueLabel.setForeground(accentColor);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(15, 15, 25));
        panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(60, 60, 80), 1),
                "Recent Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(150, 150, 170)
        ));
        
        recentTransactionsArea = new JTextArea(8, 50);
        recentTransactionsArea.setBackground(new Color(20, 20, 35));
        recentTransactionsArea.setForeground(new Color(200, 200, 210));
        recentTransactionsArea.setEditable(false);
        recentTransactionsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        recentTransactionsArea.setMargin(new Insets(10, 10, 10, 10));
        
        updateRecentTransactions();
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsArea);
        scrollPane.setBackground(new Color(20, 20, 35));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateStats() {
        YearMonth currentMonth = YearMonth.now();
        double totalIncome = 0;
        double totalExpense = 0;
        
        for (Transaction t : transactions) {
            if (YearMonth.from(t.getDate()).equals(currentMonth)) {
                if ("Income".equals(t.getType())) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();
                }
            }
        }
        
        double balance = totalIncome - totalExpense;
        
        totalIncomeLabel.setText(String.format("$%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("$%.2f", totalExpense));
        balanceLabel.setText(String.format("$%.2f", balance));
        
        // Change balance color based on value
        if (balance >= 0) {
            balanceLabel.setForeground(new Color(76, 175, 80));
        } else {
            balanceLabel.setForeground(new Color(244, 67, 54));
        }
    }
    
    private void updateRecentTransactions() {
        StringBuilder sb = new StringBuilder();
        transactions.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(10)
                .forEach(t -> sb.append(t.toString()).append("\n"));
        
        if (sb.length() == 0) {
            sb.append("No transactions yet.");
        }
        
        recentTransactionsArea.setText(sb.toString());
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        updateStats();
        updateRecentTransactions(); // Now uses stored reference, no fragile lookups
    }
}
