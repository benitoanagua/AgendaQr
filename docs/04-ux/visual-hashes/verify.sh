#!/usr/bin/env bash
# Ronda 2 (Área D, ADR-0007): verificación de capturas de referencia SIN
# versionar binarios de imagen (decisión del 2026-10-09). Compara el
# SHA-256 de cada PNG regenerado contra manifest.json (texto versionado).
#
# SENSIBLE AL ENTORNO: el render de Robolectric depende de la plataforma
# (fuentes del JDK, versión de la VM). El manifest de referencia se generó
# en Linux x86_64 + JDK 17 + Robolectric 4.14.1. Úsalo en el MISMO entorno
# en que se generó; en otro, regenera el manifest (modo record) y revisa
# el diff visual localmente.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
OUT="$ROOT/feature/destinations/presentation/build/roborazzi"
MANIFEST="$ROOT/docs/04-ux/visual-hashes/manifest.json"

cd "$ROOT"
./gradlew :feature:destinations:presentation:recordRoborazzi \
  -PallowDebugSigningForRc=true --console=plain -q

python3 - "$OUT" "$MANIFEST" <<'PY'
import hashlib, json, os, sys
out, manifest = sys.argv[1], sys.argv[2]
expected = json.load(open(manifest))
actual = {}
for name in sorted(os.listdir(out)):
    if name.endswith(".png"):
        actual[name] = hashlib.sha256(open(os.path.join(out, name), "rb").read()).hexdigest()
missing = sorted(set(expected) - set(actual))
extra = sorted(set(actual) - set(expected))
changed = sorted(n for n in set(expected) & set(actual) if expected[n] != actual[n])
if missing or extra or changed:
    print("VISUAL_HASHES=FAIL")
    for n in missing: print(f"  - captura que falta: {n}")
    for n in extra: print(f"  - captura nueva: {n}")
    for n in changed: print(f"  - pixel changed: {n}")
    sys.exit(1)
print(f"VISUAL_HASHES=PASS ({len(actual)} capturas)")
PY
