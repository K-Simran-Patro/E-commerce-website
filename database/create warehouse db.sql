CREATE TABLE warehouses (
  warehouse_id   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
  name           VARCHAR(200) NOT NULL,
  city           VARCHAR(100) NOT NULL,
  state          VARCHAR(100) NOT NULL,
  pincode        CHAR(6)       NOT NULL,
  is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_pincode CHECK (pincode ~ '^[0-9]{6}$')
);

CREATE TABLE warehouse_locations (
  location_id    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
  warehouse_id   UUID        NOT NULL REFERENCES warehouses(warehouse_id),
  aisle          VARCHAR(10)  NOT NULL,
  -- e.g. 'A', 'B', 'C12'
  rack           VARCHAR(10)  NOT NULL,
  -- e.g. '01', '02'
  bin            VARCHAR(10)  NOT NULL,
  -- e.g. '001', '002' — the exact shelf slot
  item_id        UUID,
  -- NULL if bin is empty; set when item is putaway here
  qty            INTEGER     NOT NULL DEFAULT 0,
  CONSTRAINT uq_slot UNIQUE (warehouse_id, aisle, rack, bin),
  CONSTRAINT chk_qty  CHECK (qty >= 0)
);

CREATE INDEX idx_wl_warehouse ON warehouse_locations(warehouse_id);
CREATE INDEX idx_wl_item      ON warehouse_locations(item_id) WHERE item_id IS NOT NULL;