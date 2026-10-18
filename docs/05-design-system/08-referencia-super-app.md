# Referencia de dirección visual Super-App

Fuente de la dirección visual vigente según ADR-0022.

> Especificación Arquitectónica de Diseño · Estilo Super-App China (Alta Densidad)

## I. Manifiesto

Esta filosofía representa el **maximalismo funcional y la gamificación visual**. No es el minimalismo etéreo ni la frialdad corporativa: es el "bazar digital" llevado a su máxima eficiencia. La interfaz es un ecosistema vivo, denso y vibrante donde cada píxel compite por la atención del usuario, pero donde la navegación sigue siendo sorprendentemente intuitiva para el usuario nativo.

El diseño de Super-App no es "recargado" por falta de gusto; es recargado por **abundancia de utilidad**. Cada banner, badge y gradiente comunica una oportunidad, un descuento o un estado. La decoración no es un adorno; es un mecanismo de engagement y conversión.

**Principio rector:** La densidad es utilidad. La vibrancia es retención. Todo está a un toque de distancia.

## II. Teoría del Color — Paleta Vibrante y Comercial

### Fondos

| Token | Valor | Descripción |
| --- | --- | --- |
| Fondo base (claro) | `#F5F6F8` | Gris azulado muy claro. Contrasta con las tarjetas blancas. |
| Superficie (Tarjetas) | `#FFFFFF` | Blanco puro. El lienzo para la alta densidad de información. |
| Fondo base (oscuro) | `#0A0A0A` | Negro profundo para modos inmersivos o video. |
| Superficie oscura | `#1A1A1A` | Para tarjetas en modo oscuro, con bordes sutiles. |

### Acentos y Semántica Comercial

| Token | Valor | Uso |
| --- | --- | --- |
| Primario (Rojo Comercio) | `#FF2442` | Acciones principales, precios, ofertas, "Comprar". |
| Secundario (Naranja Promo) | `#FF6A00` | Gradientes, badges de "Hot", urgencia, recompensas. |
| Confianza (Azul Fintech) | `#1677FF` | Pagos, seguridad, estados neutrales, enlaces. |
| Éxito / Dinero | `#00B578` | Saldo, ganancias, confirmaciones, cupones canjeados. |
| Texto principal | `#1A1A1A` | Casi negro. Máximo contraste para lectura densa. |
| Texto secundario | `#666666` | Gris medio. Descripciones, metadatos. |
| Texto deshabilitado | `#999999` | Para elementos inactivos o precios tachados. |

*Nota: Los gradientes lineales (ej. de `#FF2442` a `#FF6A00`) son obligatorios en botones primarios y banners.*

## III. Geometría y Morfología — Densidad y Redondez

El sistema prioriza el aprovechamiento del espacio y la amigabilidad visual mediante formas redondeadas que contrastan con la densidad de la información.

### Radios de borde

| Elemento | Radio | Sensación |
| --- | --- | --- |
| Tarjetas / Paneles | 12px – 16px | Suave, contenido, tipo "burbuja". |
| Botones primarios | 20px – 24px (Pill) | Llamativo, táctil, irresistible. |
| Inputs / Buscador | 20px (Pill) | Amigable, fácil de pulsar con el pulgar. |
| Iconos de menú (Grid) | 12px – 16px | Contenidos, ilustrativos. |
| Badges / Etiquetas | 4px – 8px | Angulares, tipo "ticket" o "cinta". |

### Grid y estructura

- Rejilla de 4 o 5 columnas para iconos (estilo "King Kong").
- Gutters extremadamente tight: 8px – 12px.
- Márgenes de página: 12px (móvil) para maximizar área útil.
- Altura de filas comprimida. Padding interno de tarjetas: 12px.
- Uso intensivo de divisores finos (`1px solid #EEEEEE`) para separar listas densas.

## IV. Tipografía — Jerarquía de Impacto

La tipografía está diseñada para escanear rápidamente datos financieros, precios y estados de entrega.

### Familias

| Rol | Tipo | Características |
| --- | --- | --- |
| Display / Precios | Sans-serif condensada o de alto impacto | Números grandes, rojos, con el símbolo de moneda más pequeño. |
| Cuerpo / CJK | Sans-serif optimizada para CJK | PingFang SC, Noto Sans SC, o equivalente. Trazos claros. |
| Datos / Métricas | Monoespaciada o Tabular Lining | Para alinear precios y cuentas regresivas perfectamente. |

### Escala tipográfica y Tratamiento de Precios

| Nivel | Tamaño | Peso | Uso |
| --- | --- | --- | --- |
| Precio Hero | 28px–32px | 800 (Black) | Precio principal en e-commerce. |
| H1 / Título Card | 18px–20px | 600 | Nombre de producto o servicio. |
| H2 / Sección | 16px | 600 | Títulos de secciones dentro de cards. |
| Body | 14px | 400 | Descripciones (ligeramente más pequeño que el estándar occidental). |
| Caption / Meta | 12px | 400 | "Vendido 10k+", "Envío gratis". |
| Símbolo Moneda | 14px | 700 | Siempre más pequeño que el número del precio. |

## V. Sistema de Elevación — Bloques y Colores

La elevación no se logra solo con sombras, sino mediante el contraste de bloques de color y bordes sutiles.

### Sombras

| Nivel | Especificación | Uso |
| --- | --- | --- |
| E-0 | Sin sombra | Contenido inline, listas. |
| E-1 | `0 2px 8px rgba(0,0,0,0.04)` | Tarjetas estándar sobre fondo gris. |
| E-2 | `0 4px 16px rgba(0,0,0,0.08)` | Cards destacadas, carruseles. |
| E-3 (Colorida) | `0 8px 24px rgba(255,36,66,0.20)` | Botones de acción principal (flotantes). |

### Elevación por color (Zonificación)

- Fondo global: `#F5F6F8` (Gris muy claro).
- Tarjetas: `#FFFFFF` (Blanco).
- Zonas de acción/Header: Gradientes o colores sólidos vibrantes.

## VI. Interacción y Cinética — Respuesta Recompensante

El movimiento es gamificado. Cada interacción debe sentirse como una pequeña victoria o una confirmación táctil.

### Curvas de tiempo

- Easing base: `cubic-bezier(0.34, 1.56, 0.64, 1)` (Spring / Rebote suave).
- Duración: 200ms – 300ms. Las animaciones son elásticas y juguetonas.
- Transiciones de página: Deslizamientos rápidos con ligera escala (push/pop).

### Estados y Micro-interacciones

| Estado | Comportamiento |
| --- | --- |
| Reposo | Elementos con gradientes sutiles o brillos animados (shimmer) en banners. |
| Tap / Press | `scale(0.95)` rápido. **Haptic feedback** (vibración suave) obligatorio. |
| Loading | No solo spinners. Mascotas animadas, monedas cayendo, o barras de progreso con texto motivacional. |
| Pull-to-refresh | Animación personalizada a pantalla completa (ej. un cohete, un sobre rojo abriéndose). |
| Success | Confeti, checkmark animado con rebote, sonido sutil o vibración doble. |
| Add to Cart | El ícono del producto "vuela" en una curva parabólica hacia el carrito en la barra inferior. |

## VII. Materialidad de Componentes

### Tarjeta "Bazar Denso"

- **Constructo:** Rectángulo 12px–16px radius.
- **Material:** Fondo `#FFFFFF`.
- **Interior:** Múltiples sub-secciones. Imagen a la izquierda, texto a la derecha, precio en rojo abajo, badge de "Oferta" superpuesto en la esquina de la imagen.
- **Borde:** Sin borde, usa sombra E-1 sobre fondo gris.

### Botón "Gradiente Irresistible"

- **Forma:** Pill (radio completo).
- **Material:** Gradiente lineal (ej. Rojo a Naranja).
- **Texto:** Blanco, negrita, tracking ligeramente positivo.
- **Efecto:** Sombra inferior del color del gradiente para dar efecto de "gordito" (neumorfismo suave).
- **Tamaño:** Altura mínima 44px, preferiblemente 48px.

### Menú "King Kong" (Grid de Iconos)

- **Constructo:** Grid de 4x2 o 5x2.
- **Material:** Iconos ilustrativos (3D o flat vibrante) dentro de un círculo o cuadrado redondeado de color pastel suave.
- **Texto:** 12px, centrado debajo del ícono. Máximo 4 caracteres (o 2 caracteres chinos).

### Etiqueta / Badge "Cinta Promocional"

- **Constructo:** Rectángulo pequeño con esquinas de 4px.
- **Material:** Fondo rojo o naranja sólido, texto blanco de 10px-12px.
- **Uso:** "HOT", "限时" (Tiempo limitado), "Nuevo", "-50%". Se superpone a imágenes o se coloca junto a precios.

## VIII. Principios de Composición

1. **Densidad por encima del espacio en blanco:** Aprovecha cada milímetro. El espacio en blanco solo se usa para separar bloques lógicos, no dentro de ellos.
2. **Gamificación visual:** Usa barras de progreso, cuentas regresivas, monedas virtuales y niveles para motivar la interacción.
3. **Jerarquía por color y tamaño:** Los precios y las acciones críticas deben gritar visualmente (rojo, grande, negrita). El resto es ruido de fondo (gris, pequeño).
4. **Navegación omnipresente:** Barra de pestañas inferior fija (4-5 items) con badges rojos de notificación. Botón flotante de "Servicio al Cliente" o "Chat" siempre visible.
5. **Adaptabilidad CJK:** La composición debe respetar la altura y el espaciado de los caracteres chinos/japoneses/coreanos, evitando que el texto se sienta apiñado verticalmente.

## IX. Resumen de Valores Técnicos

| Atributo | Especificación |
| --- | --- |
| Fondo dominante | Gris azulado claro `#F5F6F8` |
| Superficie UI | Blanco puro `#FFFFFF` |
| Acento primario | Rojo Comercio `#FF2442` (con gradientes a Naranja) |
| Acento secundario | Azul Fintech `#1677FF` |
| Texto principal | `#1A1A1A` |
| Border radius | 12px–16px (tarjetas), 20px+ (botones pill) |
| Sombra base | `rgba(0,0,0,0.04)`, blur 8px (muy sutil) |
| Tipografía | Sans-serif optimizada para CJK y números tabulares |
| Estado de foco | No aplica (entorno táctil). Tap state con Haptic. |
| Textura | Gradientes lineales, brillos sutiles (shimmer) |
| Easing | `Spring / Bounce`, 200ms–300ms |

## X. Contextos de Uso

**Ideal para:**

- Super-Apps (estilo WeChat, Alipay, Grab, Gojek).
- E-commerce de alto volumen y marketplaces (Taobao, JD, Shopee).
- Aplicaciones de delivery de comida y servicios locales (Meituan, Ele.me).
- Fintech, billeteras digitales y apps de inversión minorista.
- Plataformas de social-commerce y live-shopping.

**Posiciones cercanas en la rueda:** 150° (Neon Bazaar) · 180° (Cyber Market)
**Opuesto filosófico:** 45° (Functionalismo Racional / Minimalismo Suizo)
