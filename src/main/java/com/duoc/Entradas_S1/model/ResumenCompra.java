package com.duoc.Entradas_S1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resumenes_compra")
@Getter
@Setter
@NoArgsConstructor
public class ResumenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreEvento;

    @Column(nullable = false)
    private int cantidadEntradas;

    @Column(nullable = false)
    private int precioUnitario;

    @Column(nullable = false)
    private int precioTotal;

    public ResumenCompra(String nombreEvento, int cantidadEntradas, int precioUnitario, int precioTotal) {
        this.nombreEvento = nombreEvento;
        this.cantidadEntradas = cantidadEntradas;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
    }
}
