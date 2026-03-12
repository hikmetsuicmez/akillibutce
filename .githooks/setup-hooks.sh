#!/usr/bin/env bash
# ============================================================
# setup-hooks.sh
# Git hook dizinini .githooks olarak ayarlar ve
# tum hook dosyalarina calistirma izni verir.
# Kullanim: bash .githooks/setup-hooks.sh
# ============================================================

set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
HOOKS_DIR="$REPO_ROOT/.githooks"

echo "==> Git hook dizini ayarlaniyor: $HOOKS_DIR"
git config core.hooksPath "$HOOKS_DIR"

echo "==> Hook dosyalarina calistirma izni veriliyor..."
find "$HOOKS_DIR" -type f ! -name "*.md" ! -name "*.sh" | while read -r hook; do
    chmod +x "$hook"
    echo "    [+] $(basename "$hook")"
done
chmod +x "$HOOKS_DIR"/*.sh 2>/dev/null || true

echo ""
echo "Kurulum tamamlandi."
echo "Aktif hook dizini: $(git config core.hooksPath)"
echo ""
echo "Aktif hook'lar:"
find "$HOOKS_DIR" -maxdepth 1 -type f ! -name "*.sh" ! -name "*.md" \
     -exec echo "    - {}" \;
