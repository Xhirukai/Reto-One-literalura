package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ClienteHttp;
import com.alura.literalura.service.Conversor;


import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ClienteHttp cliente = new ClienteHttp();
    private Conversor conversor = new Conversor();
    private Scanner lecturaTeclado = new Scanner(System.in);


    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por nombre 
                    2 - Mostrar libros buscados
                    3 - Mostrar autores registrados
                    4 - Mostrar autores vivos en determinada fecha
                    5 - Mostrar libros por idioma
                    
                    
                    0 - Salir
                    """;

            System.out.println(menu);
            opcion = lecturaTeclado.nextInt();
            lecturaTeclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorNombre();
                    break;

                case 2:
                    mostrarLibrosBuscados();
                    break;

                case 3:
                    mostrarAutoresRegistrados();
                    break;

                case 4:
                    mostrarAutoresVivosEnUnaFecha();
                    break;

                case 5:
                    mostrarLibrosPorIdioma();
                    break;

                case 0:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida, por favor intente nuevamente");

            }
        }
    }

    private void buscarLibroPorNombre() {
        System.out.println("Ingrese el titulo del libro que desea buscar: ");
        var tituloLibro = lecturaTeclado.nextLine();
        var json = cliente.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datos = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibro> libroBuscado = datos.listaDatosLibro().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibro datosLibro = libroBuscado.get();


            if (libroRepository.findByTituloIgnoreCase(datosLibro.titulo()).isPresent()) {
                System.out.println("El libro ya está registrado en la base de datos");
                return;
            }


            if (datosLibro.autor().isEmpty()) {
                System.out.println("El libro no tiene autores registrados");
                return;
            }

            DatosAutor datosAutor = datosLibro.autor().get(0);


            Autor autor = autorRepository.findByNombreIgnoreCase(datosAutor.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosAutor);
                        return autorRepository.save(nuevoAutor);
                    });


            Libro libro = new Libro(datosLibro, autor);
            libroRepository.save(libro);

            System.out.println("\nLIBRO_ENCONTRADO");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Idioma: " + libro.getIdioma());
            System.out.println("Número de descargas: " + libro.getNumeroDescargas());
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    private void mostrarLibrosBuscados() {
        var libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("La tabla de libros de la base de datos esta vacia");
            return;
        }

        System.out.println("\nLISTA_DE_LIBROS\n");

        libros.forEach(libro -> {
            System.out.println("----- LIBRO_#_" + libro.getId() + "-----");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + libro.getAutor().getNombre());
            System.out.println("Idioma: " + libro.getIdioma());
            System.out.println("Número de descargas: " + libro.getNumeroDescargas());
            System.out.println();
        });
    }

    private void mostrarAutoresRegistrados() {
        var autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("La base de datos esta vacía");
            return;
        }

        System.out.println("\nLISTA_DE_AUTORES\n");

        autores.forEach(autor -> {
            System.out.println("----- AUTOR_#_" + autor.getId() + "-----");
            System.out.println("Autor: " + autor.getNombre());

            if (autor.getFechaDeNacimiento() != null) {
                System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
            } else {
                System.out.println("Fecha de nacimiento: N/A");
            }

            if (autor.getFechaDeFallecimiento() != null) {
                System.out.println("Fecha de fallecimiento: " + autor.getFechaDeFallecimiento());
            } else {
                System.out.println("Fecha de fallecimiento: N/A");
            }

            System.out.println();
        });
    }



    private void mostrarAutoresVivosEnUnaFecha() {
        System.out.println("Ingrese el año en el que desea buscar al autor(es): ");
        var fecha = lecturaTeclado.nextInt();
        lecturaTeclado.nextLine();

        var autores = autorRepository.findAutoresVivosEnFecha(fecha);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + fecha);
            return;
        }

        System.out.println("\nAUTORES_VIVOS_EN_" + fecha + " \n");

        autores.forEach(autor -> {
            System.out.println("LISTA_DE_AUTORES");
            System.out.println("Autor: " + autor.getNombre());

            if (autor.getFechaDeNacimiento() != null) {
                System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
            } else {
                System.out.println("Fecha de nacimiento: N/A");
            }

            if (autor.getFechaDeFallecimiento() != null) {
                System.out.println("Fecha de fallecimiento: " + autor.getFechaDeFallecimiento());
            } else {
                System.out.println("Fecha de fallecimiento: N/A");
            }

            System.out.println();
        });
    }


    private void mostrarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma que desea buscar (ej: es, en, fr): ");
        var busquedaIdioma = lecturaTeclado.nextLine().trim().toLowerCase();

        var libros = libroRepository.findAll().stream()
                .filter(libro -> libro.getIdioma() != null &&
                        libro.getIdioma().stream()
                                .anyMatch(idioma -> idioma.equalsIgnoreCase(busquedaIdioma)))
                .toList();

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + busquedaIdioma);
            return;
        }

        System.out.println("\nLIBROS_EN_" + busquedaIdioma.toUpperCase() + ": \n");

        libros.forEach(libro -> {
            System.out.println("----- LIBRO -----");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + libro.getAutor().getNombre());
            System.out.println("Idioma(s): " + String.join(", ", libro.getIdioma()));
            System.out.println("Número de descargas: " + libro.getNumeroDescargas());
            System.out.println();
        });
    }


}