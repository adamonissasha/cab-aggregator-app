INSERT INTO bank_card (id, number, expiry_date, cvv, balance, is_default, bank_user_id, bank_user)
VALUES (98, '1234 1234 1234 1234', '04/06', '364', 349.2, true, 3, 'PASSENGER'),
       (99, '4321 4321 4321 4321', '01/10', '921', 136.7, true, 1, 'DRIVER');

INSERT INTO bank_account (id, number, balance, driver_id, is_active)
VALUES (98, 'ojvcih2vf345vw', 250.6, 1, true),
       (99, 'sdhfjrkwnm4kl3', 123.3, 2, true);

INSERT INTO bank_account_history (id, operation_date_time, operation, sum, bank_account_id)
VALUES (98, localtimestamp, 'REFILL', 20.6, 98),
       (99, localtimestamp, 'WITHDRAWAL', 13.3, 98);