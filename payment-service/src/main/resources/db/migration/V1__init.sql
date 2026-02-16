-- V1__init.sql
-- PostgreSQL schema init for Payment domain

-- 1) Sequences (explicit, Flyway-managed)
CREATE SEQUENCE IF NOT EXISTS payments_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1;

-- 2) Payment table
CREATE TABLE IF NOT EXISTS payments
(
    id             BIGINT PRIMARY KEY DEFAULT nextval('payments_id_seq'),
    order_id       BIGINT         NOT NULL UNIQUE,
    amount         NUMERIC(19, 2) NOT NULL,
    payment_status VARCHAR(50)    NOT NULL,
    payment_method VARCHAR(50)    NOT NULL
);

-- 3) Constraints
ALTER TABLE payments
    ADD CONSTRAINT payments_payment_status_chk CHECK (
        payment_status IN (
                           'PAYMENT_INITIATED',
                           'PAYMENT_SUCCEEDED',
                           'PAYMENT_FAILED',
                           'REFUNDED'
            )
        );

ALTER TABLE payments
    ADD CONSTRAINT payments_payment_method_chk CHECK (
        payment_method IN (
                           'CARD',
                           'QR',
                           'BANK_TRANSFER'
            )
        );

-- 4) Keep sequences aligned with table defaults (optional safety)
ALTER SEQUENCE payments_id_seq owned by payments.id;