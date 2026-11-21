# Pharmanova Farmacia · Control de inventario y ventas

Proyecto Integrador · **Desarrollo Orientado a Servicios 2026-2**
Universidad Santo Tomás, Seccional Tunja · Ingeniería de Sistemas

Aplicación web para una droguería que controla el inventario, avisa qué productos están por agotarse y calcula cuánto se vendió en cada mes. Está hecha con **Quarkus**, **arquitectura hexagonal**, **MySQL (JPA/Hibernate)**, vistas **Qute + Bootstrap 5**, reportes **PDF con OpenPDF** y documentación **Swagger**.

---

## 1. Problema, usuarios y datos

- **Problema:** una droguería no sabe qué productos están por agotarse ni cuánto vendió en el mes. El control se lleva en cuadernos, y se pierden ventas por productos que se acaban sin aviso.
- **A quién le sirve:** al **regente o administrador**, que controla el inventario y revisa los reportes, y al **auxiliar de mostrador**, que registra las ventas.
- **Qué datos guarda y consulta:** guarda **productos** (nombre, categoría, precio, stock y stock mínimo) y **ventas** (producto, cantidad, precio unitario, total y fecha). Consulta los productos por agotarse y el resumen de ventas de cada mes.

## 2. Funcionalidades

- Registrar productos con su propio **stock mínimo**, sin nombres repetidos.
- Consultar el inventario con el estado de cada producto: **Disponible**, **Por agotarse** o **Agotado**.
- **Alerta** con la cantidad de productos por agotarse y filtro para verlos.
- **Reponer stock** desde la misma lista del inventario.
- **Registrar ventas** escogiendo el producto de una lista; el sistema calcula el precio y el total.
- Impedir ventas con más unidades de las que hay en stock.
- **Descontar el stock** automáticamente al vender, en una sola operación junto con la venta.
- Consultar las **ventas de cualquier mes** con su resumen: total vendido, unidades, número de ventas y producto más vendido.
- Descargar un **reporte PDF** del mes con el resumen, el detalle de ventas y los productos por agotarse.
- Mensajes de éxito y de error en pantalla.

## 3. Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Quarkus 3.40.1.2 | Framework (REST, CDI, Qute, Hibernate ORM) |
| MySQL (XAMPP) | Base de datos |
| Hibernate ORM / JPA | Persistencia |
| Qute | Plantillas HTML del lado del servidor |
| Bootstrap 5 + Bootstrap Icons | Interfaz |
| OpenPDF | Generación del reporte PDF |
| SmallRye OpenAPI | Swagger UI y documento OpenAPI |
| Maven (`mvnw`) | Construcción del proyecto |

## 4. Arquitectura hexagonal

El proyecto se organiza en tres capas. **Todas las dependencias apuntan hacia el dominio**; el dominio no depende de nadie.

```
infrastructure  ──►  application  ──►  domain
(adaptadores)        (puertos y         (entidades y
                      casos de uso)      reglas de negocio)
```

| Elemento | Ubicación (dentro del paquete base) |
|---|---|
| Entidades y reglas de negocio | `domain/model` |
| Servicios de dominio (reglas puras) | `domain/service` |
| Excepción de negocio | `domain/exception` |
| Puertos de entrada (casos de uso) | `application/port/in` |
| Puertos de salida (guardar y consultar) | `application/port/out` |
| Implementación de los casos de uso | `application/usecase` |
| Excepciones de aplicación | `application/exception` |
| Adaptador web (vistas HTML y PDF) | `infrastructure/adapter/in/web` |
| Adaptador JPA (MySQL) | `infrastructure/adapter/out/persistence` |
| Entidades JPA | `infrastructure/adapter/out/persistence/entity` |
| Mappers dominio ↔ entidad | `infrastructure/adapter/out/persistence/mapper` |
| Conexión entre capas y datos de prueba | `infrastructure/config` |

- El **dominio** no tiene `@Entity` ni código del framework.
- Los **puertos** son interfaces: los de entrada definen los casos de uso y los de salida lo que el sistema necesita para guardar y consultar.
- Los **adaptadores** traducen: el adaptador web recibe las peticiones del navegador y el adaptador JPA guarda en MySQL.
- La única entrada del sistema es la **vista HTML**: no hay API REST aparte ni adaptador de consola.

### Estructura de carpetas

```
drogueria-proyecto-integrador/
├── pom.xml
├── mvnw · mvnw.cmd
├── README.md
└── src/main/
    ├── java/dev/nickinicoll/pharmacy/
    │   ├── domain/
    │   │   ├── exception/   BusinessException
    │   │   ├── model/       Product, Sale, MonthlySummary
    │   │   └── service/     SaleDomainService, SalesSummaryService
    │   ├── application/
    │   │   ├── exception/   ResourceNotFoundException
    │   │   ├── port/in/     ProductUseCase, SaleUseCase
    │   │   ├── port/out/    ProductRepositoryPort, SaleRepositoryPort
    │   │   └── usecase/     ProductUseCaseImpl, SaleUseCaseImpl
    │   └── infrastructure/
    │       ├── adapter/in/web/   ProductWebResource, SaleWebResource,
    │       │                     SalesPdfGenerator, WebSupport
    │       ├── adapter/out/persistence/
    │       │   ├── ProductRepositoryAdapter, SaleRepositoryAdapter
    │       │   ├── entity/   ProductEntity, SaleEntity
    │       │   └── mapper/   ProductMapper, SaleMapper
    │       └── config/      BeanConfig, DataSeeder
    └── resources/
        ├── application.properties
        ├── templates/       base.html, index.html, form.html,
        │                    productForm.html, sales.html
        └── META-INF/resources/
            ├── css/pharmanova.css
            └── img/logo.png
```

## 5. Requisitos previos

- **JDK 21**
- **Maven** (se puede usar el `mvnw` incluido, o el de IntelliJ IDEA)
- **XAMPP** con MySQL (puerto 3306, usuario `root` y sin contraseña)
- Conexión a internet la primera vez (Maven descarga las dependencias y las plantillas cargan Bootstrap por CDN)

## 6. Base de datos

La aplicación **crea la base de datos y las tablas sola** al arrancar:

- `createDatabaseIfNotExist=true` en la URL crea la base **`drogueria`**.
- `quarkus.hibernate-orm.schema-management.strategy=update` crea las tablas **`products`** y **`sales`** a partir de las entidades JPA.

Lo único que hay que hacer:

1. Abre el **XAMPP Control Panel** y pulsa **Start** en **MySQL**.
2. Pulsa **Start** en **Apache** solo si quieres abrir phpMyAdmin.

Configuración en `src/main/resources/application.properties`:

```properties
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=root
quarkus.datasource.password=
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/drogueria?createDatabaseIfNotExist=true&serverTimezone=America/Bogota
quarkus.hibernate-orm.schema-management.strategy=update
```

### Modelo de datos

```
products                          sales
────────────                      ────────────
id (PK)                           id (PK)
name (único)                      product_id (FK → products.id)
category                          quantity
price                             unit_price
stock                             total
min_stock                         sale_date
```

Relación: **una venta pertenece a un producto** (`@ManyToOne` + `@JoinColumn(name = "product_id")`).

### Script SQL opcional

Solo si se quiere crear las tablas a mano (phpMyAdmin → pestaña **SQL**):

```sql
CREATE DATABASE IF NOT EXISTS drogueria
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE drogueria;

CREATE TABLE IF NOT EXISTS products (
  id         BIGINT NOT NULL AUTO_INCREMENT,
  name       VARCHAR(120) NOT NULL,
  category   VARCHAR(60)  NOT NULL,
  price      DECIMAL(12,2) NOT NULL,
  stock      INT NOT NULL,
  min_stock  INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_products_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sales (
  id          BIGINT NOT NULL AUTO_INCREMENT,
  product_id  BIGINT NOT NULL,
  quantity    INT NOT NULL,
  unit_price  DECIMAL(12,2) NOT NULL,
  total       DECIMAL(14,2) NOT NULL,
  sale_date   DATE NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_sales_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;
```

### Empezar de cero

Para borrar todo y volver a cargar los datos de prueba, en phpMyAdmin ejecuta `DROP DATABASE drogueria;` y vuelve a iniciar la aplicación.

## 7. Cómo ejecutar

### Desde IntelliJ IDEA (recomendado)

1. Prende **MySQL** en XAMPP.
2. Abre el proyecto: *File → Open* y elige la carpeta que contiene el `pom.xml`.
3. En la pestaña **Maven** (borde derecho), pulsa **Reload All Maven Projects** y espera a que termine.
4. Despliega el proyecto → **Plugins → quarkus** y haz **doble clic en `quarkus:dev`**.
5. Cuando la consola muestre `Listening on: http://localhost:8080`, abre esa dirección en el navegador.

### Desde la terminal de Windows (PowerShell)

Estando en la carpeta del proyecto (no en WSL/bash):

```powershell
.\mvnw.cmd quarkus:dev
```

Si indica que no encuentra `JAVA_HOME`, indícale el JDK 21 solo para esa ventana:

```powershell
$env:JAVA_HOME = "C:\ruta\al\jdk-21"
.\mvnw.cmd quarkus:dev
```

### Desde la terminal de Linux o Mac

```bash
./mvnw quarkus:dev
```

En modo dev, los cambios en el código y en las plantillas se recargan al refrescar el navegador. Para detener la aplicación, pulsa `Ctrl + C`.

## 8. Rutas

| Ruta | Descripción |
|---|---|
| `http://localhost:8080/` | Inventario con alertas de productos por agotarse |
| `http://localhost:8080/?filter=low` | Solo los productos por agotarse |
| `http://localhost:8080/products/new` | Registrar un producto |
| `http://localhost:8080/sales/new` | Registrar una venta |
| `http://localhost:8080/sales` | Ventas del mes y resumen (acepta `?month=2026-11`) |
| `http://localhost:8080/sales/report/pdf` | Reporte PDF del mes (acepta `?month=2026-11`) |
| `http://localhost:8080/q/swagger-ui` | Documentación Swagger |
| `http://localhost:8080/q/openapi` | Documento OpenAPI |

## 9. Datos de prueba

Al primer arranque, la clase `DataSeeder` carga **8 productos** y **13 ventas** repartidas entre el mes actual y meses anteriores. Con esos datos se ven **3 productos por agotarse** (Ibuprofeno, Loratadina y Vitamina C) y **1 agotado** (Suero oral). En un segundo arranque no se duplican.

> No se usa `import.sql`: los datos de prueba los carga `DataSeeder` a través de los casos de uso.

## 10. Reglas de negocio

Están en el **dominio**:

- Un producto está **por agotarse** cuando su stock es menor o igual a su stock mínimo, y **agotado** cuando es cero.
- El nombre del producto no puede repetirse (sin distinguir mayúsculas) y el precio debe ser mayor que cero.
- No se puede vender más unidades de las que hay en inventario, ni un producto agotado.
- La venta y el descuento de stock se guardan en **una sola transacción** (todo o nada).
- La fecha de una venta no puede ser futura.
- La reposición debe ser de al menos 1 unidad.

## 11. Reporte PDF

Se descarga desde la pantalla **Ventas del mes** (botón *Descargar PDF*) o en `/sales/report/pdf?month=AAAA-MM`. El archivo `reporte-AAAA-MM.pdf` incluye:

- Título y mes del reporte.
- Resumen: total vendido, unidades, número de ventas y producto más vendido.
- Tabla con el detalle de las ventas.
- Tabla con los productos por agotarse.

## 12. Documentación Swagger

Con la aplicación corriendo:

- **Swagger UI:** `http://localhost:8080/q/swagger-ui`
- **Documento OpenAPI:** `http://localhost:8080/q/openapi`

Los recursos de las vistas están agrupados con `@Tag` (*Productos* y *Ventas*) y descritos con `@Operation`. Swagger no obliga a crear una API REST aparte: documenta los recursos que sirven las páginas.

## 13. Evidencia de base de datos (phpMyAdmin)

1. Abre `http://localhost/phpmyadmin` y entra a la base **`drogueria`**.
2. **Llaves foráneas:** pestaña **Diseñador** (o *Más → Diseñador*); se ve la relación `sales.product_id` → `products.id`.
3. **Datos:** clic en `products` y en `sales` → **Examinar**.

## 14. Identidad visual

La interfaz usa la marca **Pharmanova Farmacia** y la paleta *Forest Exhale*:

| Color | Código | Uso |
|---|---|---|
| Deep Teal | `#0F3F3F` | Navbar, footer, botones primarios y textos |
| Celadon | `#B6D298` | Acentos, enlace activo y estado «Disponible» |
| Beige | `#DCE2C8` | Encabezados de tablas y paneles secundarios |

Los estilos están en `src/main/resources/META-INF/resources/css/pharmanova.css` y el logo en `META-INF/resources/img/logo.png`. Quarkus sirve esa carpeta como archivos estáticos en la raíz del sitio.

## 15. Solución de problemas

| Mensaje | Causa y solución |
|---|---|
| `Communications link failure` | MySQL está apagado. Inícialo en XAMPP. |
| `Access denied for user 'root'` | Tu MySQL tiene contraseña. Ponla en `quarkus.datasource.password=`. |
| `Port already bound: 8080` | Ya hay otra ejecución abierta. Deténla (cuadrado rojo en la pestaña **Run**) o cambia `quarkus.http.port=8081`. |
| `No template found for path [...]` | Faltan plantillas en `templates/` o tienen otro nombre. Deben ser `base`, `index`, `form`, `productForm` y `sales`. |
| `Property/method not found ... ItemProducto` | Quedaron plantillas viejas en `templates/` o archivos viejos en `target/`. Bórralas y ejecuta **Maven → Lifecycle → clean**. |
| La página se ve sin estilos ni logo | El CSS y el logo deben estar en `src/main/resources/META-INF/resources/`. Prueba `http://localhost:8080/css/pharmanova.css`. |
| `release version 21 not supported` | La terminal usa otro JDK. Ejecuta desde la pestaña **Maven** de IntelliJ o define `JAVA_HOME`. |
| Error de dialecto o versión de MySQL | XAMPP trae MariaDB. Cambia `quarkus-jdbc-mysql` por `quarkus-jdbc-mariadb`, usa `db-kind=mariadb` y la URL `jdbc:mariadb://localhost:3306/drogueria?createDatabaseIfNotExist=true`. |
| Imports en rojo | Maven no descargó las dependencias. Pulsa **Reload All Maven Projects** y desactiva **Work offline**. |

## 16. Equipo y curso

- **Integrantes:** (Nicoll Alejandra Pérez Palencia - Sergio Andrés Ávila Ávila - Juan Esteban Niño Benitez)
- **Docente:** (Jhon Alexis Piracoca)
- **Curso:** Desarrollo Orientado a Servicios 2026-2
- **Universidad:** Santo Tomás, Seccional Tunja
