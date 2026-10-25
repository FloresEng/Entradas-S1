package com.duoc.Entradas_S1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumenCompraDTO {

    @JsonProperty("Nombre de Evento")
    private String nomEvento;

    @JsonProperty("Cantidad de Entradas")
    private int cantidadEntradas;

    @JsonProperty("Precio Unitario")
    private int precioUnit;

    @JsonProperty("Precio Total")
    private int precioTotal;
}
