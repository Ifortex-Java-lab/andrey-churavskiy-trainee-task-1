CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       email VARCHAR(255) NOT NULL UNIQUE,
       customer_id VARCHAR(255) UNIQUE
);

CREATE TABLE subscriptions (
       id SERIAL PRIMARY KEY,
       user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
       price_id VARCHAR(255) NOT NULL,
       product_id VARCHAR(255) NOT NULL,
       status VARCHAR(20) NOT NULL,
       current_period_start TIMESTAMP,
       current_period_end TIMESTAMP,
       stripe_subscription_id VARCHAR(255)
);