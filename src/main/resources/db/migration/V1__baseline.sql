-- V1 — baseline.
--
-- Spam Reaper's schema is built entirely by Flyway from here forward.
-- This first migration is intentionally empty: it gives Flyway a V1 to
-- record in flyway_schema_history so every real table lands in a later,
-- individually reviewable migration.
--
-- Do not add table definitions here. New schema goes in a new file, e.g.
--   src/main/resources/db/migration/V2__accounts.sql
SELECT 1;
