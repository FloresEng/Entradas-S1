package com.duoc.Entradas_S1.service;

import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.model.Entrada;
import com.duoc.Entradas_S1.model.Evento;
import com.duoc.Entradas_S1.model.ResumenCompra;
import com.duoc.Entradas_S1.repository.EntradaRepository;
import com.duoc.Entradas_S1.repository.EventoRepository;
import com.duoc.Entradas_S1.repository.ResumenCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class EntradaService{

    private final EntradaRepository entradaRepo;

    private final EventoRepository eventoRepo;

    private final ResumenCompraRepository resumenCompraRepo;

    // inyección por constructor
    public EntradaService(
        EntradaRepository entradaRepo,
        EventoRepository eventoRepo,
        ResumenCompraRepository resumenCompraRepo
    ){
        this.entradaRepo = entradaRepo;
        this.eventoRepo = eventoRepo;
        this.resumenCompraRepo = resumenCompraRepo;
    }

    @Transactional // si falla la compra no se guarda ninguna entrad
    public ResumenCompraDTO comprarEntradas(Long eventoId, int cantidad){
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entradas debe ser mayor que cero");
        }

        // validamos que exista el evento
        Evento evento = eventoRepo.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("Evento no existe"));

        int precioUnit = evento.getPrecio();
        if (precioUnit < 0) {
            throw new IllegalArgumentException("El precio del evento no puede ser negativo");
        }
        int precioTotal = Math.multiplyExact(precioUnit, cantidad);

        // guardamos las entradas en la BD
        for(int i = 0; i < cantidad; i++){
            Entrada entrada = new Entrada();
            entrada.setEvento(evento);
            entrada.setPrecio(precioUnit);
            entradaRepo.save(entrada);
        }

        ResumenCompra resumen = resumenCompraRepo.save(
            new ResumenCompra(evento.getNombre(), cantidad, precioUnit, precioTotal)
        );

        return convertirADto(resumen);
    }

    private ResumenCompraDTO convertirADto(ResumenCompra resumen) {
        return ResumenCompraDTO.builder()
            .numeroResumen(resumen.getId())
            .nomEvento(resumen.getNombreEvento())
            .cantidadEntradas(resumen.getCantidadEntradas())
            .precioUnit(resumen.getPrecioUnitario())
            .precioTotal(resumen.getPrecioTotal())
            .build();
    }
}