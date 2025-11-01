-- =====================================================
-- V1__initial_schema.sql
-- Flyway Migration: Initial Schema for User Service
-- Database: gym_authentication
-- =====================================================

-- Create user_gym table (idempotent - safe for existing tables)
CREATE TABLE IF NOT EXISTS user_gym (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'TRAINER', 'CLIENT')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT,
    last_modified_by BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    
    CONSTRAINT user_gym_email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create unique constraints only if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_gym_user_name_key') THEN
        ALTER TABLE user_gym ADD CONSTRAINT user_gym_user_name_key UNIQUE (user_name);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_gym_email_key') THEN
        ALTER TABLE user_gym ADD CONSTRAINT user_gym_email_key UNIQUE (email);
    END IF;
END$$;

-- Create indexes for better query performance (only if they don't exist)
CREATE INDEX IF NOT EXISTS idx_user_gym_username ON user_gym(user_name);
CREATE INDEX IF NOT EXISTS idx_user_gym_email ON user_gym(email);
CREATE INDEX IF NOT EXISTS idx_user_gym_role ON user_gym(role);
CREATE INDEX IF NOT EXISTS idx_user_gym_enabled ON user_gym(enabled);
