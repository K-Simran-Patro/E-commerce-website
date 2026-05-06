CREATE TABLE purchase_orders (
  po_id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  po_number       VARCHAR(30)  NOT NULL UNIQUE,
  -- Human-readable: 'PO-2024-00123'
  supplier_id     UUID          NOT NULL,
  warehouse_id    UUID          NOT NULL,
  status          VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
  expected_at     TIMESTAMPTZ,
  arrived_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_po_status CHECK (status IN
    ('DRAFT','SUBMITTED','ACKNOWLEDGED','ARRIVED','CLOSED'))
);
CREATE INDEX idx_po_warehouse ON purchase_orders(warehouse_id);
CREATE INDEX idx_po_status    ON purchase_orders(status);

CREATE TABLE po_items (
  po_item_id    UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
  po_id         UUID      NOT NULL REFERENCES purchase_orders(po_id),
  item_id       UUID      NOT NULL,
  expected_qty  INTEGER   NOT NULL CHECK (expected_qty > 0),
  received_qty  INTEGER   NOT NULL DEFAULT 0,
  unit_cost     NUMERIC(12,2),
  -- cost per unit from supplier — for margin calculation
  CONSTRAINT chk_received CHECK (received_qty >= 0)
);
CREATE INDEX idx_po_items_po   ON po_items(po_id);
CREATE INDEX idx_po_items_item ON po_items(item_id);
