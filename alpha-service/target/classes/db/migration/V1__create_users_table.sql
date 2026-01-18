-- Alpha Service - Initial Schema
-- Create users table for user management

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(active);

-- Insert sample data
INSERT INTO users (username, email, first_name, last_name, active)
VALUES
    ('john.doe', 'john.doe@enterprise.com', 'John', 'Doe', true),
    ('jane.smith', 'jane.smith@enterprise.com', 'Jane', 'Smith', true),
    ('admin', 'admin@enterprise.com', 'Admin', 'User', true);
