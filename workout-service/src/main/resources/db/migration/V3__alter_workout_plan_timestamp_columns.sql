-- V3__alter_workout_plan_timestamp_columns.sql
-- Workout Service - Fix timestamp columns
-- Description: Convert created_at and updated_at from VARCHAR to TIMESTAMP

-- Alter created_at column type from VARCHAR to TIMESTAMP
ALTER TABLE workout_plan 
ALTER COLUMN created_at TYPE TIMESTAMP WITHOUT TIME ZONE 
USING COALESCE(
    TO_TIMESTAMP(created_at, 'YYYY-MM-DD:HH24:MI:SS'),
    CURRENT_TIMESTAMP
);

-- Alter updated_at column type from VARCHAR to TIMESTAMP
ALTER TABLE workout_plan 
ALTER COLUMN updated_at TYPE TIMESTAMP WITHOUT TIME ZONE 
USING COALESCE(
    TO_TIMESTAMP(updated_at, 'YYYY-MM-DD:HH24:MI:SS'),
    CURRENT_TIMESTAMP
);
