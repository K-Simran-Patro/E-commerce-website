CREATE TABLE wishlist_items (
  wishlist_item_id  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID           NOT NULL,
  item_id          UUID           NOT NULL,
  target_price     NUMERIC(12,2),
  last_notified_at TIMESTAMPTZ,
  is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
  added_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  CONSTRAINT uq_user_item UNIQUE(user_id, item_id)
);

CREATE INDEX idx_wishlist_alert
  ON wishlist_items(item_id, target_price)
  WHERE is_active = TRUE AND target_price IS NOT NULL;
