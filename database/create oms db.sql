CREATE TABLE oms_orders (
  oms_order_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id       UUID         NOT NULL UNIQUE,
  -- 1-to-1 with order (for now). Logical ref to Order DB.
  warehouse_id   UUID         NOT NULL,
  status         VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  priority       INTEGER      NOT NULL DEFAULT 5,
  -- 1=highest urgency, 10=lowest. Affects pick queue order.
  assigned_at    TIMESTAMPTZ,
  completed_at   TIMESTAMPTZ,
  created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_oms_status CHECK (status IN
    ('PENDING','ASSIGNED','PICKING','PACKING','READY_TO_SHIP','COMPLETED','CANCELLED'))
);
CREATE INDEX idx_oms_warehouse ON oms_orders(warehouse_id);
-- Priority queue index: fastest-path to next pending order per warehouse
CREATE INDEX idx_oms_queue
  ON oms_orders(warehouse_id, priority, created_at)
  WHERE status = 'PENDING';

CREATE TABLE fulfillment_tasks (
  task_id        UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  oms_order_id   UUID         NOT NULL REFERENCES oms_orders(oms_order_id),
  task_type      VARCHAR(20) NOT NULL,
  -- 'PICK' | 'PACK' | 'SHIP'
  status         VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  assigned_to    UUID,
  -- warehouse staff user_id — nullable until assigned
  completed_at   TIMESTAMPTZ
);
CREATE INDEX idx_tasks_oms    ON fulfillment_tasks(oms_order_id);
CREATE INDEX idx_tasks_worker ON fulfillment_tasks(assigned_to) WHERE assigned_to IS NOT NULL;
