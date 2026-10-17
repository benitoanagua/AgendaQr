# Licencias de terceros

Registro de licencias de los recursos y dependencias consumidos por Agenda QR, con sus avisos y obligaciones de distribución. La revisión de licencia es parte del mantenimiento del sistema; no define el lenguaje visual.

## Lucide (iconografía) — ISC

- Set: [Lucide](https://lucide.dev) <https://github.com/lucide-icons/lucide>
- Licencia: **ISC** (los glifos derivados del proyecto Feather además
  bajo **MIT**, Copyright (c) 2013-present Cole Bemis, según la lista del
  propio repositorio). Ambas exigen conservar el aviso de copyright y el
  permiso en copias: el archivo LICENSE del set se conserva junto a los
  recursos empaquetados.
- Consumo vigente: `com.composables:icons-lucide:1.1.0`
  (envoltorio de Compose Multiplatform) en `core:ui` commonMain, consumido
  SOLO vía `AmIcon`. Verificado: compila en androidMain e
  iosSimulatorArm64 con Kotlin 2.2.20 / Compose 1.8.2; publica las mismas
  coordenadas para los tres targets. Licencias aplicables: el código del
  envoltorio es **MIT** (POM del artefacto; Copyright (c) 2025 Composable
  Horizons) y los glifos son **ISC** del set Lucide, más **MIT** para los
  glifos derivados de Feather. Todas exigen conservar el aviso.
- Estado del empaquetado: **Android implementado y comprobado**. La
  tarea Gradle `generateThirdPartyNotices` (plugin
  `agendaqr.android-application`) copia el aviso a los assets generados y
  AGP lo empaqueta; el APK de depuración incluye
  `assets/THIRD_PARTY_LICENSES.txt`. **iOS y web: pendiente** (el aviso no
  se copia aún a esos distribuibles).

Texto exacto de la licencia ISC, transcrito del repositorio oficial de
Lucide (`LICENSE`, rama main, verificado el 2026-10-16):

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
el LICENSE de Lucide: p. ej. `search`, `plus`, `check`, `x`, `arrow-left`,
`calendar`, `clock`, `info` están en esa lista):

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

Aviso MIT del envoltorio empaquetado (POM de
`com.composables:icons-lucide:1.1.0`; el artefacto no publica un archivo
LICENSE propio ni en el JAR de metadatos ni en el AAR, así que el aviso se
transcribe del POM y de la fuente del repositorio, verificado el
2026-10-16):

```
MIT License

Copyright (c) 2025 Composable Horizons

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

**Estado del aviso en el distribuible:** Android implementado y comprobado
(asset `assets/THIRD_PARTY_LICENSES.txt` presente en el APK). iOS y web
siguen pendientes: el aviso no se copia aún a esos distribuibles.
