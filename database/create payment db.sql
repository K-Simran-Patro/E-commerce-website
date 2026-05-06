CREATE TABLE payments (
  payment_id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id            UUID          NOT NULL,
  gateway_payment_id  VARCHAR(100) UNIQUE,
  -- e.g. Razorpay's 'pay_abc123' — UNIQUE prevents duplicate webhook processing
  status              VARCHAR(30)  NOT NULL DEFAULT 'CREATED',
  amount              NUMERIC(12,2) NOT NULL CHECK (amount > 0),
  method              VARCHAR(20),
  -- UPI | CARD | NETBANKING | COD | WALLET
  created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_payment_status CHECK (status IN
    ('CREATED','AUTHORIZED','CAPTURED','FAILED','REFUNDED'))
);
CREATE INDEX idx_payments_order ON payments(order_id);

CREATE TABLE payment_events (
  event_id     UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  payment_id   UUID         NOT NULL REFERENCES payments(payment_id),
  event_type   VARCHAR(50)  NOT NULL,
  -- e.g. 'payment.captured', 'refund.processed'
  payload      JSONB,
  -- full raw JSON from Razorpay/Stripe — your legal proof
  received_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payment_events_payment ON payment_events(payment_id);
