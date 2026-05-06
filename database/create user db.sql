CREATE TABLE users (
  user_id       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(320) NOT NULL UNIQUE,
  phone         VARCHAR(15)  UNIQUE,
  full_name     VARCHAR(200) NOT NULL,
  password_hash VARCHAR(72)  NOT NULL,
  role          VARCHAR(20)  NOT NULL DEFAULT 'customer',
  is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_role CHECK (role IN ('customer', 'admin', 'ops'))
);

-- Unique index on lowercased email (so "[email protected]" == "[email protected]")
CREATE UNIQUE INDEX idx_users_email ON users(lower(email));

-- Unique index on phone, but only for rows where phone is not null
CREATE UNIQUE INDEX idx_users_phone ON users(phone) WHERE phone IS NOT NULL;
