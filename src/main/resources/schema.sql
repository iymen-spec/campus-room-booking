CREATE TABLE IF NOT EXISTS rooms
(
    id BIGINT PRIMARY KEY,
    room_number  varchar(255) not null,
    building     varchar(255) not null,
    capacity     integer not null
);
