package com.duoc.Entradas_S1.controller;

import com.duoc.Entradas_S1.model.Evento;
import com.duoc.Entradas_S1.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController{

    private final EventoService eventoService;

    public EventoController(EventoService eventoService){
        this.eventoService = eventoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Evento>> obtenerEventos(){
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @PostMapping("/crear")
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento){
        Evento crearEvento = eventoService.nuevoEvento(evento);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(crearEvento);        
    }

}