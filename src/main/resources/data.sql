INSERT INTO rooms (id, room_number, building, capacity) VALUES
(1, '101', 'Science Hall', 30),
(2, '202', 'Library', 6),
(3, '303', 'Oren Gateway', 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bookings (id, room_id, booked_by, date, start_time, end_time, status) VALUES
(1, 1, 'Alice', '2026-06-20', '10:00:00', '11:00:00', 'ACTIVE'),
(2, 2, 'Bob', '2026-08-03', '09:00:00', '10:00:00', 'ACTIVE'),
(3, 3, 'Charlie', '2026-09-10', '14:00:00', '15:00:00', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('bookings', 'id'), COALESCE((SELECT MAX(id) FROM bookings), 1));
