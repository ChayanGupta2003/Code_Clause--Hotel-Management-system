CREATE TABLE owners (
    owner_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100)
);
INSERT INTO owners (name, email, password) VALUES
('Chayan Gupta', 'admin@gmail.com', '123');