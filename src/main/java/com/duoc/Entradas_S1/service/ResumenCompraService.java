package com.duoc.Entradas_S1.service;

import com.duoc.Entradas_S1.dto.ActualizarResumenCompraDTO;
import com.duoc.Entradas_S1.dto.ResumenCompraDTO;
import com.duoc.Entradas_S1.model.ResumenCompra;
import com.duoc.Entradas_S1.repository.ResumenCompraRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class ResumenCompraService {

    private static final String CONTENT_TYPE = "application/json";

    private final ResumenCompraRepository resumenCompraRepo;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final String bucket;

    public ResumenCompraService(
        ResumenCompraRepository resumenCompraRepo,
        ObjectMapper objectMapper,
        S3Client s3Client,
        @Value("${aws.s3.bucket:}") String bucket
    ) {
        this.resumenCompraRepo = resumenCompraRepo;
        this.objectMapper = objectMapper;
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public byte[] generarArchivo(Long numeroResumen) {
        return serializar(convertirADto(obtenerResumen(numeroResumen)));
    }

    public void subirArchivo(Long numeroResumen) {
        subirArchivo(numeroResumen, generarArchivo(numeroResumen));
    }

    public byte[] descargarArchivo(Long numeroResumen) {
        obtenerResumen(numeroResumen);
        validarBucketConfigurado();

        try {
            ResponseBytes<GetObjectResponse> archivo = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(obtenerClave(numeroResumen))
                    .build()
            );
            return archivo.asByteArray();
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                throw new ArchivoResumenNoEncontradoException(numeroResumen);
            }
            throw exception;
        }
    }

    @Transactional
    public ResumenCompraDTO actualizarResumen(Long numeroResumen, ActualizarResumenCompraDTO actualizacion) {
        validarActualizacion(numeroResumen, actualizacion);
        validarBucketConfigurado();
        verificarArchivoExiste(numeroResumen);

        ResumenCompra resumen = obtenerResumen(numeroResumen);
        resumen.setNombreEvento(actualizacion.nomEvento());
        resumen.setCantidadEntradas(actualizacion.cantidadEntradas());
        resumen.setPrecioUnitario(actualizacion.precioUnit());
        resumen.setPrecioTotal(actualizacion.precioTotal());
        resumenCompraRepo.save(resumen);

        byte[] archivo = serializar(convertirADto(resumen));
        subirArchivo(numeroResumen, archivo);
        return convertirADto(resumen);
    }

    public void borrarArchivo(Long numeroResumen) {
        obtenerResumen(numeroResumen);
        validarBucketConfigurado();
        verificarArchivoExiste(numeroResumen);
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(obtenerClave(numeroResumen))
                .build()
        );
    }

    private void subirArchivo(Long numeroResumen, byte[] archivo) {
        validarBucketConfigurado();
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(obtenerClave(numeroResumen))
                .contentType(CONTENT_TYPE)
                .build(),
            RequestBody.fromBytes(archivo)
        );
    }

    private void verificarArchivoExiste(Long numeroResumen) {
        try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(obtenerClave(numeroResumen))
                    .build()
            );
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                throw new ArchivoResumenNoEncontradoException(numeroResumen);
            }
            throw exception;
        }
    }

    private ResumenCompra obtenerResumen(Long numeroResumen) {
        return resumenCompraRepo.findById(numeroResumen)
            .orElseThrow(() -> new ResumenCompraNoEncontradoException(numeroResumen));
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

    private byte[] serializar(ResumenCompraDTO resumen) {
        try {
            return objectMapper.writeValueAsBytes(resumen);
        } catch (JacksonException exception) {
            throw new IllegalStateException("No se pudo generar el archivo del resumen de compra", exception);
        }
    }

    private String obtenerClave(Long numeroResumen) {
        return numeroResumen + "/resumen-" + numeroResumen + ".json";
    }

    private void validarBucketConfigurado() {
        if (bucket.isBlank()) {
            throw new IllegalStateException("Debe configurar la variable AWS_S3_BUCKET");
        }
    }

    private void validarActualizacion(Long numeroResumen, ActualizarResumenCompraDTO actualizacion) {
        if (actualizacion == null) {
            throw new IllegalArgumentException("El contenido de la actualización es obligatorio");
        }
        if (!numeroResumen.equals(actualizacion.numeroResumen())) {
            throw new IllegalArgumentException("El número de resumen del contenido no coincide con la ruta");
        }
        if (actualizacion.nomEvento() == null
            || actualizacion.nomEvento().isBlank()
            || actualizacion.nomEvento().length() > 100) {
            throw new IllegalArgumentException("El nombre del evento debe tener entre 1 y 100 caracteres");
        }
        if (actualizacion.cantidadEntradas() <= 0 || actualizacion.precioUnit() < 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero y el precio no puede ser negativo");
        }
        int total = Math.multiplyExact(actualizacion.cantidadEntradas(), actualizacion.precioUnit());
        if (actualizacion.precioTotal() != total) {
            throw new IllegalArgumentException("El precio total debe coincidir con la cantidad por el precio unitario");
        }
    }
}
