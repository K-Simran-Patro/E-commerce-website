CREATE TABLE base_prices (
  price_id      UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  item_id       UUID          NOT NULL UNIQUE,
  -- One base price record per item (SKU). UNIQUE enforces this.
  mrp           NUMERIC(12,2) NOT NULL,
  selling_price NUMERIC(12,2) NOT NULL,
  updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_price CHECK (
    mrp > 0 AND selling_price > 0 AND selling_price <= mrp
  )
  -- Legally: selling price cannot exceed MRP in India
);

CREATE TABLE price_rules (
  rule_id        UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  item_id        UUID,
  -- Target one item OR...
  category_id    UUID,
  -- ...a whole category (set only one, not both)
  discount_pct   NUMERIC(5,2)  NOT NULL,
  valid_from     TIMESTAMPTZ   NOT NULL,
  valid_until    TIMESTAMPTZ,
  is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_rule_target CHECK (
    (item_id IS NOT NULL AND category_id IS NULL) OR
    (item_id IS NULL AND category_id IS NOT NULL)
  ),
  CONSTRAINT chk_discount_pct CHECK (discount_pct > 0 AND discount_pct <= 100)
);
CREATE INDEX idx_rules_active ON price_rules(item_id, category_id) WHERE is_active = TRUE;

CREATE TABLE coupons (
  coupon_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  code            VARCHAR(30)  NOT NULL UNIQUE,
  discount_type   VARCHAR(20)  NOT NULL,
  discount_value  NUMERIC(10,2) NOT NULL CHECK (discount_value > 0),
  min_order_value NUMERIC(12,2) NOT NULL DEFAULT 0,
  max_uses        INTEGER,
  per_user_limit  INTEGER       NOT NULL DEFAULT 1,
  valid_from      TIMESTAMPTZ   NOT NULL,
  valid_until     TIMESTAMPTZ,
  CONSTRAINT chk_discount_type CHECK (discount_type IN ('PERCENTAGE','FLAT','FREE_SHIPPING'))
);
