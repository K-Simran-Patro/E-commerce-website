CREATE TABLE returns (
  return_id     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id      UUID          NOT NULL,
  user_id       UUID          NOT NULL,
  reason        VARCHAR(100) NOT NULL,
  -- 'WRONG_ITEM' | 'DAMAGED' | 'CHANGED_MIND' | 'DEFECTIVE'
  status        VARCHAR(30)  NOT NULL DEFAULT 'REQUESTED',
  refund_amount NUMERIC(12,2),
  -- NULL until inspection complete — can be partial refund
  created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  resolved_at   TIMESTAMPTZ,
  CONSTRAINT chk_return_status CHECK (status IN (
    'REQUESTED','APPROVED','PICKUP_SCHEDULED','ITEM_RECEIVED',
    'INSPECTED','REFUND_INITIATED','COMPLETED','REJECTED'))
);
CREATE INDEX idx_returns_order ON returns(order_id);
CREATE INDEX idx_returns_user  ON returns(user_id);
-- Partial index: open returns sorted oldest-first for ops dashboard
CREATE INDEX idx_returns_open
  ON returns(created_at ASC)
  WHERE status NOT IN ('COMPLETED','REJECTED');

CREATE TABLE return_items (
  return_item_id    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  return_id         UUID         NOT NULL REFERENCES returns(return_id),
  order_item_id     UUID         NOT NULL,
  qty               INTEGER      NOT NULL DEFAULT 1 CHECK (qty > 0),
  condition         VARCHAR(30),
  -- 'GOOD' | 'DAMAGED' | 'MISSING_PARTS' — set after inspection
  inspection_notes  TEXT
);
CREATE INDEX idx_ri_return ON return_items(return_id);
