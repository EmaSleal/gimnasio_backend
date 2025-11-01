-- =====================================================
-- V1__initial_schema.sql
-- Flyway Migration: Initial Schema for User Service
-- Database: gym_authentication
-- =====================================================

-- Create user_gym table
CREATE TABLE IF NOT EXISTS user_gym (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'TRAINER', 'CLIENT')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT,
    last_modified_by BIGINT,
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    
    CONSTRAINT user_gym_email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for better query performance
CREATE INDEX idx_user_gym_username ON user_gym(user_name);
CREATE INDEX idx_user_gym_email ON user_gym(email);
CREATE INDEX idx_user_gym_role ON user_gym(role);
CREATE INDEX idx_user_gym_enabled ON user_gym(enabled);

-- Add comments for documentation
COMMENT ON TABLE user_gym IS 'User accounts for gym management system';
COMMENT ON COLUMN user_gym.id IS 'Primary key, auto-generated';
COMMENT ON COLUMN user_gym.user_name IS 'Unique username for authentication';
COMMENT ON COLUMN user_gym.password IS 'BCrypt hashed password';
COMMENT ON COLUMN user_gym.email IS 'User email address, must be unique';
COMMENT ON COLUMN user_gym.role IS 'User role: ADMIN, TRAINER, or CLIENT';
COMMENT ON COLUMN user_gym.enabled IS 'Account enabled status';
COMMENT ON COLUMN user_gym.account_non_expired IS 'Account expiration status';
COMMENT ON COLUMN user_gym.credentials_non_expired IS 'Credentials expiration status';
COMMENT ON COLUMN user_gym.account_non_locked IS 'Account lock status';
COMMENT ON COLUMN user_gym.created_by IS 'User ID who created this record';
COMMENT ON COLUMN user_gym.last_modified_by IS 'User ID who last modified this record';
COMMENT ON COLUMN user_gym.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN user_gym.updated_at IS 'Timestamp when record was last updated';
