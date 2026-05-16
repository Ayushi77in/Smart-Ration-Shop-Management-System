# 🏪 Ration Shop Management System

A desktop application built in **Java 21 + JavaFX** to digitize and streamline the operations of a government fair-price (ration) shop. The system manages beneficiary registration, stock inventory, and monthly ration distribution — with built-in duplicate prevention to ensure every family receives their entitlement exactly once per month.

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [How It Works](#how-it-works)
- [OOP Concepts Applied](#oop-concepts-applied)
- [Team](#team)

---

## About the Project

Ration shops in India serve thousands of families each month under the Public Distribution System (PDS). Managing beneficiary records, tracking commodity stock, and recording distributions manually is error-prone and slow. This application replaces paper-based record keeping with a simple, offline-first desktop solution that any shopkeeper can operate without technical knowledge.

The app runs completely offline — no internet connection or server setup required. All data is stored in a local SQLite database file (`rationshop.db`).

---

## Features

### Module 1 — Beneficiary Management
- Register new ration card holders with name, card number, address, phone, and family size
- View all registered beneficiaries in a sortable table
- Search beneficiary by ration card number
- Delete beneficiary records
- Prevents duplicate ration card registration at the database level (`UNIQUE` constraint)

### Module 2 — Stock Management
- Add new commodities to inventory (Rice, Wheat, Sugar, etc.)
- Update stock quantities when new supplies arrive
- View current stock levels with last-updated timestamps
- Stock is automatically deducted when ration is distributed — no manual entry needed

### Module 3 — Distribution Management
- Search beneficiary by ration card number before distributing
- One-click ration distribution with standard monthly quota:
  - 🌾 Rice — 5 kg
  - 🌾 Wheat — 3 kg
  - 🍬 Sugar — 1 kg
- **Duplicate prevention** — system blocks distribution if the same beneficiary has already received ration in the current calendar month
- Full distribution history with month-wise filtering
- Automatically records date, month, and items given for every transaction

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core programming language |
| JavaFX | 21 | Desktop UI framework |
| SQLite | 3.45.1 | Offline embedded database |
| JDBC | — | Java–database connectivity layer |
| Maven | 3.x | Build tool and dependency management |

---

## Architecture

The project follows a clean **3-tier MVC architecture** with a dedicated service layer for business logic:

```
┌─────────────────────────────────────────────┐
│              UI Layer (JavaFX)              │
│  MainController → BeneficiaryController     │
│               → StockController             │
│               → DistributionController      │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│            Service Layer                    │
│         DistributionService                 │
│     (business rules & validations)          │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│              DAO Layer                      │
│  BeneficiaryDAO │ StockDAO │ DistributionDAO│
│         DatabaseConnection                  │
│         DatabaseInitializer                 │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│         SQLite Database (offline)           │
│              rationshop.db                  │
└─────────────────────────────────────────────┘
```

**Design patterns used:**
- **DAO Pattern** — separates SQL logic from business logic; each DAO manages one table
- **MVC Pattern** — UI controllers are separate from data access and business rules
- **Service Layer Pattern** — `DistributionService` coordinates across multiple DAOs
- **Utility Class Pattern** — `DatabaseConnection` provides a single static connection method

---

## Project Structure

```
RationShopManager/
│
├── pom.xml                          # Maven build configuration
├── rationshop.db                    # SQLite database (auto-created on first run)
│
└── src/main/java/com/RationShop/
    │
    ├── MainApp.java                 # JavaFX entry point (extends Application)
    ├── Main.java                    # Placeholder main class
    │
    ├── model/                       # POJO data classes
    │   ├── Beneficiary.java         # Represents a ration card holder
    │   ├── StockItem.java           # Represents a commodity in stock
    │   └── Distribution.java        # Represents a distribution record
    │
    ├── dao/                         # Data Access Objects (SQL operations)
    │   ├── DatabaseConnection.java  # JDBC connection utility
    │   ├── DatabaseInitializer.java # Creates tables on first run
    │   ├── BeneficiaryDAO.java      # CRUD for beneficiaries table
    │   ├── StockDAO.java            # CRUD + deduction for stock table
    │   └── DistributionDAO.java     # Insert, query, duplicate check
    │
    ├── service/                     # Business logic
    │   └── DistributionService.java # Validates and orchestrates distribution
    │
    └── ui/                          # JavaFX UI controllers
        ├── MainController.java      # BorderPane shell with TabPane
        ├── BeneficiaryController.java
        ├── StockController.java
        └── DistributionController.java
```

---

## Database Schema

**Table: `beneficiaries`**
```sql
CREATE TABLE IF NOT EXISTS beneficiaries (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    name               TEXT NOT NULL,
    ration_card_number TEXT UNIQUE NOT NULL,
    address            TEXT,
    phone_number       TEXT,
    family_size        INTEGER
);
```

**Table: `stock`**
```sql
CREATE TABLE IF NOT EXISTS stock (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name    TEXT NOT NULL,
    quantity_kg  REAL,
    last_updated TEXT
);
```

**Table: `distributions`**
```sql
CREATE TABLE IF NOT EXISTS distributions (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    beneficiary_id   INTEGER,
    distribution_date TEXT,
    month_year       TEXT,
    items_given      TEXT,
    FOREIGN KEY(beneficiary_id) REFERENCES beneficiaries(id)
);
```

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) **21 or higher**
- Apache Maven **3.6+**
- IntelliJ IDEA (recommended) or any Java IDE with Maven support

### Run the Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/RationShopManager.git
   cd RationShopManager
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

   Or run directly from your IDE by executing `MainApp.java`.

> **Note:** The `rationshop.db` SQLite file is created automatically in the project root on the first launch. No database setup required.

### Running in IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA
2. Let Maven import all dependencies automatically
3. Right-click `MainApp.java` → **Run 'MainApp.main()'**

---

## How It Works

### Distributing Ration — Full Flow

1. Shopkeeper opens the **Distribution tab**
2. Enters the beneficiary's ration card number → clicks **Search**
3. System queries `beneficiaries` table → displays family details
4. Shopkeeper clicks **Mark as Distributed**
5. `DistributionService` runs the following checks:
   - Queries `distributions` table: `SELECT COUNT(*) WHERE beneficiary_id = ? AND month_year = ?`
   - If count > 0 → shows **"Already received this month"** warning and stops
   - If count = 0 → proceeds to deduct 5 kg Rice, 3 kg Wheat, 1 kg Sugar from `stock` table
   - Inserts a new row into `distributions` table with today's date and month
6. Distribution history table refreshes automatically

### Key Business Rules

| Rule | Implementation |
|---|---|
| No duplicate monthly distribution | `DistributionDAO.hasReceivedThisMonth()` checks `COUNT(*)` by beneficiary + month |
| Unique ration card numbers | `UNIQUE` constraint on `ration_card_number` column in DB |
| Automatic stock deduction | `StockDAO.deductStock()` called by `DistributionService` on every successful distribution |
| Standard monthly quota | Hardcoded in `DistributionService`: Rice 5 kg, Wheat 3 kg, Sugar 1 kg |

---

## OOP Concepts Applied

| Concept | Where used |
|---|---|
| **Encapsulation** | All model classes (`Beneficiary`, `StockItem`, `Distribution`) — private fields with controlled getters/setters |
| **Abstraction** | DAO layer hides all SQL from the UI; controllers call simple methods like `dao.addBeneficiary(b)` |
| **Inheritance** | `MainApp extends Application` (inherits JavaFX lifecycle); `Beneficiary` implicitly extends `Object` |
| **Polymorphism** | `@Override toString()` in model classes; `ObservableList` used as `List`; `PreparedStatement` used as `Statement` |
| **Composition** | `DistributionService` has-a `DistributionDAO` and has-a `StockDAO` |

---

## Team

| Member | Role | Modules Owned |
|---|---|---|
| Member 1 | Database Architect | `DatabaseConnection`, `DatabaseInitializer`, `pom.xml`, schema design |
| Member 2 | Data Modeler & Beneficiary Developer | All 3 model classes, `BeneficiaryDAO`, `BeneficiaryController` |
| Member 3 | Stock & Inventory Developer | `StockDAO`, `StockController` |
| Member 4 | Business Logic & Integration | `DistributionDAO`, `DistributionService`, `DistributionController`, `MainApp`, `MainController` |

---

## License

This project was developed as an academic project. Feel free to fork and extend it for educational purposes.
