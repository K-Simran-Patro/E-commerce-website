CREATE TABLE items (
  item_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id    UUID          NOT NULL,
  -- logical ref to Product DB — no real FK (cross-service)
  sku           VARCHAR(100)  NOT NULL UNIQUE,
  barcode       VARCHAR(100)  UNIQUE,
  color         VARCHAR(50),
  size          VARCHAR(50),
  attributes    JSONB,
  -- flexible: {storage: "256GB", ram: "8GB"} etc.
  weight_grams  INTEGER       NOT NULL,
  is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_weight CHECK (weight_grams > 0)
);

-- Indexes
CREATE INDEX idx_items_product  ON items(product_id);
-- GIN index for querying inside the JSONB attributes field
CREATE INDEX idx_items_attrs    ON items USING GIN (attributes);
-- Partial index for active items only (used in product pages)
CREATE INDEX idx_items_active   ON items(product_id) WHERE is_active = TRUE;