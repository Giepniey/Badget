# Badget - Budget & Expense Tracker

A comprehensive personal finance management application built in Java with a professional GUI interface.

## Features

### 📊 Dashboard
- Real-time summary of income, expenses, and balance for the current month
- Recent transactions display
- Color-coded balance indicator (green for positive, red for negative)

### 💰 Transaction Management
- Add income and expense transactions with details
- Automatic category selection based on transaction type
- Date, amount, category, and description fields
- Persistent storage using CSV format

### 📈 Monthly Reports
- Generate detailed monthly summaries
- View total income and expenses breakdown by category
- Analyze spending percentages
- Export reports to text files

### 🏷️ Category Analysis
- Visual breakdown of income by category
- Visual breakdown of expenses by category
- Percentage-based analysis
- Horizontal bar charts for easy comparison

### 💾 Data Persistence
- Automatic CSV file storage in `data/transactions.csv`
- Load previous transactions on startup
- Never lose your financial data

## System Requirements

- Java JDK 8 or higher
- Windows, macOS, or Linux

## Installation & Setup

### 1. Compile the Application
```bash
javac *.java
```

### 2. Run the Application
```bash
java Badget
```

The application will create a `data` directory automatically if it doesn't exist.

## Usage Guide

### Adding a Transaction
1. Click the "Add Transaction" tab
2. Select transaction type (Expense or Income)
3. Choose or enter the date
4. Select a category from the dropdown
5. Enter the amount
6. Add a description
7. Click "Add Transaction"

### Viewing Dashboard
- Click the "Dashboard" tab to see:
  - Current month summary
  - Total income and expenses
  - Remaining balance
  - 10 most recent transactions

### Generating Reports
1. Click the "Monthly Report" tab
2. Select the desired month
3. View detailed breakdown by category
4. Click "Export Report" to save as text file

### Analyzing Categories
1. Click the "Categories" tab
2. View income and expense distribution visually
3. See percentage breakdown for each category

## Data Storage

All transactions are stored in `data/transactions.csv` with the following format:
```
ID,Date,Category,Amount,Description,Type
TXN-1234567890,2024-03-06,Food & Dining,25.50,Lunch at cafe,Expense
TXN-1234567891,2024-03-06,Salary,3000.00,Monthly salary,Income
```

## Built-in Categories

**Expense Categories:**
- Food & Dining
- Transportation
- Shopping
- Entertainment
- Utilities
- Health
- Education
- Rent
- Other

**Income Categories:**
- Salary
- Freelance
- Investment
- Bonus
- Gift
- Other

## Application Structure

- **Badget.java** - Main application launcher
- **Transaction.java** - Transaction data model
- **DataManager.java** - CSV file handling
- **DashboardPanel.java** - Main navigation and container
- **OverviewPanel.java** - Dashboard statistics and summary
- **TransactionFormPanel.java** - Transaction input form
- **ReportsPanel.java** - Monthly reports and analysis
- **CategoriesPanel.java** - Category breakdown visualization
- **Refreshable.java** - Interface for updating panels

## Features Implemented

✅ Record income and expense transactions  
✅ Monthly summaries with total calculations  
✅ Remaining balance display  
✅ Category-based spending reports  
✅ CSV file persistence  
✅ Professional GUI with dark theme  
✅ Export reports functionality  
✅ Real-time data refresh  
✅ Category analysis with percentages  
✅ Multi-tab interface  

## Notes

- All monetary values are stored with 2 decimal precision
- Dates are stored in ISO format (YYYY-MM-DD)
- The application uses the system timezone
- Dark theme is optimized for extended use
- No internet connection required

## Future Enhancement Ideas

- Multi-user accounts
- Recurring transactions
- Budget forecasting
- Advanced charts and graphs
- Bank account integration
- PDF report export
- Data backup and import
- Budget alerts

## License

This project was created for CC106 - Application Development and Emerging Technologies

## Support

For issues or questions, review the code comments or the project proposal documentation.
