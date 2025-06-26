# Microservicio de Ventas

Este microservicio, desarrollado con Spring Boot, se encarga de gestionar operaciones relacionadas a ventas de productos. Forma parte de un sistema compuesto por múltiples microservicios, incluyendo uno encargado de la gestión de productos.  
Se conecta con este otro servicio para validar stock y actualizar inventario en tiempo real.

---

## Tecnologías

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- RestTemplate (para integración con otros servicios)
- Lombok

---

## Estructura del proyecto

```
src/
 └── main/
     ├── java/com/ventas/vmventas/
     │   ├── config/              # Configuraciones generales (RestTemplate)
     │   ├── controller/          # Controladores REST
     │   ├── DTO/                 # Clases DTO para integración
     │   ├── model/               # Entidad Venta
     │   ├── repository/          # Acceso a base de datos
     │   └── service/             # Lógica de negocio y cliente del otro microservicio
     └── resources/
         └── application.properties
```

---

## Funcionalidades principales

- Crear una venta validando el stock del producto.
- Registrar automáticamente la **fecha y hora** en que se realiza la venta.
- Calcular el total en base al precio y cantidad del producto.
- Actualizar la cantidad vendida (y ajustar el stock en el otro servicio).
- Eliminar una venta (y reponer el stock).
- Evitar que el usuario ingrese manualmente la fecha o el total.

---

## Comunicación entre microservicios

Este microservicio se comunica con el microservicio de productos a través de **RestTemplate**.  
La URL del otro microservicio se define en el archivo `application.properties`:

```properties
producto.api.url=http://localhost:8000/api/v1/productos
```

> Es necesario que el microservicio de productos esté corriendo para que las operaciones funcionen correctamente. Puerto 8000.

---

## Configuración local

**Archivo `application.properties`:**

```properties
spring.datasource.url=jdbc:postgresql://35.223.30.52:5432/bb_dd
spring.datasource.username=usuario
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
server.port=8001
app.api.base-url=/api/v1
producto.api.url=http://localhost:8000/api/v1/productos
```

---

## Cómo ejecutar

1. Clona el repositorio:

```bash
git clone https://github.com/usuario/repositorio.git
cd repositorio
```

2. Asegúrate de tener una base de datos PostgreSQL accesible.

3. Modifica `application.properties` si es necesario.

4. Ejecuta el proyecto desde tu IDE o con Maven:

```bash
./mvnw spring-boot:run
```

---

## Endpoints disponibles

| Método | Endpoint                | Descripción                         |
|--------|--------------------------|-------------------------------------|
| GET    | `/api/v1/ventas`         | Listar todas las ventas             |
| GET    | `/api/v1/ventas/{id}`    | Obtener venta por ID                |
| POST   | `/api/v1/ventas`         | Crear una venta                     |
| PUT    | `/api/v1/ventas/{id}`    | Actualizar cantidad de una venta    |
| DELETE | `/api/v1/ventas/{id}`    | Eliminar una venta y reponer stock  |

---

## Consideraciones

- El campo `fecha` se asigna automáticamente. Si el cliente intenta enviarlo manualmente, se lanza una excepción.
- El stock se actualiza **solo si se valida correctamente la venta**.
- Las excepciones están controladas y devuelven mensajes descriptivos.

---

## Autores

- Fernanda Miranda
- Daniel Melej
- Francisco Monsalve
- Nicolás Romo