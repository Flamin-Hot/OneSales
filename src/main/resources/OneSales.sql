CREATE SCHEMA IF NOT EXISTS sales;
USE sales;

DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS inventario;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    tipo_usuario BOOLEAN DEFAULT false
);

CREATE TABLE cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    stock INT NOT NULL
);

CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    inventario_id INT,
    categoria_id INT,
    FOREIGN KEY (inventario_id) REFERENCES inventario(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_cliente INT NOT NULL,
    fecha DATE NOT NULL,
    total DOUBLE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);

CREATE TABLE detalle_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id),
    FOREIGN KEY (id_producto) REFERENCES producto(id)
);


-- Datos de Usuario, Cliente, Categorias, Productos, Inventarios, Ventas y Detalles Ventas
INSERT INTO usuario (nombre,apellido,email,contrasena,tipo_usuario) VALUES ("Emiliano","Miranda","emiliano@mail.com","123",TRUE);
INSERT INTO usuario (nombre,apellido,email,contrasena,tipo_usuario) VALUES ("Usuario","Usuario","usuario@mail.com","456",FALSE);

INSERT INTO cliente (nombre,apellido,email,telefono) VALUES ("Jhon","Doe","jhondoe@mail.com","5512435687");
INSERT INTO cliente (nombre,apellido,email,telefono) VALUES ("Jan","Doe","jandoe@mail.com","5521346578");

INSERT INTO categoria (nombre, descripcion) VALUES ('Electrónica', 'Productos electrónicos de última generación');
INSERT INTO categoria (nombre, descripcion) VALUES ('Ropa', 'Prendas de vestir para todas las edades');
INSERT INTO categoria (nombre, descripcion) VALUES ('Hogar', 'Artículos para el hogar');
INSERT INTO categoria (nombre, descripcion) VALUES ('Deportes', 'Artículos deportivos y equipos');
INSERT INTO categoria (nombre, descripcion) VALUES ('Juguetes', 'Juguetes y juegos para niños');

INSERT INTO inventario (stock) VALUES (100);
INSERT INTO inventario (stock) VALUES (50);
INSERT INTO inventario (stock) VALUES (75);
INSERT INTO inventario (stock) VALUES (120);
INSERT INTO inventario (stock) VALUES (200);
INSERT INTO inventario (stock) VALUES (8);
INSERT INTO inventario (stock) VALUES (4);
INSERT INTO inventario (stock) VALUES (17);
INSERT INTO inventario (stock) VALUES (4);
INSERT INTO inventario (stock) VALUES (2);

-- Asignación de productos a categorías
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Smartphone', 'Teléfono inteligente de alta gama', 799.99, 1, 1);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Laptop', 'Portátil ultradelgado', 1299.99, 2, 1);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Camiseta', 'Camiseta de algodón para hombres', 19.99, 3, 2);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Mueble de Salón', 'Sofá de cuero para la sala de estar', 799.99, 4, 3);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Balón de Fútbol', 'Balón oficial de la FIFA', 29.99, 5, 4);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Muñeca Barbie', 'Muñeca Barbie clásica', 12.99, 6, 5);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Tablet', 'Tablet de 10 pulgadas', 249.99, 7, 1);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Zapatillas Deportivas', 'Zapatillas para correr', 59.99, 8, 4);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Juego de Construcción', 'Set de construcción para niños', 39.99, 9, 5);
INSERT INTO producto (nombre, descripcion, precio, inventario_id, categoria_id) VALUES ('Refrigerador', 'Refrigerador de acero inoxidable', 899.99, 10, 3);