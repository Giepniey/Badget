# Bug Report - Badget Application

## Bug #1: OverviewPanel.java - Fragile Component Access in refresh() method
**Location:** Line 174
**Severity:** HIGH

### Problem:
```java
JTextArea textArea = (JTextArea) ((JScrollPane) ((JPanel) getComponent(2)).getComponent(0)).getViewport().getView();
```

This deeply nested hard-coded component access is:
- **Brittle**: If component ordering changes, this crashes with IndexOutOfBoundsException
- **Unmaintainable**: Hard to understand and modify
- **Error-prone**: Assumes exact structure without validation

### Solution:
Store a reference to the textArea when creating it, don't try to retrieve it later.

---

## Bug #2: OverviewPanel.java - Missing Import
**Location:** Line 1-7
**Severity:** LOW

The comment shows `LocalDate` import is unused, but more importantly, the code may not compile if imports are incomplete.

### Solution:
Clean up unused imports.

---

## Bug #3: Transaction.java - CSV Parsing Issue
**Location:** Lines 56-62 (fromCsv method)
**Severity:** MEDIUM

### Problem:
```java
String[] parts = line.split(",", 6);
```

If the description field contains commas, the CSV parsing will fail. For example:
- `TXN-123,2024-01-01,Food,25.50,"Lunch with Bob, Alice, and Tim",Expense` ❌ Breaks

### Solution:
Use proper CSV parsing that handles quoted fields with commas.

---

## Summary Table

| File | Line | Bug Type | Severity |
|------|------|----------|----------|
| OverviewPanel.java | 174 | Fragile component access | HIGH |
| OverviewPanel.java | 8 | Unused import | LOW |
| Transaction.java | 56-62 | CSV parsing flaw | MEDIUM |

All bugs can cause runtime errors or data loss.
