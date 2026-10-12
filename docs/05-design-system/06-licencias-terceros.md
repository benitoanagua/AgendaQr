# Licencias de terceros

Registro de los iconos y dependencias empaquetados o consumidos por
Agenda QR, con la licencia verificada y el aviso requerido. Regla
(heredada de ADR-0005 y vigente): solo licencias libres.

## Lucide (iconografía) — ISC

- Set: [Lucide](https://lucide.dev) <https://github.com/lucide-icons/lucide>
- Licencia: **ISC** (los glifos derivados del proyecto Feather además
  bajo **MIT**, Copyright (c) 2013-present Cole Bemis, según la lista del
  propio repositorio). Ambas exigen conservar el aviso de copyright y el
  permiso en copias: el archivo LICENSE del set se conserva junto a los
  recursos empaquetados en la pasada de implementación.
- Consumo vigente: `com.composables:icons-lucide:1.1.0`
  (wrapper no oficial) en `core:ui` commonMain, consumido SOLO vía
  `AmIcon` (AgendaMobile). Verificado: compila en androidMain e
  iosSimulatorArm64 con Kotlin 2.2.20 / Compose 1.8.2; publica las mismas
  coordenadas para los tres targets. La licencia del artefacto es la ISC
  del set (más MIT para los glifos derivados de Feather listados en su
  LICENSE): el aviso se conserva en este registro; **pendiente de
  implementación**: copiar el archivo LICENSE junto a los recursos
  empaquetados del distribuible (APK/IPA/web).

Texto exacto de la licencia ISC, transcrito del repositorio oficial de
Lucide (`LICENSE`, rama main, verificado el 2026-10-05):

```
ISC License

Copyright (c) 2026 Lucide Icons and Contributors

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
```

Aviso MIT adicional (solo para los glifos derivados de Feather, lista en
el LICENSE de Lucide: p. ej. `search`, `plus`, `check`, `x`, `star`,
`arrow-left`, `image`, `camera` están en esa lista):

```
The MIT License (MIT) (for the icons listed above)

Copyright (c) 2013-present Cole Bemis

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

**Pendiente de implementación:** confirmar el texto LICENSE del artefacto
concreto empaquetado y copiarlo al distribuible (APK/IPA/web).
