CREATE TABLE categories (
  category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  parent_id   UUID REFERENCES categories(category_id),
  name        VARCHAR(100) NOT NULL,
  slug        VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE products (
  product_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  category_id     UUID NOT NULL REFERENCES categories(category_id),
  brand_id        UUID,
  name            VARCHAR(500) NOT NULL,
  description     TEXT,
  main_image_key  VARCHAR(500),
  status          VARCHAR(20) DEFAULT 'active',
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_variants (
  variant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL REFERENCES products(product_id),
  sku        VARCHAR(100) UNIQUE NOT NULL,
  color      VARCHAR(50),
  size       VARCHAR(50),
  price      NUMERIC(10,2),
  is_active  BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);