# OneSales

OneSales es una aplicación diseñada para facilitar la gestión de ventas, inventario y clientes. Proporciona una experiencia sencilla y eficiente para pequeñas empresas y emprendedores.

## Objetivo
La idea detrás de este proyecto fue desarrollar una herramienta fácil de usar que permita:
- Llevar un registro de las ventas.
- Gestionar el inventario.
- Administrar la información de los clientes.
- Dar seguimiento al negocio mediante gráficos que muestran métricas sencillas de las ventas, como:
  - Productos más vendidos y menos vendidos.
  - Monto vendido por día.

## Módulos Principales

### Usuarios y Seguridad
- Implementación de **Spring Security** para gestionar el acceso y los permisos de los usuarios.
![SEGURIDAD](https://drive.google.com/uc?id=1Epqzb6uPFINVWuTFGTymtQ98woM2GJu6)


### Balance
- Proporciona un resumen completo de las ventas, incluyendo:
  - Balance diario.
  - Productos vendidos.
  - Ingresos obtenidos.
![BALANCE](https://drive.google.com/uc?id=1nN-lHI33hX1FxtNdFGZcrANZe1ERxT79)

### Inventario y Clientes
- Funciones para gestionar productos y clientes, como:
  - Creación.
  - Edición.
  - Eliminación.
![INVENTARIOCLIENTES](https://drive.google.com/uc?id=1ti8NmzbjMe35INov37beGV08rDMy21sh)

### Ventas
- Núcleo principal de la aplicación para manejar el proceso de ventas.
![VENTAS](https://drive.google.com/uc?id=1fSFrBgGp7NPKwSUGNHDLmsnMEa1uq3QN)

### Facturas
- Generación de facturas en formato PDF.
- Envío de facturas por correo electrónico a los clientes.
![VENTAS](https://drive.google.com/uc?id=1A90rM2TYUuDax1Ok6QkS6Ys-LAQYMzYm)

## Tecnologías Utilizadas
- **Java**
- **Spring Boot**
- **Spring Security**
- **Spring MVC**
- **JPA**
- **MySQL**
- Generación de documentos PDF

## Instalación
1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/onesales.git
   ```
2. Ve al directorio del proyecto:
   ```bash
   cd onesales
   ```
3. Configura la base de datos en el archivo `application.properties`.
4. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## Uso
1. Accede a la aplicación en [http://localhost:8080](http://localhost:8080).
2. Inicia sesión con tus credenciales.
3. Explora las funcionalidades: gestión de inventario, ventas y más.

## Contribuciones
¡Las contribuciones son bienvenidas! Si deseas contribuir:
1. Haz un fork del repositorio.
2. Crea una nueva rama para tu funcionalidad o corrección:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. Envía un pull request cuando estés listo.
---

Desarrollado por **Emiliano Miranda Miranda**.
