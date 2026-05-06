CREATE TABLE shipments (
  shipment_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id          UUID          NOT NULL,
  courier_partner   VARCHAR(100) NOT NULL,
  -- 'Delhivery', 'BlueDart', 'Ekart', 'DTDC'
  tracking_number   VARCHAR(100) UNIQUE,
  -- AWB number from courier — UNIQUE per courier
  status            VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
  warehouse_id      UUID          NOT NULL,
  shipped_at        TIMESTAMPTZ,
  expected_delivery TIMESTAMPTZ,
  delivered_at      TIMESTAMPTZ,
  created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_dates CHECK (delivered_at IS NULL OR delivered_at >= shipped_at)
);
CREATE INDEX idx_shipments_order    ON shipments(order_id);
CREATE INDEX idx_shipments_tracking ON shipments(tracking_number);
-- Index for SLA breach monitoring: undelivered past expected date
CREATE INDEX idx_shipments_delayed
  ON shipments(expected_delivery)
  WHERE delivered_at IS NULL;

CREATE TABLE tracking_events (
  event_id     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  shipment_id  UUID          NOT NULL REFERENCES shipments(shipment_id),
  status       VARCHAR(50)   NOT NULL,
  -- 'PICKED_UP' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED'
  location     VARCHAR(200),
  event_time   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  description  TEXT
);
CREATE INDEX idx_tracking_shipment ON tracking_events(shipment_id, event_time DESC);
