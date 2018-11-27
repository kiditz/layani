INSERT INTO tm_user (user_id, username, phone_number, fullname, hash_password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at) VALUES
(3,'kiditz' ,'087788044374', 'Rifky Aditya Bastara','$2y$12$50cOcCdyU8hQ6VuYLDmBn.z5pnJW4JO9N/2dYzhu0.GYxGGaJ1vW.', true,  true,  true, true, now());

INSERT INTO tm_user_authority (user_authority_id, authority, user_id) VALUES (1, 'TEACHER', 3);
INSERT INTO tm_user_authority (user_authority_id, authority, user_id) VALUES (2, 'STUDENT', 3);