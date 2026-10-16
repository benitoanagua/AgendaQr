package com.agendaqr.destinations.presentation

/**
 * copy centralizado de la app (UI en español).
 *
 * El texto es copy CONGELADO de la spec UX (docs/04-ux): este objeto
 * no cambia una sola palabra, solo la centraliza para mantener una
 * única fuente de copy y preparar i18n sin tocar las pantallas.
 * Las plantillas con interpolación ($…) permanecen en sus call-sites
 * porque su valor es dinámico.
 *
 * DECISIÓN (D2): se evaluó migrar a composeResources/values/strings.xml
 * para i18n automático; se queda el objeto centralizado porque (1)
 * strings.xml exige regenerar los accessors por módulo y trasladar
 * también los androidMain/iosMain actuales, y (2) el copy está
 * congelado (no hay i18n en V1). La migración queda registrada como
 * paso previo obligatorio cuando V2 necesite idiomas.
 */
internal object AppStrings {
    val AbriendoComprobante = "Abriendo comprobante…"
    val ActividadReciente = "Actividad reciente"
    val ActividadesRecientes = "Actividades recientes"
    val attachReceiptNow = "Adjuntar comprobante ahora"
    val AgendaQr = "Agenda QR"
    // V1.1 (ADR-0005, S01): sección de entradas recientes y tile vivo.
    val Recientes = "Recientes"
    val emptyDestinationsHint = "Agrega tu primer QR para empezar."
    val associateToExistingOperation = "Asociar a actividad existente"
    val Anadir = "Añadir"
    val searchIntroHint = "Busca QR, actividades, contextos o comprobantes."
    val BuscandoEnAgendaQr = "Buscando en Agenda QR…"
    val Buscar = "Buscar"
    val searchPlaceholder = "QR, actividades y más"
    val BuscarEnAgendaQr = "Buscar en Agenda QR"
    val BuscarOperaciones = "Buscar actividades"
    val changeOperationWithReceipts = "Cambiar actividad con comprobantes"
    val Cancelar = "Cancelar"
    val CargandoContextos = "Cargando contextos…"
    val CargandoContexto = "Cargando contexto…"
    val Categoria = "Categoría"
    val Cerrar = "Cerrar"
    val CerrarSesion = "Cerrar sesión"
    val Cobro = "Cobro"
    val shareFromAnotherAppHint = "Comparte una imagen o PDF desde otra app con Agenda QR. La importación continuará aquí para revisar lo que se encontró."
    val Compartir = "Compartir"
    val Comprobante = "Comprobante"
    val receiptReceivedTitle = "Comprobante recibido"
    val Comprobante2 = "Comprobante · "
    val ComprobantesSinAsociar = "Comprobantes sin asociar"
    val Comprobantes = "Comprobantes"
    val ConceptoOpcional = "Concepto (opcional)"
    val Concepto = "Concepto: "
    val Contextos = "Contextos"
    val Contrasena = "Contraseña"
    val Correo = "Correo"
    val CrearCuenta = "Crear cuenta"
    val createOperationFromThis = "Crear nueva actividad con esto"
    val Camara = "Cámara"
    val Desasociar = "Desasociar"
    val Descartar = "Descartar"
    // copy que el DS exige al llamador (core/ui no hardcodea copy).
    val Mas = "Más"
    val CargarMas = "Cargar más"
    val NoHayMasElementos = "No hay más elementos"
    val Cargando = "Cargando…"
    val DestinoNoEncontrado = "QR no encontrado"
    val Inicio = "Inicio"
    val Detalle = "Detalle"
    val EditaElDestinoParaImportar = "Edita el QR para importar un código válido."
    val Editar = "Editar"
    val EditarQr = "Editar QR"
    val ElementoExistente = "Elemento existente"
    val ElementosQueNecesitanRevision = "Elementos que necesitan revisión"
    val Eliminar = "Eliminar"
    val EliminarComprobante = "Eliminar comprobante"
    val EliminarDestino = "Eliminar QR"
    val EliminarOperacion = "Eliminar actividad"
    val EncuadreElCodigoQr = "Encuadra el código QR" // ADR-0008: tuteo (antes "Encuadre")
    val irreversibleActionWarning = "Esta acción no se puede deshacer."
    val sensitiveChangeWarning = "Esta actividad tiene comprobantes. El comprobante no será modificado."
    val analyzingReceipt = "Estamos analizando el comprobante."
    val FaltaElCodigoQrUsa = "Falta el código QR: usa Cámara o Galería"
    val Fecha = "Fecha"
    val Hora = "Hora"
    val FechaDesconocida = "Fecha desconocida"
    /** Atajos del selector de fecha del editor (U-03, D2). */
    val Hoy = "Hoy"
    val Ayer = "Ayer"
    /** Confirmar del selector de fecha/hora (sin defaults hardcodeados). */
    val Elegir = "Elegir"
    /** S02 V1.2: acción única de galería con selección múltiple. */
    val ElegirDeLaGaleria = "Elegir de la galería"
    /** S12 Idle: acción para elegir un lote de imágenes. */
    val ElegirImagenes = "Elegir imágenes"
    /** S02 V1.2: PDF opcional (solo donde la plataforma lo soporta). */
    val ElegirArchivoPdf = "Elegir archivo (PDF)"
    val batchSavedKeepReviewNotice = "Guardado. Los elementos pendientes conservan su revisión."
    val savingRecognizedItems = "Guardando elementos reconocidos…"
    val Guardar = "Guardar"
    val GuardarCambio = "Guardar cambio"
    val GuardarCambios = "Guardar cambios"
    val GuardarDeTodosModos = "Guardar de todos modos"
    val GuardarReconocidos = "Guardar reconocidos"
    val IniciaSesionParaAccederA = "Inicia sesión para acceder a tu agenda."
    val IniciarSesion = "Iniciar sesión"
    val operationKeptOnReceiptDelete = "La actividad asociada se conservará."
    val LasOperacionesCandidatasYaNo = "Las actividades candidatas ya no están disponibles."
    val Limpiar = "Limpiar"
    val LosContextosAgrupanTusQr = "Los contextos agrupan tus QR y actividades."
    val MonedaOpcional = "Moneda (opcional)"
    val Moneda = "Moneda: "
    val MontoOpcional = "Monto (opcional)"
    val MostrarQr = "Mostrar QR"
    val noAutomaticMatch = "No encontramos una coincidencia automática. Puedes elegir manualmente una actividad."
    val NoHayQrParaRevisar = "No hay QR para revisar"
    val noUnassociatedReceipts = "No hay comprobantes sin asociar"
    val noOperationsAvailable = "No hay actividades disponibles."
    val NoPudimosMostrarElElemento = "No pudimos mostrar el elemento existente."
    val NoSePudoAbrirEl = "No se pudo abrir el comprobante"
    val Nombre = "Nombre"
    val Nota = "Nota"
    val NotaOpcional = "Nota (opcional)"
    val Nota2 = "Nota: "
    val Operaciones = "Actividades"
    val OperacionNoEncontrada = "Actividad no encontrada"
    val Origen = "Origen: "
    val Pago = "Pago"
    val PersonaOEntidadOpcional = "Persona o entidad (opcional)"
    val PersonaOEntidad = "Persona o entidad: "
    val PodrasAdjuntarComprobantesMasAdelante = "Podrás adjuntar comprobantes más adelante"
    val PruebaConOtraPalabraO = "Prueba con otra palabra o revisa la ortografía."
    val PudoHaberSidoEliminada = "Pudo haber sido eliminada."
    val PudoHaberSidoEliminadoEn = "Pudo haber sido eliminado en otro dispositivo."
    val Qr = "QR"
    val QrSinContenido = "QR sin contenido"
    /** Estado explícito cuando el QR almacenado no puede mostrarse como imagen. */
    val QrNoSePudoMostrar = "No se pudo mostrar el código"
    val Reintentar = "REINTENTAR"
    val ReintentarAhora = "Reintentar ahora"
    val ReintentarClasificacion = "Reintentar clasificación"
    val ResultadoDeImportacion = "Resultado de importación"
    val RevisarQr = "Revisar QR"
    val SeleccionarContexto = "Seleccionar contexto"
    val SinComprobanteAdjunto = "Sin comprobante adjunto"
    /** Indicador compacto de sin conexión (chip del AppStatusHost, §12 V1.4). */
    val SinConexion = "Sin conexión"
    val SinContextos = "Sin contextos"
    val SinContextosDisponibles = "Sin contextos disponibles."
    val SinResultados = "Sin resultados"
    val TraeAAgendaQrAlgo = "Trae a Agenda QR algo que ya tienes."
    val TraeVariosElementosAAgenda = "Trae varios elementos a Agenda QR."
    val VerBandejaDeRespaldos = "Ver bandeja de respaldos"
    val VerExistente = "Ver existente"
    val Volver = "Volver"
    val VolverALaLista = "Volver a la lista"
    val VolverAResultado = "Volver a resultado"
    val VuelveEImportaDesdeCamara = "Vuelve e importa desde Cámara o Galería."
    val DestinationRouteTransition = "destination_route_transition"
    // transición entre superficies (AppRoute) del stack raíz.
    val SurfaceRouteTransition = "surface_route_transition"
    val QrSinNombre = "QR sin nombre"
    val SinNombre = "Sin nombre"
    /** Monto vacío en texto de detalle/compartir de una actividad. */
    val SinMonto = "sin monto"
    /** Monto vacío en CompactUi de fila/lista (signo constante). */
    val SinMontoCorto = "—"
    /** ¿A cuál corresponde? — título del selector S11 con varias candidatas. */
    val ACualCorresponde = "¿A cuál corresponde?"
    /** Nombre visible de un elemento de importación con archivo conocido. */
    val ArchivoPrefijo = "Archivo: "
    /** Nombre visible de un elemento de importación sin extensión conocida. */
    val ElementoImportado = "Elemento importado"
    /** S09: conteo singular. */
    val UnQrListoParaGuardar = "1 QR listo para guardar"
    /** S09: conteo plural (se concatena al número). */
    val QrListosParaGuardarSufijo = " QR listos para guardar"
    /** S09: guardar varios a la vez. */
    val GuardarTodo = "Guardar todo"
    val VerMas = "Ver más"
    val Reconocidos = "Reconocidos"
    val Reconocido = "Reconocido"
    val PosiblesDuplicados = "Posibles duplicados"
    val PosibleDuplicado = "Posible duplicado"
    val PendientesDeRevision = "Pendientes de revisión"
    val CrearContexto = "Crear contexto"
    val Crear = "Crear"
    val QuitarContexto = "Quitar contexto"
    val ElNombreEsObligatorio = "El nombre es obligatorio"
    val TuInformacionEstaGuardadaEnElDispositivo = "Tu información está guardada en el dispositivo."
    val ComprobanteDuplicado = "Comprobante duplicado"
    val duplicateReceiptWarningPrefix = "Parece que este comprobante ya está guardado.\nArchivo: "
    val incomingReceiptExplanationPrefix = "Se guardará en tu bandeja de respaldos sin asociar.\nArchivo: "
    val Guardando = "Guardando…"
    // validación de auth + estado ocupado.
    val IngresaUnCorreoValido = "Ingresa un correo válido"
    // literales que esquivaban la regla D2 dentro de expresiones.
    // (FormatoFecha retirado en U-03: el texto libre dd/mm/aaaa
    // desaparece con el selector de fecha.)
    // permiso de cámara y lectura de QR.
    val AbrirAjustes = "Abrir ajustes"
    val PermisoCamaraContexto = "Usamos la cámara solo para escanear códigos QR."
    val CodigoQrDetectado = "Código QR detectado"
    // anuncios accesibles de estado (por evento).
    val SincronizacionCompletada = "Sincronización completada"
    val Eliminado = "Eliminado"
    val GuardadoOk = "Guardado"
    val guardadoParcial = "Guardado parcialmente"
    val noSeGuardóNada = "No se pudo guardar ningún elemento"
    val reintentarGuardado = "Reintentar guardado"
    /** Confirmación de descarte de un pendiente SIN clasificar (pérdida de contenido retenido). */
    val descartarElementoWarning = "Este elemento no se ha guardado. Al descartarlo perderás su contenido y tendrás que importarlo de nuevo."
    /** Etiqueta específica de la confirmación (difiere de la acción de fila «Descartar»). */
    val descartarElementoConfirmar = "Descartar elemento"
    val Minimo6Caracteres = "Mínimo 6 caracteres"
    /** Mostrar/ocultar contraseña (descripción accesible del botón). */
    val Mostrar = "Mostrar"
    val Ocultar = "Ocultar"
    val IniciandoSesion = "Iniciando sesión…"
    val AsociarAhora = "Asociar ahora"
    val AunNoHayOperaciones = "Aún no hay actividades"
    val NoHayCoincidencias = "No hay coincidencias"
    val registerFirstPaymentHint = "Registra tu primer pago o cobro."
    val tryAnotherSearchHint = "Prueba con otra búsqueda."
    val RegistrarOperacion = "Registrar actividad"
    val LimpiarBusqueda = "Limpiar búsqueda"
    val singleMatchTitle = "Parece corresponder a"
    val noMatchTitle = "No encontramos coincidencia"
    val QrImportado = "QR importado"
    val ComprobanteNoEncontrado = "Comprobante no encontrado"
    val Favoritos = "Favoritos"
    /** Descripción de estado accesible de los chips de filtro
     * seleccionados (U-01: la etiqueta no cambia; el estado va por
     * semántica `selected` + `stateDescription`). */
    val Seleccionado = "Seleccionado"
    /** S12: descripciones honestas de cada pendiente (guía de voz). */
    val elementoYaGuardado = "Parece que este elemento ya está guardado."
    val elementoSinClasificar = "No pudimos clasificar este elemento."
    val elementoPendienteRevision = "Elemento pendiente de revisión."
    val QuitarFavorito = "Quitar favorito"
    val MarcarFavorito = "Marcar favorito"
    val SeccionComprobantes = "Comprobantes"
    val checkMark = "✓ "
    val dupMark = "! "
    val pendingMark = "? "
    val ComprobanteAdjuntoVistaPreviaPendienteEnIos = "Comprobante adjunto (vista previa pendiente en iOS)"
    val PdfAdjuntoSinVistaPrevia = "PDF adjunto (sin vista previa)"
    val VistaPreviaNoDisponible = "Vista previa no disponible"
    /** Subtítulo de fila para destinos sin nombre: origen "creado" + fecha. */
    val AnadidoEl = "Añadido el "
    /** Subtítulo de fila para destinos importados: origen "importado" + fecha. */
    val ImportadoEl = "Importado el "
    val Contenido = "Contenido"
    val EditarOperacion = "Editar actividad"
    val AunNoHayDestinos = "Aún no hay QR"
    val NoSeEncontraronDestinos = "No se encontraron QR"
    val PruebaConOtraBusquedaOLimpia = "Prueba con otra búsqueda o limpia los filtros."
    val ParaContextoElegido = "Para: "
    val ParaElegirContexto = "Para: elegir contexto (opcional)"
    val ContextoGenerico = "Contexto"
    // ------------------------------------------------------------------
    // Referencia visual Stitch (Ant Design Mobile): jerarquía financiera
    // de Actividades y metadatos de detalle (ADR-0015).
    // ------------------------------------------------------------------
    /** Filtro de tipo del historial: todas las actividades. */
    val Todos = "Todos"
    /** Encabezado del grupo de datos principales del editor (S07). */
    val DetallesDeLaActividad = "Detalles de la actividad"
    /** Encabezado del grupo de notas del editor (S07). */
    val Notas = "Notas"
    /** Metadatos: etiquetas cortas sin puntuación (fila compacta). */
    val LabelPersonaOEntidad = "Persona o entidad"
    val LabelMoneda = "Moneda"
    val LabelConcepto = "Concepto"
    /** Signo direccional del monto: cobro = entrada (+), pago = salida (−). */
    val SignoCobro = "+"
    val SignoPago = "−"
    // ------------------------------------------------------------------
    // Referencia Stitch (ADR-0015).
    // ------------------------------------------------------------------
    /** Píldora de reaseguro local-first (S01, siempre cierta). */
    val GuardadoEnTuTelefono = "Guardado en tu teléfono"
    val VerTodo = "Ver todo"
    val DisponibilidadTotal = "Disponibilidad total"
    val EscanearQr = "Escanear QR"
    val AbrirLaCamara = "Abrir la cámara"
    val RegistrarPago = "Registrar pago"
    val CargarActividad = "Cargar actividad"
    val QueQuieresAnadir = "¿Qué quieres añadir?"
    val GuardaUnComprobanteOMovimiento = "Guarda un comprobante o movimiento"
    /** S07: ayuda del importe y momento de la operación. */
    val MontoHint = "Ingreso numérico rápido y validado en tiempo real."
    // (CuantoOcurrio retirado en U-03: la fila de fecha muestra su valor;
    // la ayuda de texto libre desaparece con el selector.)
    val MomentoRegistrado = "Momento registrado"
    val FirmaTemporal = "Firma temporal del sistema"
    val PuedesSinContexto = "Puedes registrar sin asociar un contexto"
    val ModoLocal = "Modo local · En tu dispositivo"
    val SqliteLocal = "SQLite local"
    /** Detalle de actividad: caja de metadatos temporales. */
    val FechaDeOperacion = "Fecha de operación"
    val RegistradoEnDispositivo = "Registrado en dispositivo"
    /** Acción de borrado del importe (accesible). */
    val BorrarImporte = "Borrar importe"
    // ------------------------------------------------------------------
    // Referencia Stitch MCP (Ant Design Mobile App): historial en tarjetas,
    // favoritos en carrusel, pestañas cápsula con conteo y dock de
    // acciones del detalle QR.
    // ------------------------------------------------------------------
    /** Pestañas del historial (con conteo real). */
    val Todas = "Todas"
    val ComprobantesSueltos = "Comprobantes sueltos"
    val RespaldoSeguro = "Respaldo seguro"
    val RegistrosLocales = "registros locales"
    /** Prefijo de la hora de registro local (metadato temporal). */
    val RegPrefijo = "Reg: "
    /** Inicio: sección de favoritos deslizables. */
    val DestinosFrecuentes = "Destinos frecuentes"
    val DeslizaParaVerMas = "Desliza para ver más"
    val TodosLosDestinos = "Todos los destinos"
    val VerQr = "Ver QR"
    val AnadirDestino = "Añadir destino"
    val ImportarImagen = "Importar imagen"
    val AlmacenamientoLocal = "Almacenamiento 100% local"
    val DatosNuncaSalen = "Tus datos nunca salen de tu teléfono"
    val IndexacionSinInternet = "Indexación y análisis instantáneo sin internet."
    /** Detalle QR: guía de uso, acción de exhibición y dock. */
    val MuestraEsteCodigo = "Muestra este código para cobrar o escanéalo para transferir"
    val PantallaCompleta = "Pantalla completa"
    val Copiar = "Copiar"
    val Copiado = "Copiado"
    val FechaDeAlta = "Fecha de alta"
    val EditarMetadatosReemplazarQr = "Editar metadatos / reemplazar QR"
    val IntegridadHistorica = "Los cambios a este destino no modifican operaciones históricas asociadas."
    /** Editor: requisito mínimo honesto (RF-08/RF-09). */
    val RequisitoMinimo = "Requisito mínimo: tipo y momento ocurrido. Todos los demás campos son opcionales."
    // ------------------------------------------------------------------
    // Acceso local (sin cuenta): configuración de PIN, desbloqueo y
    // bloqueo. El PIN protege el acceso en el dispositivo; NO es la
    // contraseña del sistema ni una credencial remota.
    // ------------------------------------------------------------------
    val ConfiguraTuPin = "Configura tu PIN"
    val ConfiguraTuPinDescripcion = "Tu PIN protege el acceso en este dispositivo. No es el código de tu teléfono."
    val Pin = "PIN"
    val ConfirmaElPin = "Confirma el PIN"
    val GuardarPin = "Guardar PIN"
    val DesbloqueaAgendaQr = "Desbloquea Agenda QR"
    val DesbloqueaAgendaQrDescripcion = "Introduce tu PIN para continuar."
    val Desbloquear = "Desbloquear"
    val Bloquear = "Bloquear"
    val PinErrorCorto = "El PIN debe tener al menos 4 dígitos"
    val PinErrorLargo = "El PIN no puede tener más de 12 dígitos"
    val PinErrorSoloDigitos = "El PIN solo puede contener dígitos"
    val PinErrorNoCoinciden = "Los PIN no coinciden"
    val PinErrorIncorrecto = "PIN incorrecto"
    val PinErrorBloqueado = "Demasiados intentos. Espera antes de reintentar."
    val IntentosRestantes = "Intentos restantes: "
    val SoloEnEsteDispositivo = "Solo en este dispositivo"
    val CuentaYRespaldo = "Cuenta y respaldo"
    val CuentaConectada = "Cuenta conectada"
    val ConectarCuentaDescripcion = "Conectar una cuenta es opcional. Tus datos siguen en tu teléfono; la cuenta solo habilita respaldo y sincronización."
    val DesconectarCuenta = "Desconectar cuenta"
    val DatosLocalesSeConservan = "Al desconectar, los datos locales se conservan en este dispositivo."
    // ------------------------------------------------------------------
    // Biometría (atajo de desbloqueo; el PIN sigue siendo obligatorio
    // como alternativa y la biometría nunca cifra datos).
    // ------------------------------------------------------------------
    val UsarBiometria = "Usar biometría"
    val BiometriaNoDisponible = "La biometría no está disponible en este dispositivo. Usa tu PIN."
    // ------------------------------------------------------------------
    // Modo de demostración (datos ficticios aislados, sin cuenta ni red).
    // ------------------------------------------------------------------
    val ExplorarDemostracion = "Explorar demostración"
    val ExplorarDemostracionDescripcion = "Prueba la app con datos ficticios. No toca tu información real."
    val ModoDemostracion = "Modo demostración · datos ficticios"
    val SalirDeDemostracion = "Salir de demostración"
    val DemostracionAislada = "Estás explorando con datos ficticios. Nada se sincroniza ni se mezcla con tu información."
}
