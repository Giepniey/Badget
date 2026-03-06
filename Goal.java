import java.io.Serializable;
import java.time.LocalDate;

public class Goal implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate targetDate;
    private String imageBase64; // Store image as base64
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    
    public Goal(String name, double targetAmount, LocalDate targetDate, String imageBase64) {
        this.id = generateId();
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
    
    private static String generateId() {
        return "GOAL-" + System.currentTimeMillis();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getImageBase64() { return imageBase64; }
    public LocalDate getCreatedDate() { return createdDate; }
    public LocalDate getLastModifiedDate() { return lastModifiedDate; }
    public double getProgress() { return (currentAmount / targetAmount) * 100; }
    public boolean isCompleted() { return currentAmount >= targetAmount; }
    
    // Setters
    public void setName(String name) { 
        this.name = name;
        this.lastModifiedDate = LocalDate.now();
    }
    public void setTargetAmount(double targetAmount) { 
        this.targetAmount = targetAmount;
        this.lastModifiedDate = LocalDate.now();
    }
    public void setCurrentAmount(double currentAmount) { 
        this.currentAmount = currentAmount;
        this.lastModifiedDate = LocalDate.now();
    }
    public void setTargetDate(LocalDate targetDate) { 
        this.targetDate = targetDate;
        this.lastModifiedDate = LocalDate.now();
    }
    public void setImageBase64(String imageBase64) { 
        this.imageBase64 = imageBase64;
        this.lastModifiedDate = LocalDate.now();
    }
    
    public void addToCurrentAmount(double amount) {
        this.currentAmount += amount;
        this.lastModifiedDate = LocalDate.now();
    }
    
    public String toCsv() {
        // Escape image data for CSV
        String escapedImage = imageBase64 != null ? imageBase64.replace("\n", "\\n") : "";
        return String.format("%s|%s|%.2f|%.2f|%s|%s|%s|%s",
                id, name, targetAmount, currentAmount, targetDate, 
                escapedImage, createdDate, lastModifiedDate);
    }
    
    public static Goal fromCsv(String line) {
        try {
            String[] parts = line.split("\\|", 8);
            if (parts.length < 8) return null;
            
            String imageData = parts[5].replace("\\n", "\n");
            return new Goal(parts[0], parts[1], Double.parseDouble(parts[2]), 
                    Double.parseDouble(parts[3]), LocalDate.parse(parts[4]), 
                    imageData.isEmpty() ? null : imageData, LocalDate.parse(parts[6]), 
                    LocalDate.parse(parts[7]));
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f / $%.2f (%.1f%%)", 
                name, currentAmount, targetAmount, getProgress());
    }
}
