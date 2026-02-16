-- V1__init.sql
-- PostgreSQL schema init for Delivery domain

-- 1) Sequences (explicit, Flyway-managed)
CREATE SEQUENCE IF NOT EXISTS deliveries_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1;

-- 2) Deliveries table
CREATE TABLE IF NOT EXISTS deliveries
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('deliveries_id_seq'),
    order_id     BIGINT       NOT NULL UNIQUE,
    courier_name VARCHAR(255) NOT NULL,
    eta_minutes  INTEGER      NOT NULL
);

-- 3) Keep sequences aligned with table defaults (optional safety)
ALTER SEQUENCE deliveries_id_seq owned by deliveries.id;