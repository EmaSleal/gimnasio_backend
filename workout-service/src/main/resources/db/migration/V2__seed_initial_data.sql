-- V2__seed_initial_data.sql
-- Workout Service - Seed Initial Data
-- Description: Inserts test data for development and testing

-- =====================================================
-- SEED DATA: muscular_groups
-- =====================================================
INSERT INTO muscular_groups (id, name) VALUES 
    (1, 'Pecho'),
    (2, 'Espalda'),
    (3, 'Piernas'),
    (4, 'Hombros'),
    (5, 'Brazos'),
    (6, 'Core')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence for muscular_groups
SELECT setval('muscular_groups_id_seq', (SELECT MAX(id) FROM muscular_groups));

-- =====================================================
-- SEED DATA: workout
-- =====================================================
INSERT INTO workout (id_workout, name, muscular_group, muscular_load) VALUES 
    (1, 'Press de Banca', 1, 'HIGH'),
    (2, 'Flexiones', 1, 'MEDIUM'),
    (3, 'Dominadas', 2, 'HIGH'),
    (4, 'Remo con Barra', 2, 'HIGH'),
    (5, 'Sentadillas', 3, 'HIGH'),
    (6, 'Press Militar', 4, 'MEDIUM'),
    (7, 'Curl de Bíceps', 5, 'LOW'),
    (8, 'Plancha', 6, 'MEDIUM')
ON CONFLICT (id_workout) DO NOTHING;

-- Reset sequence for workout
SELECT setval('workout_id_workout_seq', (SELECT MAX(id_workout) FROM workout));

-- =====================================================
-- SEED DATA: workout_specification
-- =====================================================
INSERT INTO workout_specification 
    (id_workout_specification, description, reps_number, sets_number, recommended_weight, trainer_rating, is_time_based, time, id_workout) 
VALUES 
    (1, 'Press de banca básico con barra', 10, 3, 60.0, 4.5, false, null, 1),
    (2, 'Flexiones estándar', 15, 3, 0.0, 4.0, false, null, 2),
    (3, 'Dominadas con peso corporal', 8, 3, 0.0, 5.0, false, null, 3),
    (4, 'Remo con barra inclinado', 12, 4, 50.0, 4.5, false, null, 4),
    (5, 'Sentadillas con barra', 10, 4, 80.0, 5.0, false, null, 5),
    (6, 'Press militar con mancuernas', 10, 3, 20.0, 4.0, false, null, 6),
    (7, 'Curl de bíceps con mancuernas', 12, 3, 10.0, 3.5, false, null, 7),
    (8, 'Plancha isométrica', 0, 3, 0.0, 4.0, true, 60, 8)
ON CONFLICT (id_workout_specification) DO NOTHING;

-- Reset sequence for workout_specification
SELECT setval('workout_specification_id_workout_specification_seq', (SELECT MAX(id_workout_specification) FROM workout_specification));

-- =====================================================
-- SEED DATA: daily_routine
-- =====================================================
INSERT INTO daily_routine (id_daily_routine) VALUES 
    (1),
    (2),
    (3)
ON CONFLICT (id_daily_routine) DO NOTHING;

-- Reset sequence for daily_routine
SELECT setval('daily_routine_id_daily_routine_seq', (SELECT MAX(id_daily_routine) FROM daily_routine));

-- =====================================================
-- SEED DATA: daily_routine_days (ElementCollection)
-- =====================================================
INSERT INTO daily_routine_days (daily_routine_id_daily_routine, days) VALUES 
    (1, 'MONDAY'),
    (1, 'WEDNESDAY'),
    (1, 'FRIDAY'),
    (2, 'TUESDAY'),
    (2, 'THURSDAY'),
    (3, 'SATURDAY')
ON CONFLICT DO NOTHING;

-- =====================================================
-- SEED DATA: workout_plan
-- =====================================================
-- NOTE: Assuming id_user=1 (admin), id_trainer=2 (trainer1) from user-service
INSERT INTO workout_plan 
    (id_workout_plan, id_user, id_trainer, description, status, start_date, end_date, created_at, updated_at, is_template) 
VALUES 
    (1, 3, 2, 'Plan de fuerza básico', 'ACTIVE', '2024-11-01', '2024-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    (2, 3, 2, 'Plan de hipertrofia', 'DRAFT', '2024-12-01', '2025-02-28', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    (3, 2, 2, 'Template: Rutina Full Body', 'ACTIVE', '2024-01-01', '2024-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true)
ON CONFLICT (id_workout_plan) DO NOTHING;

-- Reset sequence for workout_plan
SELECT setval('workout_plan_id_workout_plan_seq', (SELECT MAX(id_workout_plan) FROM workout_plan));

-- =====================================================
-- SEED DATA: workout_specification_daily_routine (Join Table)
-- =====================================================
INSERT INTO workout_specification_daily_routine (id_workout_specification, id_daily_routine) VALUES 
    (1, 1), -- Press de banca en rutina 1
    (2, 1), -- Flexiones en rutina 1
    (3, 2), -- Dominadas en rutina 2
    (4, 2), -- Remo en rutina 2
    (5, 3), -- Sentadillas en rutina 3
    (6, 3)  -- Press militar en rutina 3
ON CONFLICT DO NOTHING;

-- =====================================================
-- SEED DATA: daily_routine_workout_plan (Join Table)
-- =====================================================
INSERT INTO daily_routine_workout_plan (id_daily_routine, id_workout_plan) VALUES 
    (1, 1), -- Rutina 1 en Plan 1
    (2, 1), -- Rutina 2 en Plan 1
    (3, 2), -- Rutina 3 en Plan 2
    (1, 3), -- Rutina 1 en Template
    (2, 3), -- Rutina 2 en Template
    (3, 3)  -- Rutina 3 en Template
ON CONFLICT DO NOTHING;
