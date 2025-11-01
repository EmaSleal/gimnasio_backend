-- =====================================================
-- RESET_FLYWAY.sql
-- Script para resetear Flyway y permitir que recree el esquema
-- =====================================================
-- INSTRUCCIONES:
-- Ejecuta este script en la base de datos gym_authentication
-- Puedes usar pgAdmin, DBeaver, o cualquier cliente PostgreSQL
--
-- Comando desde línea de comandos (si tienes psql):
-- psql -h 192.168.100.207 -U postgres -d gym_authentication -f RESET_FLYWAY.sql
-- =====================================================

-- 1. Eliminar la tabla flyway_schema_history
DROP TABLE IF EXISTS flyway_schema_history CASCADE;

-- 2. Eliminar la tabla user_gym (para que Flyway la recree desde cero)
DROP TABLE IF EXISTS user_gym CASCADE;

-- Listo! Ahora puedes arrancar el servicio y Flyway recreará todo desde cero
