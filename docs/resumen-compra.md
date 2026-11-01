# Resumen de compra y AWS S3

Cada compra persiste un número de resumen, que se devuelve junto con los datos de la compra. El resumen se genera como un archivo JSON descargable:

```http
GET /api/resumenes/{numeroResumen}/descargar
```

Configura `AWS_S3_BUCKET` y, opcionalmente, `AWS_REGION` (por defecto `us-east-1`). El SDK de AWS obtiene credenciales mediante su cadena estándar; la aplicación necesita permisos `s3:PutObject`, `s3:GetObject` y `s3:DeleteObject` sobre el bucket. Cada objeto se guarda en `/{numeroResumen}/resumen-{numeroResumen}.json`.

| Método | Endpoint | Acción |
|---|---|---|
| `POST` | `/api/resumenes/{numeroResumen}/archivo` | Genera y sube a S3 el resumen persistido |
| `GET` | `/api/resumenes/{numeroResumen}/archivo` | Descarga desde S3 el archivo del resumen |
| `PUT` | `/api/resumenes/{numeroResumen}/archivo` | Actualiza los datos del resumen y reemplaza el archivo en S3 |
| `DELETE` | `/api/resumenes/{numeroResumen}/archivo` | Elimina el archivo de S3 |

El cuerpo de `PUT` debe ser JSON con el mismo formato del archivo descargado. El número debe coincidir con el de la URL y el precio total debe ser igual a la cantidad por el precio unitario. Para actualizar el archivo, primero debe existir en S3; usa `POST` para subirlo.
