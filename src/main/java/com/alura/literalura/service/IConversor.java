package com.alura.literalura.service;

public interface IConversor {
    <T> T obtenerDatos(String json, Class<T> clase);
}
