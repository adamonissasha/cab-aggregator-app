INSERT INTO promo_code (id, code, start_date, end_date, discount_percent)
VALUES (98, 'HELLO', '2023-11-20', '2023-12-03', 10),
       (99, 'NEWYEAR', current_date - interval '10' day, current_date + interval '10' day, 15);

INSERT INTO ride (id, passenger_id, start_address, end_address, creation_date_time, start_date_time, end_date_time,
                  payment_method, bank_card_id, promo_code_id, price, driver_id, car_id, status)
VALUES (98, '65cbc3c1a85b366e0ef0564c', 'Platonova 49', 'Masherova 113', localtimestamp, null, null, 'CARD', 1, 99, 12.5, 1, 1, 'CREATED'),
       (99, '65cbc3c1a85b366e0ef0564c', 'Platonova 49', 'Masherova 113', localtimestamp, null, null, 'CARD', 1, 99, 12.5, 1, 1, 'CANCELED'),
       (100, '65cbc3c1a85b366e0ef0564c', 'Platonova 49', 'Masherova 113', localtimestamp, localtimestamp, null, 'CARD', 1, 99, 12.5, 1, 1,
        'STARTED'),
       (101, '65cbc3c1a85b366e0ef0564c', 'Platonova 49', 'Masherova 113', localtimestamp, localtimestamp, null, 'CARD', 1, 99, 12.5, 1, 1,
        'COMPLETED');

INSERT INTO stop (id, number, address, ride_id)
VALUES (98, 1, 'Kupaly 88', 98),
       (99, 1, 'Kupaly 88', 99),
       (100, 1, 'Kupaly 88', 100),
       (101, 1, 'Kupaly 88', 101);