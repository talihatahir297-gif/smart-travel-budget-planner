
### By Taliha Tahir (2025-CYS-122) | OOP Lab Project | UET Lahore

---

## Project Structure

```
SmartTravelBudgetPlanner/
├── src/main/java/com/travelplanner/
│   ├── Main.java                      ← Entry point
│   ├── db/
│   │   └── DatabaseConnection.java    ← MySQL connection + auto-init
│   ├── model/
│   │   ├── User.java                  ← Encapsulation demo
│   │   ├── Trip.java                  ← Budget logic (Abstraction)
│   │   ├── Expense.java               ← Abstract class (Abstraction)
│   │   ├── HotelExpense.java          ← Inheritance
│   │   ├── FoodExpense.java           ← Inheritance
│   │   ├── TransportExpense.java      ← Inheritance
│   │   ├── OtherExpense.java          ← Inheritance
│   │   └── ExpenseFactory.java        ← Polymorphism (Factory Pattern)
│   ├── dao/
│   │   ├── UserDAO.java               ← DB operations for users
│   │   ├── TripDAO.java               ← DB operations for trips
│   │   └── ExpenseDAO.java            ← DB operations for expenses
│   ├── gui/
│   │   ├── LoginFrame.java            ← Login screen
│   │   ├── RegisterFrame.java         ← Registration screen
│   │   ├── DashboardFrame.java        ← Main dashboard with trip list
│   │   ├── AddTripDialog.java         ← Create new trip dialog
│   │   ├── TripDetailFrame.java       ← Expense list + budget tracker
│   │   └── AddExpenseDialog.java      ← Add expense dialog
│   └── util/
│       ├── UIConstants.java           ← Colors, fonts, sizes
│       └── UIHelper.java              ← Reusable UI components
├── lib/
│   └── mysql-connector-j-8.3.0.jar   ← Place MySQL driver here
├── database_setup.sql                 ← Run this first in MySQL
└── SmartTravelBudgetPlanner.iml       ← IntelliJ module file
```

---

## OOP Concepts Demonstrated

| Concept         | Where                                                        |
|----------------|--------------------------------------------------------------|
| Encapsulation  | All model classes (private fields + getters/setters)         |
| Inheritance    | HotelExpense, FoodExpense, TransportExpense, OtherExpense    |
| Polymorphism   | ExpenseFactory.createExpense(), getCategoryIcon/Color()      |
| Abstraction    | Expense (abstract class), Trip.getRemainingBudget()          |

---

## Setup Instructions (IntelliJ IDEA)

### Step 1 — MySQL Setup
1. Open **MySQL Workbench** or terminal
2. Run `database_setup.sql`:
   ```
   mysql -u root -p < database_setup.sql
   ```
   Or paste contents into MySQL Workbench and execute.

### Step 2 — MySQL JDBC Driver
1. Download **mysql-connector-j-8.3.0.jar** from:
   https://dev.mysql.com/downloads/connector/j/
   (Select "Platform Independent" → download the ZIP → extract the .jar)
2. Place the JAR inside the `lib/` folder of this project.

### Step 3 — Open in IntelliJ
1. Open IntelliJ IDEA → **File → Open** → select the `SmartTravelBudgetPlanner` folder
2. IntelliJ will detect the `.iml` file automatically
3. If prompted, set **Project SDK** to Java 17 or 21

### Step 4 — Configure Database Password
Open `src/main/java/com/travelplanner/db/DatabaseConnection.java`
Change this line to match your MySQL root password:
```java
private static final String PASSWORD = ""; // ← Put your password here
```

### Step 5 — Add JAR to Project
1. In IntelliJ: **File → Project Structure → Modules → Dependencies**
2. Click **+** → **JARs or Directories**
3. Select `lib/mysql-connector-j-8.3.0.jar`
4. Click OK

### Step 6 — Run
- Right-click `Main.java` → **Run 'Main.main()'**
- The app will auto-create all tables on first run

---

## Features
- ✅ User Registration & Login
- ✅ Create / Delete Trips (with destination, dates, budget)
- ✅ Add Expenses (Hotel, Food, Transport, Other)
- ✅ Real-time budget tracking with progress bar
- ✅ Warning alert when budget is exceeded
- ✅ Delete expenses (double-click row)
- ✅ Update trip budget anytime
- ✅ MySQL database persistence

---

## Login Credentials (after running SQL setup)
- Email: `taliha@uet.edu.pk`
- Password: `123456`get-planner
