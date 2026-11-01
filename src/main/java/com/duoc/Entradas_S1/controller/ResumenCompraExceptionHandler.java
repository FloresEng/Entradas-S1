package com.duoc.Entradas_S1.controller;

import com.duoc.Entradas_S1.service.ArchivoResumenNoEncontradoException;
import com.duoc.Entradas_S1.service.ResumenCompraNoEncontradoException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResumenCompraExceptionHandler {

    @ExceptionHandler({ResumenCompraNoEncontradoException.class, ArchivoResumenNoEncontradoException.class})
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, ArithmeticException.class})
    public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(RuntimeException exception) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", exception.getMessage()));
    }
}
