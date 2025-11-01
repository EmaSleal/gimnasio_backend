-- =====================================================
-- V2__seed_initial_data.sql
-- Flyway Migration: Seed Initial Data for User Service
-- Database: gym_authentication
-- =====================================================

-- Insert default admin user
-- Password: admin123 (BCrypt hash: $2a$10$X5wFWHkB...)
-- NOTE: Change this password in production!
INSERT INTO user_gym (
    user_name, 
    password, 
    email, 
    role, 
    enabled, 
    account_non_expired, 
    credentials_non_expired, 
    account_non_locked,
    created_at
) VALUES (
    'admin',
    '$2a$10$X5wFWHkB7P9yjus3VxJxH.ZGj7RfZr5Y9XzN8WzGJQqKxGxGVxGxG', -- admin123
    'admin@gym.com',
    'ADMIN',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    TO_CHAR(NOW(), 'YYYY-MM-DD:HH24:MI:SS')
) ON CONFLICT (user_name) DO NOTHING;

-- Insert sample trainer user
INSERT INTO user_gym (
    user_name, 
    password, 
    email, 
    role, 
    enabled, 
    account_non_expired, 
    credentials_non_expired, 
    account_non_locked,
    created_at
) VALUES (
    'trainer1',
    '$2a$10$X5wFWHkB7P9yjus3VxJxH.ZGj7RfZr5Y9XzN8WzGJQqKxGxGVxGxG', -- trainer123
    'trainer@gym.com',
    'TRAINER',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    TO_CHAR(NOW(), 'YYYY-MM-DD:HH24:MI:SS')
) ON CONFLICT (user_name) DO NOTHING;

-- Insert sample client user
INSERT INTO user_gym (
    user_name, 
    password, 
    email, 
    role, 
    enabled, 
    account_non_expired, 
    credentials_non_expired, 
    account_non_locked,
    created_at
) VALUES (
    'client1',
    '$2a$10$X5wFWHkB7P9yjus3VxJxH.ZGj7RfZr5Y9XzN8WzGJQqKxGxGVxGxG', -- client123
    'client@gym.com',
    'CLIENT',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    TO_CHAR(NOW(), 'YYYY-MM-DD:HH24:MI:SS')
) ON CONFLICT (user_name) DO NOTHING;
