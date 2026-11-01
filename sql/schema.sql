-- Sweet Crumbs - database schema and starting data (MySQL 8)
-- Run with:  mysql --default-character-set=utf8mb4 -u root -p < sql/schema.sql

DROP DATABASE IF EXISTS sweet_crumbs;
CREATE DATABASE sweet_crumbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sweet_crumbs;

-- ---------- TABLES ----------

CREATE TABLE users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(20)  NOT NULL,
    password_hash  CHAR(64)     NOT NULL,          -- SHA-256 hex, never the plain password
    is_admin       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE products (
    product_id   INT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    category     VARCHAR(30)  NOT NULL,
    price        DECIMAL(8,2) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    emoji        VARCHAR(10)  NOT NULL
);

CREATE TABLE orders (
    order_id       INT AUTO_INCREMENT PRIMARY KEY,
    customer_name  VARCHAR(100)  NOT NULL,
    phone          VARCHAR(20)   NOT NULL,
    address        VARCHAR(500)  NOT NULL,
    delivery_date  VARCHAR(20)   NOT NULL,
    baker_note     VARCHAR(1000) NOT NULL DEFAULT '',
    total          DECIMAL(10,2) NOT NULL,
    status         VARCHAR(20)   NOT NULL DEFAULT 'Pending',
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT = 1001;                            -- order numbers start at #1001 like before

CREATE TABLE order_items (
    item_id               INT AUTO_INCREMENT PRIMARY KEY,
    order_id              INT           NOT NULL,
    product_id            INT           NOT NULL,
    quantity              INT           NOT NULL CHECK (quantity > 0),
    unit_price            DECIMAL(8,2)  NOT NULL,   -- price at the time of ordering
    cake_message          VARCHAR(255)  NOT NULL DEFAULT '',
    special_instructions  VARCHAR(1000) NOT NULL DEFAULT '',
    FOREIGN KEY (order_id)   REFERENCES orders(order_id)     ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- ---------- STARTING DATA ----------

INSERT INTO products (product_id, name, category, price, description, emoji) VALUES
(1,  'Chocolate Truffle Cake',  'Cake',     850, 'Rich chocolate cake covered with smooth chocolate ganache.', '🎂'),
(2,  'Red Velvet Cake',         'Cake',     900, 'Soft red velvet cake with creamy cheese frosting.', '🎂'),
(3,  'Black Forest Cake',       'Cake',     950, 'Classic chocolate cake layered with cream and cherries.', '🎂'),
(4,  'Chocolate Cupcake',       'Cupcake',   60, 'Soft chocolate cupcake with chocolate frosting.', '🧁'),
(5,  'Vanilla Cupcake',         'Cupcake',   50, 'Classic vanilla cupcake with creamy vanilla frosting.', '🧁'),
(6,  'Fudge Brownie',           'Brownie',   80, 'Dense, rich and fudgy chocolate brownie.', '🍫'),
(7,  'Walnut Brownie',          'Brownie',  100, 'Chocolate brownie with crunchy walnuts.', '🍫'),
(8,  'Chocolate Chip Cookies',  'Cookie',   150, 'Homemade cookies filled with chocolate chips.', '🍪'),
(9,  'Butter Cookies',          'Cookie',   130, 'Classic crisp and buttery homemade cookies.', '🍪'),
(10, 'Cinnamon Roll',           'Pastry',   120, 'Soft cinnamon roll finished with a sweet glaze.', '🥐'),
(11, 'Fruit Tart',              'Pastry',   220, 'Buttery tart filled with cream and fresh fruit.', '🥧'),
(12, 'Brownie Box',             'Gift Box', 450, 'A gift box containing six assorted brownies.', '🎁');

INSERT INTO users (name, email, phone, password_hash, is_admin) VALUES
('Demo Customer', 'customer@sweetcrumbs.com', '9876543210', SHA2('1234', 256),  FALSE),
('Bakery Admin',  'admin@sweetcrumbs.com',    '9999999999', SHA2('admin', 256), TRUE);
