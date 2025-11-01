-- V1__initial_schema.sql
-- Workout Service - Initial Schema
-- Database: gym_exercise
-- Description: Creates all tables for workout management system

-- =====================================================
-- TABLE 1: muscular_groups
-- =====================================================
CREATE TABLE IF NOT EXISTS muscular_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- =====================================================
-- TABLE 2: workout
-- =====================================================
CREATE TABLE IF NOT EXISTS workout (
    id_workout BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    muscular_group BIGINT NOT NULL,
    muscular_load VARCHAR(50) NOT NULL,
    CONSTRAINT fk_workout_muscular_group FOREIGN KEY (muscular_group) 
        REFERENCES muscular_groups(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE 3: workout_specification
-- =====================================================
CREATE TABLE IF NOT EXISTS workout_specification (
    id_workout_specification BIGSERIAL PRIMARY KEY,
    description TEXT,
    reps_number INTEGER,
    sets_number INTEGER NOT NULL,
    recommended_weight DOUBLE PRECISION NOT NULL,
    trainer_rating DOUBLE PRECISION NOT NULL,
    is_time_based BOOLEAN NOT NULL DEFAULT FALSE,
    time INTEGER,
    id_workout BIGINT NOT NULL,
    CONSTRAINT fk_workout_specification_workout FOREIGN KEY (id_workout) 
        REFERENCES workout(id_workout) ON DELETE CASCADE
);

-- =====================================================
-- TABLE 4: daily_routine
-- =====================================================
CREATE TABLE IF NOT EXISTS daily_routine (
    id_daily_routine BIGSERIAL PRIMARY KEY
);

-- =====================================================
-- TABLE 5: daily_routine_days (ElementCollection)
-- =====================================================
CREATE TABLE IF NOT EXISTS daily_routine_days (
    daily_routine_id_daily_routine BIGINT NOT NULL,
    days VARCHAR(20) NOT NULL,
    CONSTRAINT fk_daily_routine_days_routine FOREIGN KEY (daily_routine_id_daily_routine) 
        REFERENCES daily_routine(id_daily_routine) ON DELETE CASCADE
);

-- =====================================================
-- TABLE 6: workout_plan
-- =====================================================
CREATE TABLE IF NOT EXISTS workout_plan (
    id_workout_plan BIGSERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL,
    id_trainer BIGINT NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    start_date VARCHAR(50) NOT NULL,
    end_date VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    is_template BOOLEAN NOT NULL DEFAULT FALSE
);

-- =====================================================
-- TABLE 7: workout_specification_daily_routine (Join Table)
-- =====================================================
CREATE TABLE IF NOT EXISTS workout_specification_daily_routine (
    id_workout_specification BIGINT NOT NULL,
    id_daily_routine BIGINT NOT NULL,
    PRIMARY KEY (id_workout_specification, id_daily_routine),
    CONSTRAINT fk_ws_dr_workout_specification FOREIGN KEY (id_workout_specification) 
        REFERENCES workout_specification(id_workout_specification) ON DELETE CASCADE,
    CONSTRAINT fk_ws_dr_daily_routine FOREIGN KEY (id_daily_routine) 
        REFERENCES daily_routine(id_daily_routine) ON DELETE CASCADE
);

-- =====================================================
-- TABLE 8: daily_routine_workout_plan (Join Table)
-- =====================================================
CREATE TABLE IF NOT EXISTS daily_routine_workout_plan (
    id_daily_routine BIGINT NOT NULL,
    id_workout_plan BIGINT NOT NULL,
    PRIMARY KEY (id_daily_routine, id_workout_plan),
    CONSTRAINT fk_dr_wp_daily_routine FOREIGN KEY (id_daily_routine) 
        REFERENCES daily_routine(id_daily_routine) ON DELETE CASCADE,
    CONSTRAINT fk_dr_wp_workout_plan FOREIGN KEY (id_workout_plan) 
        REFERENCES workout_plan(id_workout_plan) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES for better query performance
-- =====================================================

-- Index on workout by muscular_group
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_workout_muscular_group') THEN
        CREATE INDEX idx_workout_muscular_group ON workout(muscular_group);
    END IF;
END $$;

-- Index on workout_specification by id_workout
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_workout_spec_workout') THEN
        CREATE INDEX idx_workout_spec_workout ON workout_specification(id_workout);
    END IF;
END $$;

-- Index on workout_plan by user and trainer
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_workout_plan_user') THEN
        CREATE INDEX idx_workout_plan_user ON workout_plan(id_user);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_workout_plan_trainer') THEN
        CREATE INDEX idx_workout_plan_trainer ON workout_plan(id_trainer);
    END IF;
END $$;

-- Index on workout_plan by status
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_workout_plan_status') THEN
        CREATE INDEX idx_workout_plan_status ON workout_plan(status);
    END IF;
END $$;
