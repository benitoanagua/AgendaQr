# Validación — Simulaciones V1

## Naturaleza

Se utilizaron escenarios sintéticos y perfiles de uso para estresar el modelo conceptual. No constituyen evidencia de mercado.

## Perfiles

- usuario casual;
- usuario intensivo;
- pequeño negocio;
- usuario que cobra con frecuencia;
- usuario que paga con frecuencia;
- usuario desorganizado;
- usuario que guarda todo;
- usuario que abandona metadatos;
- usuario propenso a errores.

## Casos adversariales

- comprobante sin operación;
- operación sin comprobante;
- comprobante duplicado;
- mismo comprobante usado en contextos distintos;
- asociación incorrecta;
- comprobante recibido tarde;
- múltiples comprobantes;
- QR reemplazado;
- eliminación de operación;
- eliminación de comprobante;
- operaciones similares en fecha/importe/persona;
- comprobante ilegible;
- comprobante recibido de otra persona;
- búsqueda de evidencia antigua;
- registro bajo prisa;
- registro completamente organizado.

## Resultado

El modelo V1 mantiene separados:

```text
Destino QR
Operación
Comprobante
```

y permite combinarlos sin obligar a que todos existan al mismo tiempo.
