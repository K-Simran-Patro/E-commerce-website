CREATE TABLE user_schema.users (
  user_id       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(320) NOT NULL UNIQUE,
  phone         VARCHAR(15)  UNIQUE,
  full_name     VARCHAR(200) NOT NULL,
  password_hash VARCHAR(60)  NOT NULL,
  role          VARCHAR(20)  NOT NULL DEFAULT 'customer',
  is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  created_by    VARCHAR(100),
  modified_by   VARCHAR(100),
  CONSTRAINT chk_role CHECK (role IN ('customer', 'admin', 'ops'))
);

CREATE UNIQUE INDEX idx_users_email ON user_schema.users(lower(email));
CREATE UNIQUE INDEX idx_users_phone ON user_schema.users(phone) WHERE phone IS NOT NULL;