package com.duoc.Entradas_S1.service;

public class ResumenCompraNoEncontradoException extends RuntimeException {

    public ResumenCompraNoEncontradoException(Long numeroResumen) {
        super("No existe el resumen de compra " + numeroResumen);
    }
}
