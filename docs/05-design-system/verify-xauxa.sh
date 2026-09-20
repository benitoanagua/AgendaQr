#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
UI="$ROOT/core/ui/src/commonMain/kotlin"
if grep -RInE 'Color\(0x|\.shadow\(|RoundedCornerShape\(' "$UI" --exclude=XauxaTokens.kt; then
  echo "Xauxa gate failed: forbidden raw visual primitive found."
  exit 1
fi
if grep -RInE '[0-9]+\.dp|[0-9]+\.sp' "$UI" --exclude=XauxaTokens.kt; then
  echo "Xauxa gate failed: visual literals must live in XauxaTokens.kt."
  exit 1
fi
echo "XAUXA_GATE=PASS"
