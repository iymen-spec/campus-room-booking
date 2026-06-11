INSERT INTO rooms (id, room_number, building, capacity) VALUES
(1, '101', 'Science Hall', 30),
(2, '202', 'Library', 6),
(3, '303', 'Oren Gateway', 20)
ON CONFLICT (id) DO NOTHING;
