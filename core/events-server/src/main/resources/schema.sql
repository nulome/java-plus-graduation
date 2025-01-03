
create TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    category_id BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_lat NUMERIC(10, 6) NOT NULL,
    location_lon NUMERIC(10, 6) NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit integer NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(30) NOT NULL,
    title VARCHAR(120) NOT NULL
);

