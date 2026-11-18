# Evaluación de tipografía

Estado vigente: Inter 4.0 se empaqueta como recurso multiplataforma en
`core/ui/src/commonMain/composeResources/font/` y `AqTheme` inyecta
`AqType.FamilyUi` en todos los roles de `AqMaterialTypography`. El proyecto
ya no depende de la fuente de sistema. Los tamaños y pesos siguen viniendo
de `@shopify/polaris-tokens@9.4.2`; la escala con la fuente del sistema se
respeta hasta 200 %. El texto íntegro de la licencia está en
`docs/05-design-system/06-licencias-terceros.md`.

El resto del documento conserva el informe histórico de MiSans Global
(Xiaomi), la candidata que ADR-0020 dejó anotada «para comparar en local».
Su conclusión sigue siendo no adoptarla: la licencia no permite
redistribuir el archivo de fuente.

## Decisión vigente: Inter 4.0 (SIL OFL 1.1)

- Fuente verificable: release 4.0 de
  <https://github.com/rsms/inter/releases/tag/v4.0>, paquete
  `Inter-4.0.zip`.
- Licencia: SIL Open Font License 1.1. Permite empaquetado, subconjuntos y
  redistribución del archivo de fuente siempre que se conserve el aviso y
  no se use el nombre reservado en derivados. Aviso incluido en Android e
  iOS no verificado; detalle en `docs/05-design-system/06-licencias-terceros.md`.
- Formato: cuatro TTF estáticos (Regular/Medium/SemiBold/Bold), sin
  ejes variables, para evitar dependencias de `FontVariation` y mantener
  un nombre de peso verificable por archivo.
- Pesos: los cuatro de la escala Polaris publicada (450/550/650/700),
  sin interpolaciones propias.
- Cobertura y peso medidos: los TTF se distribuyen con el releasing
  oficial; el tamaño exacto del distribuible no se ha medido en este
  entorno. Resultado del tamaño: **NO VERIFICADO**.
- Comparación visual: sustituye a la fuente de sistema en los roles
  Polaris; las previews claro/oscuro/200 % de `AqPreviews.kt` permiten
  revisarlos. No se han generado capturas de pantalla verificadas.
- Conclusión: adoptar Inter 4.0 con OFL 1.1; la adopción queda respaldada
  por `:core:ui:testDebugUnitTest` y el gate de arquitectura.

## Informe histórico: MiSans Global

### 1. Licencia (consultada el 2026-10-16)

- Fuente: acuerdo 《MiSans 字体知识产权许可协议》 de Xiaomi, publicado desde la página de
  fuentes de HyperOS <https://hyperos.mi.com/font/>.
- La página oficial devolvió HTTP 504 al consultarla en esa fecha. El texto del acuerdo se
  leyó de su reproducción íntegra citada en <https://github.com/dsrkafuu/misans> (README),
  que enlaza el PDF original de Xiaomi.
- Términos relevantes del acuerdo:
  - Concesión: licencia de copyright mundial, no exclusiva, no transferible, libre de
    regalías y **revocable**.
  - Obligación: indicar de forma específica en el software que se usa MiSans (atribución).
  - Prohibido: adaptar o crear obras derivadas de la fuente o de cualquiera de sus
    componentes.
  - Prohibido: alquilar, sublicenciar, ceder, prestar o **redistribuir** el software de
    fuente o copias. La restricción **no** aplica a las obras creadas con MiSans, que sí
    pueden distribuirse y venderse.
- Lectura: empaquetar el archivo de fuente dentro de un APK/IPA es redistribuir una copia del
  software de fuente, algo que el acuerdo prohíbe expresamente; además, subconjuntar
  (*subsetting*) es una adaptación, también prohibida. La licencia es ambigua para el
  empaquetado en app y, encima, revocable.
- Conclusión de licencia: **no empaquetar MiSans Global** sin un acuerdo comercial
  específico con el titular.

### 2. Cobertura

No verificada sobre el archivo local: no se descargó la fuente (obtenerla para comparar
implica manejar un archivo cuya redistribución el acuerdo no permite). Según la
documentación de Xiaomi, MiSans Global cubre latín con diacríticos (`á é í ó ú ü ñ ¿ ¡`),
cirílico, griego y CJK. Las cifras tabulares y los símbolos monetarios (`$`; `Bs` es
composición latina) no se pudieron comprobar en el archivo.

Resultado: **NO VERIFICADO**.

### 3. Peso

No medido sobre el archivo (no se descargó). Referencia: los pesos estáticos de MiSans
cubren el rango CJK completo y, sin subconjuntar, pesan del orden de decenas de MB por peso;
empaquetar Regular + Medium (+ SemiBold) aumentaría el distribuible en decenas de MB.
Subconjuntar a latín, que lo reduciría a cientos de KB, está prohibido por el acuerdo.

Resultado: **NO VERIFICADO** (estimación cualitativa, sin medición).

### 4. Comparación visual

No ejecutada. Requeriría obtener el archivo de la fuente y generar capturas lado a lado
(fuente de sistema frente a candidata) en claro y oscuro de Inicio, detalle QR, Actividades y
un formulario, más las comprobaciones de desborde a 360 dp y 150 % de fuente (el 200 %
se conserva en producción, pero quedó fuera de la verificación automática, ADR-0007). Mientras la
licencia no permita el empaquetado, su resultado no cambiaría la recomendación. Se puede
ejecutar en un parche no commiteado si se obtiene una licencia comercial.

### 5. Recomendación

- **No adoptar ni empaquetar** MiSans Global con la licencia vigente: el acuerdo no permite
  redistribuir el archivo de fuente ni adaptarlo, y es revocable.
- Se eligió Inter 4.0 (OFL 1.1) como familia con licencia que permite empaquetado
  y subconjuntos, evaluada en la sección de decisión vigente.
- Mantener Inter 4.0 como familia por defecto; la fuente de sistema sigue
  como fallback dentro de `FontFamily`.

### 6. Riesgos registrados

- Licencia revocable: una adopción podría quedar sin base legal si Xiaomi cambia los
  términos.
- Subconjunto prohibido: obliga a empaquetar el rango completo, con el coste de tamaño.
- Atribución obligatoria: exigiría un aviso de MiSans en el distribuible, análogo al de
  Lucide.
