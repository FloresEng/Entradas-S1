package com.duoc.Entradas_S1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.duoc.Entradas_S1.dto.ActualizarResumenCompraDTO;
import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.model.ResumenCompra;
import com.duoc.Entradas_S1.repository.ResumenCompraRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ResumenCompraServiceTest {

    @Mock
    private ResumenCompraRepository resumenCompraRepo;

    @Mock
    private S3Client s3Client;

    private ResumenCompraService service;

    @BeforeEach
    void setUp() {
        service = new ResumenCompraService(
            resumenCompraRepo,
            new ObjectMapper(),
            s3Client,
            "resumenes-test"
        );
    }

    @Test
    void generarArchivoIncluyeElNumeroYLosDatosDeLaCompra() throws Exception {
        when(resumenCompraRepo.findById(23L)).thenReturn(Optional.of(crearResumen()));

        JsonNode archivo = new ObjectMapper().readTree(service.generarArchivo(23L));

        assertEquals(23, archivo.get("Número de Resumen").asLong());
        assertEquals("Concierto", archivo.get("Nombre de Evento").asText());
        assertEquals(2, archivo.get("Cantidad de Entradas").asInt());
        assertEquals(15000, archivo.get("Precio Total").asInt());
    }

    @Test
    void subirArchivoGuardaElJsonEnLaCarpetaDelNumeroDeResumen() {
        when(resumenCompraRepo.findById(23L)).thenReturn(Optional.of(crearResumen()));

        service.subirArchivo(23L);

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(request.capture(), any(RequestBody.class));
        assertEquals("resumenes-test", request.getValue().bucket());
        assertEquals("23/resumen-23.json", request.getValue().key());
        assertEquals("application/json", request.getValue().contentType());
        assertTrue(request.getValue().key().startsWith("23/"));
    }

    @Test
    void actualizarResumenActualizaLosDatosYReemplazaElArchivoEnS3() {
        when(resumenCompraRepo.findById(23L)).thenReturn(Optional.of(crearResumen()));
        ActualizarResumenCompraDTO actualizacion = new ActualizarResumenCompraDTO(
            23L,
            "Concierto actualizado",
            3,
            10000,
            30000
        );

        ResumenCompraDTO actualizado = service.actualizarResumen(23L, actualizacion);

        assertEquals(30000, actualizado.getPrecioTotal());
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(resumenCompraRepo).save(any(ResumenCompra.class));
    }

    @Test
    void actualizarResumenRechazaUnTotalInconsistenteSinModificarS3() {
        ActualizarResumenCompraDTO actualizacion = new ActualizarResumenCompraDTO(
            23L,
            "Concierto",
            2,
            7500,
            10000
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> service.actualizarResumen(23L, actualizacion)
        );

        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    private ResumenCompra crearResumen() {
        ResumenCompra resumen = new ResumenCompra("Concierto", 2, 7500, 15000);
        resumen.setId(23L);
        return resumen;
    }
}
