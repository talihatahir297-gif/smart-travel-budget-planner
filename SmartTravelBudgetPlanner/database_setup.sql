-- ============================================
--  Smart Travel Budget Planner - MySQL Setup
--  Run this in MySQL Workbench or terminal
-- ============================================

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS travel_budget_db;
USE travel_budget_db;

-- Step 2: Users table
CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Step 3: Trips table
CREATE TABLE IF NOT EXISTS trips (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    destination  VARCHAR(200) NOT NULL,
    start_date   DATE,
    end_date     DATE,
    total_budget DOUBLE NOT NULL DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Step 4: Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    trip_id      INT NOT NULL,
    category     VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    amount       DOUBLE NOT NULL,
    expense_date DATE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

-- Step 5: Sample data (optional - to test quickly)
INSERT INTO users (name, email, password) VALUES
  ('Taliha Tahir', 'taliha@uet.edu.pk', '123456');

SELECT 'Database setup complete!' AS status;
