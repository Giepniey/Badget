import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private LocalDate date;
    private String category;
    private double amount;
    private String description;
    private String type; // "Income" or "Expense"
    
    public Transaction(String id, LocalDate date, String category, double amount, 
                       String description, String type) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.type = type;
    }
    
    public Transaction(LocalDate date, String category, double amount, 
                       String description, String type) {
        this.id = generateId();
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.type = type;
    }
    
    private static String generateId() {
        return "TXN-" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    
    public void setDate(LocalDate date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    
    public String toCsv() {
        // Properly escape description field by wrapping in quotes
        String escapedDescription = "\"" + description.replace("\"", "\"\"") + "\"";
        return String.format("%s,%s,%s,%.2f,%s,%s",
                id, date, category, amount, escapedDescription, type);
    }
    
    public static Transaction fromCsv(String line) {
        try {
            // Simple but effective CSV parsing for this structure
            // Handles quoted fields with commas
            String[] parts = parseCsvLine(line);
            
            if (parts.length < 6) return null;
            
            LocalDate date = LocalDate.parse(parts[1].trim());
            String description = parts[4].trim();
            // Remove surrounding quotes if present
            if (description.startsWith("\"") && description.endsWith("\"")) {
                description = description.substring(1, description.length() - 1);
                description = description.replace("\"\"", "\""); // Unescape quotes
            }
            
            return new Transaction(parts[0].trim(), date, parts[2].trim(), 
                    Double.parseDouble(parts[3].trim()), description, parts[5].trim());
        } catch (Exception e) {
            System.err.println("Error parsing CSV line: " + line + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Simple CSV parser that respects quoted fields
     */
    private static String[] parseCsvLine(String line) {
        java.util.List<String> result = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s | %s | $%.2f | %s", 
                date, type, category, amount, description);
    }
}
