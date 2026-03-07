import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransactionFormPanel extends JPanel implements Refreshable {
    private JComboBox<String> typeCombo, categoryCombo;
    private JSpinner dateSpinner, amountSpinner;
    private JTextField descriptionField;
    private JTable transactionTable;
    private List<Transaction> transactions;
    private DataManager dataManager;
    private DashboardPanel parentPanel;
    private Transaction selectedTransaction = null;
    
    private static final String[] EXPENSE_CATEGORIES = {
        "Food", "Transport", "Shopping", "Entertainment", "Utilities", "Health", "Education", "Rent", "Other"
    };
    private static final String[] INCOME_CATEGORIES = {
        "Salary", "Freelance", "Investment", "Bonus", "Gift", "Other"
    };
    
    public TransactionFormPanel(DashboardPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.dataManager = new DataManager();
        this.transactions = dataManager.loadTransactions();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        
        add(createFormPanel());
        add(Box.createVerticalStrut(20));
        add(createTablePanel());
        add(Box.createVerticalStrut(20));
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        typeCombo = new JComboBox<>(new String[]{"Expense", "Income"});
        typeCombo.addActionListener(e -> updateCategories());
        categoryCombo = new JComboBox<>(EXPENSE_CATEGORIES);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.99, 0.01));
        descriptionField = new JTextField(20);
        
        addFormRow(panel, gbc, 0, "Type:", typeCombo);
        addFormRow(panel, gbc, 1, "Date:", dateSpinner);
        addFormRow(panel, gbc, 2, "Category:", categoryCombo);
        addFormRow(panel, gbc, 3, "Amount:", amountSpinner);
        addFormRow(panel, gbc, 4, "Description:", descriptionField);
        
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addTransaction());
        
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(52, 152, 219));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        updateBtn.setFocusPainted(false);
        updateBtn.addActionListener(e -> updateTransaction());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(150, 150, 150));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearForm());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(field, gbc);
        gbc.gridx = 0;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        JLabel title = new JLabel("Transactions");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        transactionTable = new JTable(new DefaultTableModel(
            new String[]{"Date", "Type", "Category", "Amount", "Description"}, 0)) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        transactionTable.setBackground(Color.WHITE);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 11));
        transactionTable.setRowHeight(25);
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (transactionTable.getSelectedRow() >= 0) loadTransactionToForm();
        });
        
        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        updateTable();
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    
    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        model.setRowCount(0);
        for (Transaction t : transactions) {
            model.addRow(new Object[]{t.getDate(), t.getType(), t.getCategory(), 
                                      String.format("₱%.2f", t.getAmount()), t.getDescription()});
        }
    }
    
    private void loadTransactionToForm() {
        int row = transactionTable.getSelectedRow();
        if (row >= 0) {
            selectedTransaction = transactions.get(row);
            dateSpinner.setValue(java.sql.Date.valueOf(selectedTransaction.getDate()));
            typeCombo.setSelectedItem(selectedTransaction.getType());
            categoryCombo.setSelectedItem(selectedTransaction.getCategory());
            amountSpinner.setValue(selectedTransaction.getAmount());
            descriptionField.setText(selectedTransaction.getDescription());
        }
    }
    
    private void addTransaction() {
        try {
            LocalDate date = new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime()).toLocalDate();
            String description = descriptionField.getText();
            double amount = (double) amountSpinner.getValue();
            if (description.isEmpty() || amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Transaction t = new Transaction(date, (String) categoryCombo.getSelectedItem(), 
                                           amount, description, (String) typeCombo.getSelectedItem());
            parentPanel.addTransaction(t);
            JOptionPane.showMessageDialog(this, "Added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTransaction() {
        if (selectedTransaction == null) {
            JOptionPane.showMessageDialog(this, "Select a transaction", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            selectedTransaction.setDate(new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime()).toLocalDate());
            selectedTransaction.setCategory((String) categoryCombo.getSelectedItem());
            selectedTransaction.setAmount((double) amountSpinner.getValue());
            selectedTransaction.setDescription(descriptionField.getText());
            selectedTransaction.setType((String) typeCombo.getSelectedItem());
            dataManager.saveTransactions(transactions);
            updateTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        descriptionField.setText("");
        amountSpinner.setValue(0.0);
        dateSpinner.setValue(new java.util.Date());
        typeCombo.setSelectedIndex(0);
        selectedTransaction = null;
        updateCategories();
    }
    
    private void updateCategories() {
        String type = (String) typeCombo.getSelectedItem();
        categoryCombo.removeAllItems();
        for (String cat : "Income".equals(type) ? INCOME_CATEGORIES : EXPENSE_CATEGORIES) {
            categoryCombo.addItem(cat);
        }
    }
    
    @Override
    public void refresh(List<Transaction> transactions) {
        this.transactions = transactions;
        updateTable();
    }
}
