package com.duoc.Entradas_S1.service;

public class ArchivoResumenNoEncontradoException extends RuntimeException {

    public ArchivoResumenNoEncontradoException(Long numeroResumen) {
        super("No existe el archivo del resumen de compra " + numeroResumen + " en S3");
    }
}
