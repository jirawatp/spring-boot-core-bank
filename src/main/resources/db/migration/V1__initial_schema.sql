-- Create roles table
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

-- Create users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       citizen_id VARCHAR(13) UNIQUE,
                       thai_name VARCHAR(100),
                       english_name VARCHAR(100),
                       pin VARCHAR(6),
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

-- Create user_roles junction table
CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Accounts Table (Bank Accounts)
CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          account_number CHAR(7) UNIQUE NOT NULL, -- 7-digit account number
                          balance DECIMAL(15, 2) DEFAULT 0 CHECK (balance >= 0), -- Ensure non-negative balance
                          created_by_teller_id BIGINT REFERENCES users(id) ON DELETE SET NULL, -- Who created this account
                          user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- Account belongs to a user
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions Table
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              account_id BIGINT REFERENCES accounts(id) ON DELETE CASCADE,
                              type VARCHAR(50) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
                              amount DECIMAL(15, 2) NOT NULL CHECK (amount >= 0), -- Allow zero for initial deposit
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 🔥 Adding Necessary Indexes for Performance 🔥

-- Index for faster lookup on email (login purpose)
CREATE INDEX idx_users_email ON users(email);

-- Index for citizen_id (faster search when creating an account)
CREATE INDEX idx_users_citizen_id ON users(citizen_id);

-- Index for quick role lookup
CREATE INDEX idx_roles_name ON roles(name);

-- Index for mapping users to roles efficiently
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Index for account number to make lookup faster
CREATE INDEX idx_accounts_account_number ON accounts(account_number);

-- Index for user_id in accounts (faster retrieval of user accounts)
CREATE INDEX idx_accounts_user_id ON accounts(user_id);

-- Index for created_by_teller_id in accounts (to track who created accounts)
CREATE INDEX idx_accounts_created_by_teller ON accounts(created_by_teller_id);

-- Index for account_id in transactions (to speed up querying transactions per account)
CREATE INDEX idx_transactions_account_id ON transactions(account_id);

-- Index for transaction type (to filter by type faster)
CREATE INDEX idx_transactions_type ON transactions(type);

-- Index for created_at in transactions (to query by time faster)
CREATE INDEX idx_transactions_created_at ON transactions(created_at);

-- Index for created_at in accounts (to query accounts by time)
CREATE INDEX idx_accounts_created_at ON accounts(created_at);