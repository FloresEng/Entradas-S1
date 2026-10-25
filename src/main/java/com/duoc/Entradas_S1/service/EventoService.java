package com.duoc.Entradas_S1.service;

import com.duoc.Entradas_S1.model.Evento;
import com.duoc.Entradas_S1.repository.EventoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventoService{

    private final EventoRepository eventoRepo;

    // inyección por constructor
    public EventoService(EventoRepository eventoRepo){
        this.eventoRepo = eventoRepo;
    }

    // listar eventos disponibles
    public List<Evento> listarEventos(){
        return eventoRepo.findAll();
    }

    // crear nuevo evento
    public Evento nuevoEvento(Evento evento){
        return eventoRepo.save(evento);
    }
}