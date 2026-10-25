package com.duoc.Entradas_S1.controller;

import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.service.EntradaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/entradas")
public class EntradaController{

    private final EntradaService entradaService;

    public EntradaController(EntradaService entradaService){
        this.entradaService = entradaService;
    }

    @PostMapping("/comprar")
    public ResponseEntity<ResumenCompraDTO> comprar(@RequestParam Long eventoId, @RequestParam int cantidad){
        ResumenCompraDTO resumen = entradaService.comprarEntradas(eventoId, cantidad);

        return ResponseEntity.ok(resumen);
    }
}