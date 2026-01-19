-- Modify status column to use integer
ALTER TABLE t_company MODIFY COLUMN status INT DEFAULT 1;

-- Add missing fields one by one
ALTER TABLE t_company ADD COLUMN credit_code VARCHAR(50) AFTER company_code;
ALTER TABLE t_company ADD COLUMN short_name VARCHAR(100) AFTER credit_code;
ALTER TABLE t_company ADD COLUMN logo VARCHAR(255) AFTER short_name;
ALTER TABLE t_company ADD COLUMN stage VARCHAR(50) AFTER scale;
ALTER TABLE t_company ADD COLUMN city VARCHAR(100) AFTER location;
ALTER TABLE t_company ADD COLUMN address TEXT AFTER city;
ALTER TABLE t_company ADD COLUMN benefits TEXT AFTER description;
ALTER TABLE t_company ADD COLUMN contact_position VARCHAR(100) AFTER contact_person;
ALTER TABLE t_company ADD COLUMN verify_status VARCHAR(50) AFTER contact_email;
ALTER TABLE t_company ADD COLUMN verify_time DATETIME AFTER verify_status;
ALTER TABLE t_company ADD COLUMN reject_reason TEXT AFTER verify_time;
