-- Ensure only these 3 roles exist
INSERT INTO roles (name) VALUES
                             ('CUSTOMER'),
                             ('TELLER'),
                             ('SUPER_ADMIN')
    ON CONFLICT (name) DO NOTHING;

-- Create a SUPER_ADMIN user (superadmin@example.com / password@1)
INSERT INTO users (email, password, citizen_id, thai_name, english_name, pin)
VALUES ('superadmin@example.com', '$2a$10$cyqz3lQyJoQeB1cdb4E9gezTto7IZhZxCqzwSqcKLEwUfMWxH27dK', NULL, 'ซุปเปอร์แอดมิน', 'Super Admin', '000000')
    ON CONFLICT (email) DO NOTHING;

-- Assign SUPER_ADMIN role to superadmin@example.com
INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'SUPER_ADMIN')
FROM users WHERE email = 'superadmin@example.com';

-- Create a TELLER user (test@example.com / password@1)
INSERT INTO users (email, password, citizen_id, thai_name, english_name, pin)
VALUES ('test@example.com', '$2a$10$cyqz3lQyJoQeB1cdb4E9gezTto7IZhZxCqzwSqcKLEwUfMWxH27dK', '1234567890123', 'พนักงาน', 'Bank Teller', '123456')
    ON CONFLICT (email) DO NOTHING;

-- Assign TELLER role to test@example.com
INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'TELLER')
FROM users WHERE email = 'test@example.com';