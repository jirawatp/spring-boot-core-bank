-- Database schema for CoreBank application

CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          account_number VARCHAR(255) UNIQUE NOT NULL,
                          citizen_id VARCHAR(20) NOT NULL,
                          thai_name VARCHAR(255) NOT NULL,
                          english_name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          pin VARCHAR(6) NOT NULL,
                          balance DECIMAL(15, 2) DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              account_id INTEGER REFERENCES accounts(id) ON DELETE CASCADE,
                              type VARCHAR(50) NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_transaction_account ON transactions(account_id);
