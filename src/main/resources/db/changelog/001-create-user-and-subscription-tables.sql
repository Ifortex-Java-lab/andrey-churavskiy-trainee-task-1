CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       email VARCHAR(255) NOT NULL UNIQUE,
       customer_id VARCHAR(255) UNIQUE
);

CREATE TYPE subscription_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'TRIALING',
    'PAST_DUE',
    'CANCELED'
    );

CREATE TABLE subscriptions (
       id SERIAL PRIMARY KEY,
       user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
       price_id VARCHAR(255) NOT NULL,
       status subscription_status NOT NULL,
       current_period_start TIMESTAMP,
       current_period_end TIMESTAMP,
       stripe_subscription_id VARCHAR(255)
);