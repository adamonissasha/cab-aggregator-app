INSERT INTO passenger (id, first_name, last_name, email, phone_number, password, is_active)
VALUES (99, 'Alex', 'Alexandrov', 'alex@gmail.com', '+375297654321', '321321321', true),
       (100, 'Pasha', 'Pavlov', 'pasha@gmail.com', '+375297654322', '12345678', true);

INSERT INTO passenger_rating (id, driver_id, ride_id, passenger_id, rating)
VALUES (98, 1, 1, 99, 5),
       (99, 2, 2, 99, 4);