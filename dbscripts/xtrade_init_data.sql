-- ID生成器
INSERT INTO xtrade_sequence_key(id, `key`, start_with, inc_span, scope, version, expired_date, description)
VALUES (1, 'TEST_SEQUENCE', 1, 50, null, 1, curdate(), null);
INSERT INTO xtrade_sequence_key(id, `key`, start_with, inc_span, scope, version, expired_date, description)
VALUES (2, 'TEST_SERIAL_SEQUENCE', 1, 1, null, 1, curdate(), null);