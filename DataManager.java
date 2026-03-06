import java.io.*;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String GOALS_FILE = DATA_DIR + "/goals.csv";
    private static final String HISTORY_FILE = DATA_DIR + "/transaction_history.csv";
    
    public DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    // ===== TRANSACTIONS =====
    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.write("ID,Date,Category,Amount,Description,Type\n");
            for (Transaction t : transactions) {
                writer.write(t.toCsv() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        
        if (!file.exists()) {
            return transactions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                Transaction t = Transaction.fromCsv(line);
                if (t != null) {
                    transactions.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // ===== GOALS =====
    public void saveGoals(List<Goal> goals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GOALS_FILE))) {
            writer.write("ID|Name|TargetAmount|CurrentAmount|TargetDate|Image|CreatedDate|LastModifiedDate\n");
            for (Goal g : goals) {
                writer.write(g.toCsv() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Goal> loadGoals() {
        List<Goal> goals = new ArrayList<>();
        File file = new File(GOALS_FILE);
        
        if (!file.exists()) {
            return goals;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                Goal g = Goal.fromCsv(line);
                if (g != null) {
                    goals.add(g);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return goals;
    }
    
    // ===== TRANSACTION HISTORY =====
    public void saveTransactionHistory(TransactionHistory history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            // Write header only if file is new
            if (new File(HISTORY_FILE).length() == 0) {
                writer.write("ID,TransactionID,ChangeType,PreviousValues,NewValues,Timestamp\n");
            }
            writer.write(history.toCsv() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<TransactionHistory> loadTransactionHistory(String transactionId) {
        List<TransactionHistory> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        
        if (!file.exists()) {
            return history;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                TransactionHistory h = TransactionHistory.fromCsv(line);
                if (h != null && h.getTransactionId().equals(transactionId)) {
                    history.add(h);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return history;
    }
    
    public List<TransactionHistory> loadAllHistory() {
        List<TransactionHistory> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        
        if (!file.exists()) {
            return history;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                TransactionHistory h = TransactionHistory.fromCsv(line);
                if (h != null) {
                    history.add(h);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return history;
    }
}
