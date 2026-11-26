# Agenda QR — mapa semántico de iconos Phosphor

**Estado:** destino de migración ADR-0052; no significa que todos los mappings estén implementados.  
**Fuente:** Phosphor Icons `@phosphor-icons/core` v2.0.8, MIT.  
**API pública:** `AqIcons` en `core/ui`; los features no importan vectores del paquete ni crean iconos.

| Concepto Agenda QR | Glifo Phosphor objetivo | Peso base | Nota de revisión |
|---|---|---|---|
| Search | MagnifyingGlass | Regular | Búsqueda general |
| Add | Plus | Regular | Acción de agregar |
| Register | Receipt | Regular | Registro/recibo; validar contexto comercial |
| Favorite | Star | Regular | Estado favorito; Filled solo cuando esté seleccionado |
| Context | Folder | Regular | Contexto/colección |
| Back | ArrowLeft | Regular | Navegación |
| Cancel | XCircle | Regular | Acción cancelar; diferenciar de Close |
| Close | X | Regular | Cierre sin círculo |
| More | DotsThree | Regular | Menú de más acciones |
| Home | House | Regular | Inicio |
| Gallery | Image | Regular | Galería/imagen |
| Camera | Camera | Regular | Captura |
| Info | Info | Regular | Información |
| Warning | Warning | Regular | Aviso |
| CircleCheck | CheckCircle | Regular | Estado completado |
| Check | Check | Regular | Selección simple |
| QrCode | QrCode | Regular | QR |
| AlertCircle | WarningCircle | Regular | Alerta con círculo |
| Inbox | Tray | Regular | Bandeja |
| ScanLine | Scan | Regular | Escaneo |
| Income | ArrowDown | Regular | Entrada; dirección confirmada por layout/copy |
| Expense | ArrowUp | Regular | Salida; dirección confirmada por layout/copy |
| Attachment | Paperclip | Regular | Archivo adjunto |
| Store | Basket | Regular | Comercio |
| Calendar | CalendarBlank | Regular | Calendario |
| Clock | Clock | Regular | Hora |
| ChevronRight | CaretRight | Regular | Indicador de navegación |
| ShieldCheck | ShieldCheck | Regular | Seguridad/verificación |
| Wallet | Wallet | Regular | Billetera |
| Lock | LockKey | Regular | Bloqueo |
| Erase | Backspace | Regular | Borrar texto; no es Delete |
| NoteEdit | NotePencil | Regular | Edición de nota |
| Edit | PencilSimple | Regular | Editar |
| Trash | Trash | Regular | Eliminar |
| Share | ShareNetwork | Regular | Compartir |
| Copy | Copy | Regular | Copiar |
| Maximize | ArrowsOut | Regular | Expandir; verificar frente a CornersOut en captura |

## Reglas de peso y estado

- **Regular:** peso base de iconografía de navegación, campos, acciones y contenido.
- **Bold:** controles pequeños de estado y acciones de alto énfasis (por ejemplo, Check/X dentro de `AqSwitch`).
- **Filled:** solo estados seleccionados/activos cuando el significado del estado lo requiera (por ejemplo, favorito activo); no usarlo como sustituto arbitrario de Regular.
- **Light/Thin:** no son el default de producción; solo se usan si el catálogo o una comparación de referencia define explícitamente el peso.
- Los iconos semánticos deben mantener caja óptica y tamaño del slot Moon; no se cambian las medidas del componente para compensar un glifo diferente.

## Verificación requerida por mapping

Cada fila debe quedar cubierta por:
1. una propiedad `AqIcons` que devuelve el vector Phosphor exacto;
2. un test que compara `ImageVector.name` con el nombre esperado y confirma dimensiones no nulas;
3. una captura de galería con tema claro/oscuro para revisar peso, caja óptica y significado;
4. búsqueda/gate que confirme que no existe una referencia productiva directa a Moon Icons.

Las equivalencias de Register, Cancel, Warning/AlertCircle, Income/Expense y Maximize requieren revisión visual de contexto antes de cerrar ADR-0052.
