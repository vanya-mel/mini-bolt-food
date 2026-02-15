-- V1__init.sql
-- PostgreSQL schema init for Order domain

-- 1) Sequences (explicit, Flyway-managed)
CREATE SEQUENCE IF NOT EXISTS orders_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1;

CREATE SEQUENCE IF NOT EXISTS order_items_id_seq
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1;

-- 2) Orders table
CREATE TABLE IF NOT EXISTS orders (
    id                  BIGINT PRIMARY KEY DEFAULT nextval('orders_id_seq'),
    customer_id         BIGINT NOT NULL,
    destination_address VARCHAR(500) NOT NULL,
    courier_name        VARCHAR(255),
    total_amount        NUMERIC(19, 2) NOT NULL,
    eta_minutes         INTEGER,
    order_status        VARCHAR(50) NOT NULL
);

-- 3) Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id                 BIGINT PRIMARY KEY DEFAULT nextval('order_items_id_seq'),
    order_id           BIGINT NOT NULL,
    item_id            BIGINT NOT NULL,
    item_name          VARCHAR(255),
    quantity           INTEGER NOT NULL,
    price_at_purchase  NUMERIC(19, 2) NOT NULL
);

-- 4) Constraints
ALTER TABLE orders
    ADD CONSTRAINT orders_order_status_chk CHECK (
        order_status IN (
                         'PENDING_PAYMENT',
                         'PAID',
                         'PAYMENT_FAILED',
                         'PENDING_DELIVERY',
                         'DELIVERED'
            )
        );

ALTER TABLE order_items
    ADD CONSTRAINT order_items_order_fk
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE;

-- 5) Indexes
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);

-- 6) Keep sequences aligned with table defaults (optional safety)
ALTER SEQUENCE orders_id_seq OWNED BY orders.id;
ALTER SEQUENCE order_items_id_seq OWNED BY order_items.id;