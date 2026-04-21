--liquibase formatted sql

--changeset author:booking-service-schema-4
ALTER TABLE hotels ADD COLUMN owner_id BIGINT;
ALTER TABLE hotels ADD CONSTRAINT fk_hotels_owner FOREIGN KEY (owner_id) REFERENCES users(id);
CREATE INDEX idx_hotels_owner_id ON hotels(owner_id);

-- Assign existing hotels to the first available OWNER user for backward compatibility
UPDATE hotels
SET owner_id = (
    SELECT id
    FROM users
    WHERE role = 'OWNER'
    ORDER BY id
    LIMIT 1
)
WHERE owner_id IS NULL;
