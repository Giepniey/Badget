import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private List<Transaction> transactions;
    private DataManager dataManager;
    
    public DashboardPanel() {
        this.dataManager = new DataManager();
        this.transactions = dataManager.loadTransactions();
        
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        add(createHeader(), BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(50, 50, 50));
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        tabbedPane.addTab("Dashboard", new OverviewPanel(transactions));
        tabbedPane.addTab("Add Transaction", new TransactionFormPanel(this));
        tabbedPane.addTab("Reports", new ReportsPanel(transactions));
        tabbedPane.addTab("Categories", new CategoriesPanel(transactions));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);
        header.setLayout(new BorderLayout());
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        
        JLabel title = new JLabel("Badget");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(50, 50, 50));
        leftPanel.add(title);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshData());
        rightPanel.add(refreshBtn);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    public void refreshData() {
        transactions = dataManager.loadTransactions();
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
}

interface Refreshable {
    void refresh(List<Transaction> transactions);
}
