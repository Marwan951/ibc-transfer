-- This file seeds the database with sample users and accounts
-- Passwords are BCrypt hashed:
--   alice123  -> $2a$10$...
--   bob456    -> $2a$10$...
--   carol789  -> $2a$10$...

INSERT INTO users (username, password, email, full_name) VALUES
('alice', '$2a$10$8alg87.nE/TWpj7KTrVKDOFZc.KCm77SQJV//4N0Ptn8NBzkRV.sy', 'alice@example.com', 'Alice Johnson'),
('zain' , '$2a$10$8alg87.nE/TWpj7KTrVKDOFZc.KCm77SQJV//4N0Ptn8NBzkRV.sy', 'zain@example.com', 'Zain Ayman'),
('marwan', '$2a$10$8alg87.nE/TWpj7KTrVKDOFZc.KCm77SQJV//4N0Ptn8NBzkRV.sy', 'marwan@example.com', 'Marwan Salah');

INSERT INTO accounts (account_number, balance, user_id) VALUES
('ACC-1001', 5000.00, 1),
('ACC-1002', 3500.00, 2),
('ACC-1003', 7200.00, 3);