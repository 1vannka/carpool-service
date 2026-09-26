CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       telegram_alias VARCHAR(100),
                       vk_alias VARCHAR(100),
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE offices (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         city VARCHAR(100) NOT NULL,
                         address VARCHAR(255) NOT NULL,
                         location GEOMETRY(Point, 4326) NOT NULL
);
CREATE INDEX idx_offices_location ON offices USING GIST (location);

CREATE TABLE trips (
                       id BIGSERIAL PRIMARY KEY,
                       driver_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       office_id BIGINT NOT NULL REFERENCES offices(id) ON DELETE CASCADE,
                       departure_time TIMESTAMPTZ NOT NULL,
                       estimated_duration INTEGER NOT NULL,
                       total_seats INTEGER NOT NULL,
                       available_seats INTEGER NOT NULL,
                       car_model VARCHAR(100) NOT NULL,
                       car_color VARCHAR(50) NOT NULL,
                       car_plate VARCHAR(20) NOT NULL,
                       route_path GEOMETRY(LineString, 4326) NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0,
                       status VARCHAR(50) NOT NULL,
                       start_location GEOMETRY(Point, 4326) NOT NULL
);
CREATE INDEX idx_trips_route_path ON trips USING GIST (route_path);

CREATE TABLE trip_passengers (
                                 trip_id BIGINT NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
                                 passenger_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 status VARCHAR(50) NOT NULL DEFAULT 'WAITING_APPROVAL',
                                 PRIMARY KEY (trip_id, passenger_id)
);

CREATE TABLE ride_requests (
                               id BIGSERIAL PRIMARY KEY,
                               passenger_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               office_id BIGINT NOT NULL REFERENCES offices(id) ON DELETE CASCADE,
                               pickup_location GEOMETRY(Point, 4326) NOT NULL,
                               target_time TIMESTAMPTZ NOT NULL,
                               tolerance_time INTEGER NOT NULL,
                               status VARCHAR(50) NOT NULL
);
CREATE INDEX idx_ride_requests_pickup_location ON ride_requests USING GIST (pickup_location);
