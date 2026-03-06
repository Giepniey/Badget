import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransactionFormPanel extends JPanel implements Refreshable {
    private DashboardPanel parentPanel;
    private JTextField descriptionField;
    private JSpinner amountSpinner;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> typeCombo;
    private JSpinner dateSpinner;
    private JTable transactionTable;
    private JScrollPane tableScrollPane;
    private List<Transaction> transactions;
    private DataManager dataManager;
    private Transaction selectedTransaction = null;
    
    private static final String[] EXPENSE_CATEGORIES = {
        "Food & Dining", "Transportation", "Shopping", "Entertainment",
        "Utilities", "Health", "Education", "Rent", "Other"
    };
    
    private static final String[] INCOME_CATEGORIES = {
        "Salary", "Freelance", "Investment", "Bonus", "Gift", "Other"
    };
    
    public TransactionFormPanel(DashboardPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.dataManager = new DataManager();
        this.transactions = dataManager.loadTransactions();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();
        
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(createTransactionsTablePanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(15, 15, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(createLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        typeCombo = new JComboBox<>(new String[]{"Expense", "Income"});
        styleComboBox(typeCombo);
        typeCombo.addActionListener(e -> updateCategories());
        panel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(createLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        styleSpinner(dateSpinner);
        panel.add(dateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        categoryCombo = new JComboBox<>(EXPENSE_CATEGORIES);
        styleComboBox(categoryCombo);
        panel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(createLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.99, 0.01));
        styleSpinner(amountSpinner);
        panel.add(amountSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        panel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        descriptionField = new JTextField(20);
        styleTextField(descriptionField);
        panel.add(descriptionField, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(15, 15, 25));
        
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBorder(BorderFactory.createEmptyBorder());
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addTransaction());
        
        JButton updateBtn = new JButton("Update Transaction");
        updateBtn.setPreferredSize(new Dimension(150, 40));
        updateBtn.setBackground(new Color(100, 150, 200));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateBtn.setBorder(BorderFactory.createEmptyBorder());
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateBtn.addActionListener(e -> updateTransaction());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(100, 40));
        clearBtn.setBackground(new Color(100, 100, 120));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.setBorder(BorderFactory.createEmptyBorder());
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearForm());
        
        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(clearBtn);
        
        return panel;
    }
    
    private JPanel createTransactionsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(15, 15, 25));
        panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(60, 60, 80), 1),
                "Recent Transactions - Click to Edit",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(150, 150, 170)
        ));
        
        String[] columns = {"Date", "Type", "Category", "Amount", "Description", "Actions"};
        Object[][] data = new Object[0][6];
        
        transactionTable = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };
        transactionTable.setBackground(new Color(20, 20, 35));
        transactionTable.setForeground(new Color(200, 200, 210));
        transactionTable.setGridColor(new Color(60, 60, 80));
        transactionTable.setRowHeight(30);
        
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (transactionTable.getSelectedRow() >= 0) {
                loadTransactionToForm();
            }
        });
        
        tableScrollPane = new JScrollPane(transactionTable);
        tableScrollPane.setBackground(new Color(20, 20, 35));
        tableScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        updateTransactionTable();
        return panel;
    }
    
    private void updateTransactionTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Date", "Type", "Category", "Amount", "Description", "Edit", "Delete"});
        
        for (Transaction t : transactions) {
            model.addRow(new Object[]{
                t.getDate(),
                t.getType(),
                t.getCategory(),
                String.format("$%.2f", t.getAmount()),
                t.getDescription(),
                "Edit",
                "Delete"
            });
        }
        
        transactionTable.setModel(model);
        
        transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = transactionTable.rowAtPoint(evt.getPoint());
                int col = transactionTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 6) {
                    transactionTable.setRowSelectionInterval(row, row);
                    deleteTransaction();
                }
            }
        });
    }
    
    private void loadTransactionToForm() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedTransaction = transactions.get(selectedRow);
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
            String category = (String) categoryCombo.getSelectedItem();
            double amount = (double) amountSpinner.getValue();
            String description = descriptionField.getText();
            String type = (String) typeCombo.getSelectedItem();
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Transaction transaction = new Transaction(date, category, amount, description, type);
            parentPanel.addTransaction(transaction);
            JOptionPane.showMessageDialog(this, "Transaction added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTransaction() {
        if (selectedTransaction == null) {
            JOptionPane.showMessageDialog(this, "Select a transaction to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate newDate = new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime()).toLocalDate();
            String newCategory = (String) categoryCombo.getSelectedItem();
            double newAmount = (double) amountSpinner.getValue();
            String newDescription = descriptionField.getText();
            String newType = (String) typeCombo.getSelectedItem();
            
            if (newDescription.isEmpty() || newAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Record history
            String oldValues = String.format("Date:%s,Category:%s,Amount:%.2f,Description:%s,Type:%s",
                    selectedTransaction.getDate(), selectedTransaction.getCategory(),
                    selectedTransaction.getAmount(), selectedTransaction.getDescription(),
                    selectedTransaction.getType());
            String newValues = String.format("Date:%s,Category:%s,Amount:%.2f,Description:%s,Type:%s",
                    newDate, newCategory, newAmount, newDescription, newType);
            
            TransactionHistory history = new TransactionHistory(selectedTransaction.getId(), "EDITED", oldValues, newValues);
            dataManager.saveTransactionHistory(history);
            
            selectedTransaction.setDate(newDate);
            selectedTransaction.setCategory(newCategory);
            selectedTransaction.setAmount(newAmount);
            selectedTransaction.setDescription(newDescription);
            selectedTransaction.setType(newType);
            
            dataManager.saveTransactions(transactions);
            updateTransactionTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Transaction updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a transaction to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Transaction toDelete = transactions.get(selectedRow);
            TransactionHistory history = new TransactionHistory(toDelete.getId(), "DELETED", 
                    toDelete.toString(), "");
            dataManager.saveTransactionHistory(history);
            
            transactions.remove(selectedRow);
            dataManager.saveTransactions(transactions);
            updateTransactionTable();
            clearForm();
        }
    }
    
    private void updateCategories() {
        String type = (String) typeCombo.getSelectedItem();
        categoryCombo.removeAllItems();
        for (String cat : "Income".equals(type) ? INCOME_CATEGORIES : EXPENSE_CATEGORIES) {
            categoryCombo.addItem(cat);
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
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 210));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(25, 25, 40));
        field.setForeground(new Color(200, 200, 210));
        field.setCaretColor(new Color(100, 200, 255));
        field.setBorder(new LineBorder(new Color(60, 60, 80), 1));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(new Color(25, 25, 40));
        combo.setForeground(new Color(200, 200, 210));
        combo.setBorder(new LineBorder(new Color(60, 60, 80), 1));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setBackground(new Color(25, 25, 40));
        spinner.setForeground(new Color(200, 200, 210));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    @Override
    public void refresh(List<Transaction> transactionsList) {
        this.transactions = transactionsList;
        updateTransactionTable();
    }
}
