-- Gamma Service - Initial Schema
-- Create inventory table for product inventory management

CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE,
    quantity INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 10,
    warehouse_location VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT quantity_non_negative CHECK (quantity >= 0),
    CONSTRAINT reorder_level_non_negative CHECK (reorder_level >= 0)
);

-- Create indexes for performance
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_sku ON inventory(sku);
CREATE INDEX idx_inventory_quantity ON inventory(quantity);

-- Insert sample data
INSERT INTO inventory (product_id, product_name, sku, quantity, reorder_level, warehouse_location)
VALUES
    (1, 'Widget A', 'WGT-A-001', 100, 20, 'Warehouse-North'),
    (2, 'Widget B', 'WGT-B-002', 50, 10, 'Warehouse-South'),
    (3, 'Gadget X', 'GDT-X-003', 200, 30, 'Warehouse-East'),
    (4, 'Gadget Y', 'GDT-Y-004', 5, 15, 'Warehouse-West');  -- Below reorder level
