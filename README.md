# LiterAlura - Catálogo de Libros

## Descripción del Proyecto

LiterAlura es una aplicación de consola desarrollada con **Spring Boot** que permite buscar, registrar y consultar libros utilizando la API de **Gutendex**. Los datos se almacenan en una base de datos relacional usando **JPA/Hibernate**.
Es necesario anotar que para el proyecto no se hace uso de variables de entorno por su simplicidad y por la facilidad de implementacion para incorporarlo a cualquier base de datos postgreSQL.

## Tecnologías Utilizadas

- **Java 17+**
- **Spring Boot**
- **Spring Data JPA**
- **Maven**
- **PostgreSQL**
- **Jackson Databind** (procesamiento JSON)
- **API Gutendex** (catálogo de libros)

## Funcionalidades del Menú

### 1. Buscar libro por título
- Solicita al usuario el título del libro
- Realiza una petición HTTP a la API de Gutendex
- Deserializa la respuesta JSON usando Jackson
- Guarda el libro y su autor en la base de datos (si no existen)
- Muestra la información del libro encontrado

**Métodos utilizados:**
- `ClienteHttp.obtenerDatos()` - Realiza la petición HTTP
- `Conversor.obtenerDatos()` - Deserializa el JSON
- `LibroRepository.save()` - Persiste en la base de datos

### 2. Listar libros registrados
- Recupera todos los libros almacenados en la base de datos
- Muestra el título, autor, idiomas y número de descargas de cada libro

**Métodos utilizados:**
- `LibroRepository.findAll()` - Derived Query que obtiene todos los libros

### 3. Listar autores registrados
- Obtiene todos los autores únicos de la base de datos
- Muestra nombre, fecha de nacimiento, fecha de fallecimiento y sus libros

**Métodos utilizados:**
- `AutorRepository.findAll()` - Derived Query que obtiene todos los autores

### 4. Listar autores vivos en un determinado año
- Solicita al usuario un año específico
- Busca autores que estaban vivos en ese año
- Filtra comparando el año con las fechas de nacimiento y fallecimiento

**Métodos utilizados:**
- `AutorRepository.findAutoresVivosEnAnio()` - Consulta personalizada con `@Query`:
```java
@Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :anio AND (a.fechaDeFallecimiento >= :anio OR a.fechaDeFallecimiento IS NULL)")
List<Autor> findAutoresVivosEnAnio(@Param("anio") Integer anio);
```

### 5. Listar libros por idioma
- Solicita al usuario el código del idioma (ej: es, en, fr)
- Busca libros que contengan ese idioma
- Combina Derived Query con Streams de Java para el filtrado

**Métodos utilizados:**
- `LibroRepository.findAll()` - Derived Query para obtener todos los libros
- **Streams de Java** para filtrar por idioma:
```java
.stream()
.filter(libro -> libro.getIdioma().stream()
    .anyMatch(idioma -> idioma.equalsIgnoreCase(idiomaBuscado)))
.toList()
```

## Estructura del Proyecto

```
literalura/
├── model/
│   ├── Autor.java           # Entidad JPA para autores
│   ├── Libro.java           # Entidad JPA para libros
│   ├── Datos.java           # Record para respuesta completa API
│   ├── DatosLibro.java      # Record para datos del libro
│   └── DatosAutor.java      # Record para datos del autor
├── repository/
│   ├── AutorRepository.java # Repositorio JPA para autores
│   └── LibroRepository.java # Repositorio JPA para libros
├── service/
│   ├── ClienteHttp.java     # Cliente HTTP para consumir API
│   ├── Conversor.java       # Implementación de conversión JSON
│   └── IConversor.java      # Interfaz de conversión
└── principal/
    └── Principal.java       # Clase principal con menú interactivo
```

## Modelo de Datos

### Relaciones
- Un **Autor** puede tener uno o más **Libros** (relación @OneToMany)
- Un **Libro** pertenece a un **Autor** (relación @ManyToOne)

### Entidades

**Libro:**
- id (Long)
- titulo (String)
- autor (Autor)
- idioma (List<String>)
- numeroDescargas (Long)

**Autor:**
- id (Long)
- nombre (String)
- fechaDeNacimiento (Integer)
- fechaDeFallecimiento (Integer)
- libros (List<Libro>)

## Configuración

### Requisitos Previos
1. Java 17 o superior
2. PostgreSQL instalado y ejecutándose
3. Base de datos `literalura_db` creada

### application.properties
```properties
spring.application.name=literalura

spring.datasource.url=jdbc:postgresql://localhost:5432/literalura_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Ejecución

Ejecutar la clase principal `LiteraluraApplication.java`

## Uso de la Aplicación

Al ejecutar la aplicación, se mostrará un menú interactivo:

```
1 - Buscar libro por nombre
2 - Mostrar libros buscados
3 - Mostrar autores registrados
4 - Mostrar autores vivos en determinado año
5 - Mostrar libros por idioma

0 - Salir
```

Selecciona la opción deseada ingresando el número correspondiente.

## API Utilizada

**Gutendex API:** https://gutendex.com/

La API proporciona acceso a más de 70,000 libros del Proyecto Gutenberg.

### Ejemplo de uso de api:
```
https://gutendex.com/books/?search=pride+and+prejudice
```

## Características Técnicas

- **Derived Queries:** Uso de consultas derivadas de Spring Data JPA para operaciones de base de datos
- **Streams de Java:** Procesamiento funcional de colecciones
- **Jackson Databind:** Deserialización automática de JSON a objetos Java
- **JPA/Hibernate:** Mapeo objeto-relacional y persistencia
- **HttpClient:** Cliente HTTP nativo de Java para consumir APIs REST



## Autor

Proyecto desarrollado por Luis Daniel Rosas Miranda, estudiante del programa **ONE - Oracle Next Education** de **Alura Latam**.



