CREATE SEQUENCE order_number_seq START 1000 INCREMENT 1;

CREATE TABLE orders (
  order_id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  order_number      VARCHAR(30)   UNIQUE NOT NULL DEFAULT
    ('ORD-' || TO_CHAR(NOW(), 'YYYY' ) || '-' || LPAD(nextval('order_number_seq')::TEXT, 6, '0')),
  user_id           UUID          NOT NULL,
  status            VARCHAR(30)   NOT NULL DEFAULT 'PENDING',
  total_amount      NUMERIC(12,2) NOT NULL,
  discount_amount   NUMERIC(12,2) NOT NULL DEFAULT 0,
  delivery_address  JSONB         NOT NULL,
  coupon_code       VARCHAR(50),
  created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_order_status CHECK (status IN
    ('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','CANCELLED')),
  CONSTRAINT chk_total CHECK (total_amount >= 0)
);
CREATE INDEX idx_orders_user    ON orders(user_id);
CREATE INDEX idx_orders_status  ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at DESC);

CREATE TABLE order_items (
  order_item_id  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id       UUID          NOT NULL REFERENCES orders(order_id),
  item_id        UUID          NOT NULL,
  quantity       INTEGER       NOT NULL CHECK (quantity > 0),
  unit_price     NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0)
);
CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE order_status_history (
  history_id  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id    UUID         NOT NULL REFERENCES orders(order_id),
  status      VARCHAR(30) NOT NULL,
  changed_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  note        TEXT
);
CREATE INDEX idx_osh_order ON order_status_history(order_id, changed_at DESC);