-- V3__alter_workout_plan_timestamp_columns.sql
-- Workout Service - Fix timestamp columns
-- Description: Convert created_at and updated_at from VARCHAR to TIMESTAMP

-- Alter created_at column type from VARCHAR to TIMESTAMP
ALTER TABLE workout_plan 
ALTER COLUMN created_at TYPE TIMESTAMP WITHOUT TIME ZONE 
USING CASE 
    WHEN created_at IS NULL THEN NULL
    WHEN created_at ~ '^\d{4}-\d{2}-\d{2}:\d{2}:\d{2}:\d{2}$' THEN 
        TO_TIMESTAMP(created_at, 'YYYY-MM-DD:HH24:MI:SS')
    ELSE CURRENT_TIMESTAMP
END;

-- Alter updated_at column type from VARCHAR to TIMESTAMP
ALTER TABLE workout_plan 
ALTER COLUMN updated_at TYPE TIMESTAMP WITHOUT TIME ZONE 
USING CASE 
    WHEN updated_at IS NULL THEN NULL
    WHEN updated_at ~ '^\d{4}-\d{2}-\d{2}:\d{2}:\d{2}:\d{2}$' THEN 
        TO_TIMESTAMP(updated_at, 'YYYY-MM-DD:HH24:MI:SS')
    ELSE CURRENT_TIMESTAMP
END;
