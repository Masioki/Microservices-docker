-- mysql-db
-- create necessary databases
create database if not exists creditDB;
create database if not exists productDB;
create database if not exists customerDB;

-- create users for services with privileges only to designated databases
drop user if exists 'credit-user'@'%';
flush privileges;
CREATE USER 'credit-user'@'%' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON creditDB.* TO 'credit-user'@'%';

drop user if exists 'product-user'@'%';
flush privileges;
CREATE USER 'product-user'@'%' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON productDB.* TO 'product-user'@'%';

drop user if exists 'customer-user'@'%';
flush privileges;
CREATE USER 'customer-user'@'%' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON customerDB.* TO 'customer-user'@'%';