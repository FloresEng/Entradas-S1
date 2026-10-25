package com.duoc.Entradas_S1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos")
@Getter // obtención de getters con lombok
public class Evento{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 100)
    @Setter // obtención de setters con lombok
    private String nombre;

    @Column (nullable = false)
    @Setter
    private String fecha;

    @Column (nullable = false)
    @Setter
    private int precio;

    // constructor vacío
    public Evento(){
    }

    public Evento(String nombre, String fecha, int precio){
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
    }

}