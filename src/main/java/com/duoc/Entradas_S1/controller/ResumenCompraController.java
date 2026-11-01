package com.duoc.Entradas_S1.controller;

import com.duoc.Entradas_S1.dto.ActualizarResumenCompraDTO;
import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.service.ResumenCompraService;
import java.net.URI;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumenes")
public class ResumenCompraController {

    private final ResumenCompraService resumenCompraService;

    public ResumenCompraController(ResumenCompraService resumenCompraService) {
        this.resumenCompraService = resumenCompraService;
    }

    @GetMapping("/{numeroResumen}/descargar")
    public ResponseEntity<byte[]> descargarResumen(@PathVariable Long numeroResumen) {
        return respuestaArchivo(numeroResumen, resumenCompraService.generarArchivo(numeroResumen));
    }

    @PostMapping("/{numeroResumen}/archivo")
    public ResponseEntity<Void> subirResumen(@PathVariable Long numeroResumen) {
        resumenCompraService.subirArchivo(numeroResumen);
        return ResponseEntity.created(URI.create("/api/resumenes/" + numeroResumen + "/archivo")).build();
    }

    @PutMapping("/{numeroResumen}/archivo")
    public ResponseEntity<ResumenCompraDTO> actualizarResumen(
        @PathVariable Long numeroResumen,
        @RequestBody ActualizarResumenCompraDTO actualizacion
    ) {
        return ResponseEntity.ok(resumenCompraService.actualizarResumen(numeroResumen, actualizacion));
    }

    @GetMapping("/{numeroResumen}/archivo")
    public ResponseEntity<byte[]> descargarArchivoS3(@PathVariable Long numeroResumen) {
        return respuestaArchivo(numeroResumen, resumenCompraService.descargarArchivo(numeroResumen));
    }

    @DeleteMapping("/{numeroResumen}/archivo")
    public ResponseEntity<Void> borrarArchivo(@PathVariable Long numeroResumen) {
        resumenCompraService.borrarArchivo(numeroResumen);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<byte[]> respuestaArchivo(Long numeroResumen, byte[] contenido) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .contentLength(contenido.length)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename("resumen-" + numeroResumen + ".json")
                    .build()
                    .toString()
            )
            .body(contenido);
    }
}
