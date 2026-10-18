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
    /** Etiquetas de tipo y procedencia (U-21: antes literales en código). */
    val TipoContexto = "Contexto"
    val TipoQr = "QR"
    val TipoActividad = "Actividad"
    val TipoComprobante = "Comprobante"
    val ProcedenciaEnviado = "Enviado"
    val ProcedenciaRecibido = "Recibido"
    val ProcedenciaDesconocida = "Origen desconocido"
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
    val shareFromAnotherAppHint = "Comparte una imagen o PDF con Agenda QR desde otra app."
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
    val sensitiveChangeWarning = "Esta actividad tiene comprobantes; no se modificarán."
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
    /** Error en vivo del importe (U-07, D6): validación real. */
    val ImporteInvalido = "Usa solo números y un decimal"
    /** Monedas del editor (U-05, D6): lista de presentación. */
    val MonedaBob = "BOB"
    val MonedaUsd = "USD"
    val MonedaOtra = "Otra…"
    /** Confirmación de descarte del editor (U-08, D7). */
    val DescartarCambios = "¿Descartar cambios?"
    val DescartarCambiosMensaje = "Si sales ahora, se perderán los cambios."
    val SeguirEditando = "Seguir editando"
    /** Divulgación progresiva del editor (U-06, D5). */
    val MasDetalles = "Más detalles"
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
    // (MonedaOpcional retirado en U-05: la moneda va en chips + campo
    // Otra… con LabelMoneda.)
    val Moneda = "Moneda: "
    val MontoOpcional = "Monto (opcional)"
    val MostrarQr = "Mostrar QR"
    val noAutomaticMatch = "Sin coincidencia automática. Elige una actividad."
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
    // (PodrasAdjuntar retirado en U-06: el editor muestra solo lo
    // definido en D5.)
    val PruebaConOtraPalabraO = "Prueba con otra palabra o revisa la ortografía."
    val PudoHaberSidoEliminada = "Pudo haber sido eliminada."
    val PudoHaberSidoEliminadoEn = "Pudo haber sido eliminado en otro dispositivo."
    val Qr = "QR"
    val QrSinContenido = "QR sin contenido"
    /** Estado explícito cuando el QR almacenado no puede mostrarse como imagen. */
    val QrNoSePudoMostrar = "No se pudo mostrar el código"
    val Reintentar = "REINTENTAR"
    val RevisarDatos = "Revisar datos"
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
    val PendienteDeRevision = "Pendiente de revisión"
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
    val descartarElementoWarning = "Sin guardar: si lo descartas, tendrás que importarlo."
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
    // Jerarquía financiera de Actividades y metadatos de detalle
    // (patrón de apps financieras de consumo, ADR-0015 y ADR-0021).
    // ------------------------------------------------------------------
    /** Filtro de tipo del historial: todas las actividades. */
    val Todos = "Todos"
    /** Encabezado del grupo de datos principales del editor (S07). */
    val DetallesDeLaActividad = "Detalles de la actividad"
    /** Encabezado del grupo de notas del editor (S07). */
    val Notas = "Notas"
    /** Rótulos de sección de los grupos del editor (S07). */
    val ImporteYMoneda = "Importe y moneda"
    val FechaYHora = "Fecha y hora"
    /** Metadatos: etiquetas cortas sin puntuación (fila compacta). */
    val LabelPersonaOEntidad = "Persona o entidad"
    val LabelMoneda = "Moneda"
    val LabelConcepto = "Concepto"
    /** Signo direccional del monto: cobro = entrada (+), pago = salida (−). */
    val SignoCobro = "+"
    val SignoPago = "−"
    // ------------------------------------------------------------------
    // Reaseguro y estados de apps de consumo (ADR-0015 y ADR-0021).
    // ------------------------------------------------------------------
    /** Píldora de reaseguro local-first (S01, siempre cierta). */
    val GuardadoEnTuTelefono = "Guardado en tu teléfono"
    val VerTodo = "Ver todo"
    val DisponibilidadTotal = "Disponibilidad total"
    // (EscanearQr retirado en U-10: acción duplicada de la tarjeta; la
    // entrada vive en S02 y en el hub.)
    val AbrirLaCamara = "Abrir la cámara"
    val RegistrarPago = "Registrar pago"
    val CargarActividad = "Cargar actividad"
    val QueQuieresAnadir = "¿Qué quieres añadir?"
    /** Hub de Inicio (U-10, D8): traer un QR a S02. */
    val TraerQr = "Traer un QR"
    // (MontoHint retirado en U-07: la afirmación de validación era falsa;
    // el error en vivo es ImporteInvalido.)
    // (CuantoOcurrio retirado en U-03: la fila de fecha muestra su valor;
    // la ayuda de texto libre desaparece con el selector.)
    // (MomentoRegistrado/FirmaTemporal retirados en U-06: jerga fuera del
    // editor; la fecha de alta sigue visible donde el dato existe.)
    val PuedesSinContexto = "Puedes registrar sin asociar un contexto"
    // (ModoLocal/SqliteLocal retirados en U-06/U-11: jerga fuera del
    // editor; el estado global comunica el modo.)
    /** Detalle de actividad: caja de metadatos temporales. */
    val FechaDeOperacion = "Fecha de la actividad"
    val RegistradoEnDispositivo = "Registrado en dispositivo"
    /** Acción de borrado del importe (accesible). */
    val BorrarImporte = "Borrar importe"
    // ------------------------------------------------------------------
    // Patrones de apps de consumo (ADR-0021): historial en tarjetas, favoritos en
    // carrusel, pestañas cápsula con conteo y dock de acciones del
    // detalle QR.
    // ------------------------------------------------------------------
    /** Pestañas del historial (con conteo real). */
    val Todas = "Todas"
    val ComprobantesSueltos = "Comprobantes sueltos"
    val RespaldoSeguro = "Respaldo seguro"
    val RegistrosLocales = "registros locales"
    /** Prefijo de la hora de registro local (metadato temporal). */
    val RegPrefijo = "Reg: "
    // (DeslizaParaVerMas retirado en U-10: instrucción en lugar de
    // indicio; la siguiente tarjeta asoma.)
    val TodosLosDestinos = "Todos los destinos"
    /** Encabezado de la lista cuando el carrusel ya muestra los favoritos. */
    val OtrosDestinos = "Otros destinos"
    /** Estado vacío propio del filtro Favoritos cuando no hay ninguno. */
    val AunNoHayFavoritos = "Aún no tienes favoritos"
    val MarcaUnDestinoConEstrella = "Marca un destino con la estrella para verlo aquí."
    /** Acción que sale del filtro Favoritos vacío hacia la lista completa. */
    val VerTodos = "Ver todos"
    val VerQr = "Ver QR"
    val AnadirDestino = "Añadir destino"
    // (ImportarImagen retirado en U-10: acción duplicada de la tarjeta;
    // la entrada vive en S02.)
    val AlmacenamientoLocal = "Almacenamiento 100% local"
    val DatosNuncaSalen = "Tus datos nunca salen de tu teléfono"
    val IndexacionSinInternet = "Indexación y análisis instantáneo sin internet."
    /** Detalle QR: guía de uso, acción de exhibición y dock. */
    val MuestraEsteCodigo = "Muéstralo para cobrar o compártelo para pagar."
    val PantallaCompleta = "Pantalla completa"
    val Copiar = "Copiar"
    val Copiado = "Copiado"
    /** Contenido del QR en el Detalle (U-12, D9): plegado del bloque
     * (reutiliza VerMas de S09). */
    val VerMenos = "Ver menos"
    val FechaDeAlta = "Fecha de alta"
    val EditarMetadatosReemplazarQr = "Editar metadatos / reemplazar QR"
    val IntegridadHistorica = "Editar este destino no cambia tus actividades guardadas."
    // (RequisitoMinimo retirado en U-06: el formulario ya lo comunica.)
    // ------------------------------------------------------------------
    // Acceso local (sin cuenta): configuración de PIN, desbloqueo y
    // bloqueo. El PIN protege el acceso en el dispositivo; NO es la
    // contraseña del sistema ni una credencial remota.
    // ------------------------------------------------------------------
    val ConfiguraTuPin = "Configura tu PIN"
    val ConfiguraTuPinDescripcion = "Protege el acceso aquí; no es el PIN del teléfono."
    /** Acción del paso "crear el código" del onboarding (S00, V1.16). */
    val Continuar = "Continuar"
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
    /** Tecla de borrado del teclado del PIN (S00): nombre accesible. */
    val BorrarDigito = "Borrar dígito"
    val CuentaYRespaldo = "Cuenta y respaldo"
    val CuentaConectada = "Cuenta conectada"
    val ConectarCuentaDescripcion = "Conectar una cuenta es opcional."
    /** Segunda línea de la cuenta opcional: que el dato sigue local. */
    val ConectarCuentaDetalle = "Habilita respaldo y sincronización; tus datos siguen aquí."
    val DesconectarCuenta = "Desconectar cuenta"
    val DatosLocalesSeConservan = "Al desconectar, tus datos locales se conservan aquí."
    // ------------------------------------------------------------------
    // Biometría (atajo de desbloqueo; el PIN sigue siendo obligatorio
    // como alternativa y la biometría nunca cifra datos).
    // ------------------------------------------------------------------
    val UsarBiometria = "Usar biometría"
    val BiometriaNoDisponible = "La biometría no está disponible aquí; usa tu PIN."
    // ------------------------------------------------------------------
    // Modo de demostración (datos ficticios aislados, sin cuenta ni red).
    // ------------------------------------------------------------------
    val ExplorarDemostracion = "Explorar demostración"
    val ExplorarDemostracionDescripcion = "Prueba la app con datos ficticios. No toca tu información."
    val ModoDemostracion = "Modo demostración · datos ficticios"
    val SalirDeDemostracion = "Salir de demostración"
    val DemostracionAislada = "Datos ficticios: nada se sincroniza con tu información."

    // Errores recuperables (UserFacingError). Sentence case.
    val ErrorReintentar = "Reintentar"
    val ErrorElegirOtraImagen = "Elegir otra imagen"
    val ErrorCerrar = "Cerrar"

    val ErrorSaveQrWhat = "No pudimos guardar el QR."
    val ErrorSaveQrData = "Tus cambios siguen en la pantalla."
    val ErrorDeleteQrWhat = "No pudimos eliminar el QR."
    val ErrorDeleteQrData = "El QR sigue guardado."
    val ErrorSaveOperationWhat = "No pudimos guardar la actividad."
    val ErrorSaveOperationData = "Tus cambios siguen en la pantalla."
    val ErrorDeleteOperationWhat = "No pudimos eliminar la actividad."
    val ErrorDeleteOperationData = "La operación sigue guardada."
    val ErrorSaveReceiptWhat = "No pudimos guardar el comprobante."
    val ErrorSaveReceiptData = "El comprobante no se guardó."
    val ErrorDeleteReceiptWhat = "No pudimos eliminar el comprobante."
    val ErrorDeleteReceiptData = "El comprobante sigue guardado."
    val ErrorComprobanteOpenWhat = "No pudimos abrir el comprobante."
    val ErrorComprobanteOpenData = "El comprobante sigue guardado."
    val ErrorSearchNetworkWhat = "No pudimos buscar. Revisa tu conexión."
    val ErrorSearchWhat = "No pudimos buscar."
    val ErrorSearchData = "Tu información está a salvo."
    val ErrorSignInNetworkWhat = "No pudimos conectarnos. Revisa tu conexión."
    val ErrorSignInWhat = "No pudimos iniciar sesión. Revisa tu correo y contraseña."
    val ErrorSignInData = "Tus credenciales no se guardaron."
    val ErrorSignUpNetworkWhat = "No pudimos conectarnos. Revisa tu conexión."
    val ErrorSignUpWhat = "No pudimos crear la cuenta. Revisa tus datos."
    val ErrorSignUpData = "No se creó ninguna cuenta."
    val ErrorSignOutWhat = "No pudimos cerrar sesión."
    val ErrorSignOutData = "Tu información está guardada en el dispositivo."
    val ErrorImportReadWhat = "No se pudo abrir el archivo compartido."
    val ErrorImportReadData = "Nada se guardó."
    val ErrorImportBatchSaveWhat = "No pudimos guardar la importación."
    val ErrorImportBatchSaveData = "Los elementos reconocidos siguen pendientes de guardar."
    val ErrorContextSaveWhat = "No pudimos crear el contexto."
    val ErrorContextSaveData = "No se guardó nada."
    val ErrorContextOpenWhat = "No encontramos este contexto."
    val ErrorContextOpenData = "Tus demás datos están bien."
    val ErrorCameraPermissionWhat = "No pudimos abrir la cámara."
    val ErrorCameraPermissionData = "Concede el permiso o usa Galería."
    val ErrorDuplicateReceiptWhat = "Ya existe un comprobante igual."
    val ErrorDuplicateReceiptData = "No se creó ninguna copia."
    val ErrorComprobanteNotFoundWhat = "No encontramos este comprobante."
    val ErrorComprobanteNotFoundData = "Pudo haber sido eliminado."
    val ErrorOperationNotFoundWhat = "No encontramos esta actividad."
    val ErrorOperationNotFoundData = "Pudo haber sido eliminada."
}
