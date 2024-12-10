
create TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(250) NOT NULL
);

create TABLE IF NOT EXISTS followers (
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    CONSTRAINT followers_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON delete CASCADE,
    CONSTRAINT followers_followed FOREIGN KEY (followed_id) REFERENCES users(id) ON delete CASCADE
);

create TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat NUMERIC(10, 6) NOT NULL,
    lon NUMERIC(10, 6) NOT NULL
);

create TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    category_id BIGINT REFERENCES categories (id) ON delete RESTRICT ON update CASCADE,
    created_on TIMESTAMP NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT REFERENCES users (id) ON delete CASCADE ON update CASCADE,
    location_id BIGINT REFERENCES locations (id) ON delete CASCADE ON update CASCADE,
    paid BOOLEAN NOT NULL,
    participant_limit integer NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(30) NOT NULL,
    title VARCHAR(120) NOT NULL
);

create TABLE IF NOT EXISTS compilation (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  pinned boolean NOT NULL,
  title varchar(50) NOT NULL
);

create TABLE IF NOT EXISTS compilation_event (
  compilation_id integer NOT NULL,
  event_id integer NOT NULL,
  CONSTRAINT compilation_event_compilation FOREIGN KEY (compilation_id) REFERENCES compilation(id) ON delete CASCADE,
  CONSTRAINT compilation_event_event FOREIGN KEY (event_id) REFERENCES events(id) ON delete CASCADE
);

create TABLE IF NOT EXISTS user_request (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  created TIMESTAMP NOT NULL,
  event_id integer NOT NULL,
  requester_id integer NOT NULL,
  status varchar(50) NOT NULL,
  UNIQUE (event_id, requester_id),
  CONSTRAINT user_request_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON delete CASCADE,
  CONSTRAINT user_request_event FOREIGN KEY (event_id) REFERENCES events(id) ON delete CASCADE
);
