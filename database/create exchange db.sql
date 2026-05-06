CREATE TABLE exchanges (
  exchange_id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  original_order_item_id  UUID          NOT NULL,
  -- Which order line item the customer is returning
  replacement_item_id     UUID          NOT NULL,
  -- The new variant they want (different size/colour)
  user_id                 UUID          NOT NULL,
  status                  VARCHAR(30)  NOT NULL DEFAULT 'REQUESTED',
  price_difference        NUMERIC(12,2) NOT NULL DEFAULT 0,
  -- +ve = user pays extra; -ve = we owe user credit; 0 = same price
  reason                  VARCHAR(200),
  created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_exc_status CHECK (status IN (
    'REQUESTED','APPROVED','ORIGINAL_RECEIVED','INSPECTION_PASSED',
    'NEW_SHIPPED','COMPLETED','REJECTED'))
);
CREATE INDEX idx_exchanges_user   ON exchanges(user_id);
CREATE INDEX idx_exchanges_status ON exchanges(status);

CREATE TABLE exchange_status_history (
  history_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  exchange_id  UUID         NOT NULL REFERENCES exchanges(exchange_id),
  status       VARCHAR(30) NOT NULL,
  changed_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  note         TEXT
);
CREATE INDEX idx_esh_exchange ON exchange_status_history(exchange_id, changed_at DESC)
