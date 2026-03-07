import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String GOALS_FILE = DATA_DIR + "/goals.csv";
    private static final String HISTORY_FILE = DATA_DIR + "/transaction_history.csv";
    
    public DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();
    }
    
    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.write("ID,Date,Category,Amount,Description,Type\n");
            for (Transaction t : transactions) {
                writer.write(t.toCsv() + "\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return transactions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                Transaction t = Transaction.fromCsv(line);
                if (t != null) transactions.add(t);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return transactions;
    }
    
    public void saveGoals(List<Goal> goals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GOALS_FILE))) {
            writer.write("ID|Name|TargetAmount|CurrentAmount|TargetDate|Image|CreatedDate|LastModifiedDate\n");
            for (Goal g : goals) {
                writer.write(g.toCsv() + "\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public List<Goal> loadGoals() {
        List<Goal> goals = new ArrayList<>();
        File file = new File(GOALS_FILE);
        if (!file.exists()) return goals;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                Goal g = Goal.fromCsv(line);
                if (g != null) goals.add(g);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return goals;
    }
    
    public void saveTransactionHistory(TransactionHistory history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            if (new File(HISTORY_FILE).length() == 0) {
                writer.write("ID,TransactionID,ChangeType,PreviousValues,NewValues,Timestamp\n");
            }
            writer.write(history.toCsv() + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public List<TransactionHistory> loadTransactionHistory(String transactionId) {
        List<TransactionHistory> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return history;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                TransactionHistory h = TransactionHistory.fromCsv(line);
                if (h != null && h.getTransactionId().equals(transactionId)) {
                    history.add(h);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return history;
    }
}

class TransactionHistory implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id, transactionId, changeType, previousValues, newValues;
    private LocalDateTime timestamp;
    
    public TransactionHistory(String transactionId, String changeType, 
                            String previousValues, String newValues) {
        this.id = "HIST-" + System.currentTimeMillis();
        this.transactionId = transactionId;
        this.changeType = changeType;
        this.previousValues = previousValues;
        this.newValues = newValues;
        this.timestamp = LocalDateTime.now();
    }
    
    public TransactionHistory(String id, String transactionId, String changeType,
                            String previousValues, String newValues, LocalDateTime timestamp) {
        this.id = id;
        this.transactionId = transactionId;
        this.changeType = changeType;
        this.previousValues = previousValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }
    
    public String getId() { return id; }
    public String getTransactionId() { return transactionId; }
    public String getChangeType() { return changeType; }
    
    public String toCsv() {
        String prev = previousValues != null ? previousValues.replace(",", ";") : "";
        String next = newValues != null ? newValues.replace(",", ";") : "";
        return String.format("%s,%s,%s,%s,%s,%s", id, transactionId, changeType, prev, next, timestamp);
    }
    
    public static TransactionHistory fromCsv(String line) {
        try {
            String[] parts = line.split(",", 6);
            if (parts.length < 6) return null;
            return new TransactionHistory(parts[0], parts[1], parts[2],
                    parts[3].replace(";", ","), parts[4].replace(";", ","),
                    LocalDateTime.parse(parts[5]));
        } catch (Exception e) { return null; }
    }
}

class Goal implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id, name, imageBase64;
    private double targetAmount, currentAmount;
    private LocalDate targetDate, createdDate, lastModifiedDate;
    
    public Goal(String name, double targetAmount, LocalDate targetDate, String imageBase64) {
        this.id = "GOAL-" + System.currentTimeMillis();
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.targetDate = targetDate;
        this.imageBase64 = imageBase64;
        this.createdDate = LocalDate.now();
        this.lastModifiedDate = LocalDate.now();
    }
    
    public Goal(String id, String name, double targetAmount, double currentAmount, 
                LocalDate targetDate, String imageBase64, LocalDate createdDate, LocalDate lastModifiedDate) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.imageBase64 = imageBase64;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getImageBase64() { return imageBase64; }
    
    public String toCsv() {
        String escaped = imageBase64 != null ? imageBase64.replace("\n", "\\n") : "";
        return String.format("%s|%s|%.2f|%.2f|%s|%s|%s|%s",
                id, name, targetAmount, currentAmount, targetDate, escaped, createdDate, lastModifiedDate);
    }
    
    public static Goal fromCsv(String line) {
        try {
            String[] parts = line.split("\\|", 8);
            if (parts.length < 8) return null;
            String image = parts[5].replace("\\n", "\n");
            return new Goal(parts[0], parts[1], Double.parseDouble(parts[2]), 
                    Double.parseDouble(parts[3]), LocalDate.parse(parts[4]), 
                    image.isEmpty() ? null : image, LocalDate.parse(parts[6]), 
                    LocalDate.parse(parts[7]));
        } catch (Exception e) { return null; }
    }
}
