import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private List<Transaction> transactions;
    private List<Goal> goals;
    private DataManager dataManager;
    
    public DashboardPanel() {
        this.dataManager = new DataManager();
        this.transactions = dataManager.loadTransactions();
        this.goals = dataManager.loadGoals();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 25));
        
        // Header to
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(25, 25, 40));
        tabbedPane.setForeground(new Color(230, 230, 240));
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        tabbedPane.addTab("Dashboard", new OverviewPanel(transactions));
        tabbedPane.addTab("Add Transaction", new TransactionFormPanel(this));
        tabbedPane.addTab("Monthly Report", new ReportsPanel(transactions));
        tabbedPane.addTab("Categories", new CategoriesPanel(transactions));
        tabbedPane.addTab("Goals", new GoalsPanel(this, goals));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(20, 20, 35));
        header.setLayout(new BorderLayout(15, 10));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Badget");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(100, 200, 255));
        
        JLabel subtitle = new JLabel("Budget & Expense Tracker");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(150, 150, 170));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }
    
    public void refreshData() {
        transactions = dataManager.loadTransactions();
        goals = dataManager.loadGoals();
        
        // Refresh all tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof Refreshable) {
                ((Refreshable) comp).refresh(transactions);
            }
        }
    }
    
    public void addTransaction(Transaction t) {
        transactions.add(t);
        dataManager.saveTransactions(transactions);
        refreshData();
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public List<Goal> getGoals() {
        return goals;
    }
}

interface Refreshable {
    void refresh(List<Transaction> transactions);
}
