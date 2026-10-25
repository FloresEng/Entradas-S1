package com.duoc.Entradas_S1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "entradas")
@Getter
@Setter
public class Entrada{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false)
    private int precio;

    public Entrada(){

    }

    public Entrada(Evento evento, int precio){
        this.evento = evento;
        this.precio = precio;
    }
    

}
