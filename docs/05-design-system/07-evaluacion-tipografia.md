# Evaluación de tipografía: MiSans Global

Informe de evaluación de la fuente candidata MiSans Global (Xiaomi) que ADR-0020 dejó
anotada "para comparar en local; no se empaquetan fuentes en esta decisión". **No se
empaqueta ninguna fuente en este informe** y no se tocan `AmTokens.kt`, `AmTypographyTest`
ni los hashes visuales.

Estado de la tipografía vigente: `AmTokens.FamilyUi = FontFamily.Default` (fuente de
sistema). El proyecto no empaqueta una familia propia.

## 1. Licencia (consultada el 2026-10-16)

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

## 2. Cobertura

No verificada sobre el archivo local: no se descargó la fuente (obtenerla para comparar
implica manejar un archivo cuya redistribución el acuerdo no permite). Según la
documentación de Xiaomi, MiSans Global cubre latín con diacríticos (`á é í ó ú ü ñ ¿ ¡`),
cirílico, griego y CJK. Las cifras tabulares y los símbolos monetarios (`$`; `Bs` es
composición latina) no se pudieron comprobar en el archivo.

Resultado: **NO VERIFICADO**.

## 3. Peso

No medido sobre el archivo (no se descargó). Referencia: los pesos estáticos de MiSans
cubren el rango CJK completo y, sin subconjuntar, pesan del orden de decenas de MB por peso;
empaquetar Regular + Medium (+ SemiBold) aumentaría el distribuible en decenas de MB.
Subconjuntar a latín, que lo reduciría a cientos de KB, está prohibido por el acuerdo.

Resultado: **NO VERIFICADO** (estimación cualitativa, sin medición).

## 4. Comparación visual

No ejecutada. Requeriría obtener el archivo de la fuente y generar capturas lado a lado
(fuente de sistema frente a candidata) en claro y oscuro de Inicio, detalle QR, Actividades y
un formulario, más las comprobaciones de desborde a 320 dp y 200 % de fuente. Mientras la
licencia no permita el empaquetado, su resultado no cambiaría la recomendación. Se puede
ejecutar en un parche no commiteado si se obtiene una licencia comercial.

## 5. Recomendación

- **No adoptar ni empaquetar** MiSans Global con la licencia vigente: el acuerdo no permite
  redistribuir el archivo de fuente ni adaptarlo, y es revocable.
- Si se quiere una tipografía propia, elegir una familia con licencia que permita
  empaquetado y subconjuntos (por ejemplo OFL), y evaluarla en su propio informe con
  cobertura, peso y capturas medidos.
- Mantener la fuente de sistema mientras no exista esa decisión.

## 6. Riesgos registrados

- Licencia revocable: una adopción podría quedar sin base legal si Xiaomi cambia los
  términos.
- Subconjunto prohibido: obliga a empaquetar el rango completo, con el coste de tamaño.
- Atribución obligatoria: exigiría un aviso de MiSans en el distribuible, análogo al de
  Lucide.
