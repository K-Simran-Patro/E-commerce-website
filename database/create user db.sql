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




-- User Address
CREATE TABLE user_schema.user_address (
    address_id      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES user_schema.users(user_id),
    full_name       VARCHAR(200) NOT NULL,
    phone           VARCHAR(15),
    address_line    VARCHAR(300) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    pincode         VARCHAR(20)  NOT NULL,
    country         VARCHAR(100) NOT NULL DEFAULT 'India',
    is_default      BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    modified_by     VARCHAR(100)
);

-- User Session
CREATE TABLE user_schema.user_session (
    session_id      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES user_schema.users(user_id),
    token           TEXT         NOT NULL,
    device_info     VARCHAR(255),
    ip_address      VARCHAR(50),
    expires_at      TIMESTAMPTZ  NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    modified_by     VARCHAR(100)
);

-- User Wishlist
CREATE TABLE user_schema.user_wishlist (
    wishlist_id     UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES user_schema.users(user_id),
    variant_id      UUID         NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    modified_by     VARCHAR(100)
);

-- User Cart
CREATE TABLE user_schema.user_cart (
    cart_id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES user_schema.users(user_id),
    variant_id      UUID         NOT NULL,
    quantity        INT          NOT NULL DEFAULT 1,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    modified_by     VARCHAR(100)
);


ALTER TABLE user_schema.user_wishlist DROP COLUMN variant_id;
ALTER TABLE user_schema.user_wishlist ADD COLUMN variant_id BIGINT NOT NULL;

ALTER TABLE user_schema.user_cart DROP COLUMN variant_id;
ALTER TABLE user_schema.user_cart ADD COLUMN variant_id BIGINT NOT NULL;