package com.duoc.Entradas_S1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActualizarResumenCompraDTO(
    @JsonProperty("Número de Resumen") Long numeroResumen,
    @JsonProperty("Nombre de Evento") String nomEvento,
    @JsonProperty("Cantidad de Entradas") int cantidadEntradas,
    @JsonProperty("Precio Unitario") int precioUnit,
    @JsonProperty("Precio Total") int precioTotal
) {
}
