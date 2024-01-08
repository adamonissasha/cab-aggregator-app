INSERT INTO car (id, number, color, car_make)
VALUES (98, '1234-AA-1', 'red', 'BMW'),
       (99, '1234-BB-2', 'black', 'Audi');

INSERT INTO driver (id, first_name, last_name, email, phone_number, password, car_id, is_active, status)
VALUES (99, 'Alex', 'Alexandrov', 'alex@gmail.com', '+375297654321', '321321321', 98, true, 'FREE'),
       (100, 'Pasha', 'Pavlov', 'pasha@gmail.com', '+375297654322', '12345678', 99, true, 'BUSY');

INSERT INTO driver_rating (id, driver_id, ride_id, passenger_id, rating)
VALUES (98, 99, 1, 1, 5),
       (99, 99, 2, 2, 4);