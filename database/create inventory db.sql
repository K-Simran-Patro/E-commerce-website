CREATE TABLE inventory (
  inventory_id   UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
  item_id        UUID      NOT NULL UNIQUE,
  -- One inventory row per item. UNIQUE enforces this.
  total_qty      INTEGER   NOT NULL DEFAULT 0,
  reserved_qty   INTEGER   NOT NULL DEFAULT 0,
  available_qty  INTEGER   GENERATED ALWAYS AS (total_qty - reserved_qty) STORED,
  -- Auto-computed: can't be set manually. Always accurate.
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_qty CHECK (total_qty >= 0 AND reserved_qty >= 0 AND reserved_qty <= total_qty)
);
CREATE TABLE inventory_movements (
  movement_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  item_id       UUID         NOT NULL,
  delta         INTEGER      NOT NULL,
  -- positive = stock added, negative = stock removed
  reason        VARCHAR(50) NOT NULL,
  -- 'INBOUND' | 'ORDER_RESERVED' | 'ORDER_CANCELLED' | 'DAMAGED'
  reference_id  UUID,
  -- order_id or inbound_shipment_id for traceability
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_inv_movements_item ON inventory_movements(item_id, created_at DESC);
