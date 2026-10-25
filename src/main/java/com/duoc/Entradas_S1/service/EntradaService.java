package com.duoc.Entradas_S1.service;

import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.model.Entrada;
import com.duoc.Entradas_S1.model.Evento;
import com.duoc.Entradas_S1.repository.EntradaRepository;
import com.duoc.Entradas_S1.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class EntradaService{

    private final EntradaRepository entradaRepo;

    private final EventoRepository eventoRepo;

    // inyección por constructor
    public EntradaService(EntradaRepository entradaRepo, EventoRepository eventoRepo){
        this.entradaRepo = entradaRepo;
        this.eventoRepo = eventoRepo;
    }

    @Transactional // si falla la compra no se guarda ninguna entrad
    public ResumenCompraDTO comprarEntradas(Long eventoId, int cantidad){
        // validamos que exista el evento
        Evento evento = eventoRepo.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("Evento no existe"));

            int precioUnit = evento.getPrecio();
            int precioTotal = precioUnit * cantidad;

        // guardamos las entradas en la BD
        for(int i = 0; i < cantidad; i++){
            Entrada entrada = new Entrada();
            entrada.setEvento(evento);
            entrada.setPrecio(precioUnit);
            entradaRepo.save(entrada);
        }

        // retornamos resumen de compra
        return ResumenCompraDTO.builder()
            .nomEvento(evento.getNombre())
            .cantidadEntradas(cantidad)
            .precioUnit(precioUnit)
            .precioTotal(precioTotal)
            .build();
    }
}