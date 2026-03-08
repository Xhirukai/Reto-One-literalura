package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @Column(name = "numero_descargas")
    private Long numeroDescargas;


    private List<String> idioma;


    public Libro() {}

    public Libro(DatosLibro datosLibro, Autor autor) {
        this.titulo = datosLibro.titulo();
        this.autor = autor;

        if (datosLibro.idiomas().isEmpty()) {
            this.idioma.add("N/A");
        } else {
            this.idioma = new ArrayList<>(datosLibro.idiomas());
        }

        this.numeroDescargas = datosLibro.numeroDescargas();
    }


    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {

        return titulo;
    }
    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }

    public Autor getAutor() {

        return autor;
    }
    public void setAutor(Autor autor) {

        this.autor = autor;
    }

    public List<String> getIdioma() {

        return idioma;
    }

    public void setIdioma(List<String> idioma) {

        this.idioma = idioma;
    }

    public Long getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Long numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }
}

