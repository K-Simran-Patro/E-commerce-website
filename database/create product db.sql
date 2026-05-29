CREATE TABLE categories (
  category_id SERIAL PRIMARY KEY,
  parent_id   INT REFERENCES categories(category_id),
  name        VARCHAR(100) NOT NULL,
  slug        VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE products (
  product_id      SERIAL PRIMARY KEY,
  category_id     INT NOT NULL REFERENCES categories(category_id),
  brand_id        INT,
  name            VARCHAR(500) NOT NULL,
  description     TEXT,
  main_image_key  VARCHAR(500),
  status          VARCHAR(20) DEFAULT 'active',
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_variants (
  variant_id SERIAL PRIMARY KEY,
  product_id INT NOT NULL REFERENCES products(product_id),
  sku        VARCHAR(100) UNIQUE NOT NULL,
  color      VARCHAR(50),
  size       VARCHAR(50),
  price      NUMERIC(10,2),
  is_active  BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE product_variants
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN modified_at TIMESTAMP,
ADD COLUMN modified_by VARCHAR(100);

ALTER TABLE products 
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN modified_at TIMESTAMP,
ADD COLUMN modified_by VARCHAR(100);

ALTER TABLE products ADD COLUMN brand_name VARCHAR(100);
ALTER TABLE products DROP COLUMN brand_id;