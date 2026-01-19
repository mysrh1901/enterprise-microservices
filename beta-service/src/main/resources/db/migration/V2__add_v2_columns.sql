-- Add nullable columns for V2 (backward compatible with V1)
ALTER TABLE orders
ADD COLUMN customer_email VARCHAR(255),
ADD COLUMN tax_amount DECIMAL(15, 2),
ADD COLUMN shipping_cost DECIMAL(15, 2),
ADD COLUMN payment_method VARCHAR(50),
ADD COLUMN shipping_address TEXT,
ADD COLUMN updated_at TIMESTAMP;

CREATE INDEX idx_orders_payment_method ON orders(payment_method);
