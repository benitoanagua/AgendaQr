#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
UI="$ROOT/core/ui/src"

fail() {
  echo "Xauxa gate failed: $1" >&2
  exit 1
}

# Inspect every production source set in core:ui. The component lab is a
# deliberately isolated specimen of platform primitives and is excluded.
UI_PRODUCTION_DIRS=()
while IFS= read -r source_dir; do
  [[ "$source_dir" == */lab || "$source_dir" == */lab/* ]] && continue
  UI_PRODUCTION_DIRS+=("$source_dir")
done < <(find "$UI" -type d \( -name commonMain -o -name androidMain -o -name iosMain -o -name wasmJsMain -o -name jvmMain -o -name macosMain -o -name linuxMain \) | sort)

if ((${#UI_PRODUCTION_DIRS[@]})); then
  # Literal visual primitives and raw dimensions belong in the token/theme
  # layer, not in individual production components.
  if grep -RInE 'Color\(0x|\.shadow\(|RoundedCornerShape\(' \
    "${UI_PRODUCTION_DIRS[@]}" --include='*.kt' \
    --exclude='XauxaTokens.kt' --exclude='XauxaTheme.kt' --exclude-dir=lab; then
    fail "raw color/shadow/rounded geometry found in core:ui production code."
  fi
  if grep -RInE '[0-9]+\.dp|[0-9]+\.sp' \
    "${UI_PRODUCTION_DIRS[@]}" --include='*.kt' \
    --exclude='XauxaTokens.kt' --exclude='XauxaTheme.kt' \
    --exclude-dir=lab; then
    fail "visual dimension literals must live in XauxaTokens.kt."
  fi
  if grep -RInE 'RectangleShape|RoundedCornerShape|CutCornerShape|CircleShape' \
    "${UI_PRODUCTION_DIRS[@]}" --include='*.kt' \
    --exclude='XauxaTokens.kt' --exclude='XauxaTheme.kt' --exclude-dir=lab; then
    fail "production components must use the canonical XauxaShape/token API."
  fi
fi

# Accessibility floor: the canonical minimum interactive target remains 48dp.
if ! grep -Eq 'val ControlMinSize = 48\.dp' \
  "$UI/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt"; then
  fail "XauxaMetrics.ControlMinSize must remain 48.dp."
fi

# Application and feature modules may not bypass core:ui. Scan all production
# Kotlin source sets (including conventional src/main/kotlin) while excluding
# test fixtures. Material 3 inside core:ui remains allowed as implementation
# detail; its wrappers are audited separately and cannot be globally banned.
APP_ROOTS=("$ROOT/feature" "$ROOT/shared/src" "$ROOT/androidApp/src")
for app_root in "${APP_ROOTS[@]}"; do
  [[ -d "$app_root" ]] || continue
  while IFS= read -r source_file; do
    if grep -nE 'MaterialTheme\.colorScheme|androidx\.compose\.material3' "$source_file"; then
      fail "application/feature code must use core:ui and Xauxa tokens ($source_file)."
    fi
  done < <(find "$app_root" -type f -name '*.kt' \
    ! -path '*/build/*' \
    ! -path '*/commonTest/*' \
    ! -path '*/androidUnitTest/*' \
    ! -path '*/androidInstrumentedTest/*' \
    ! -path '*/androidTest/*' \
    ! -path '*/iosTest/*' \
    ! -path '*/jvmTest/*' \
    ! -path '*/test/*' | sort)
done

echo "XAUXA_GATE=PASS"
