#!/usr/bin/env bash
# =============================================================
# Attendance Journal — Linux / macOS installer
# Usage: bash <(curl -fsSL https://sadfsadfs345345.github.io/attendance-journal/install.sh)
# =============================================================
set -e

REPO="sadfsadfs345345/attendance-journal"
APP_NAME="AttendanceJournal"
INSTALL_DIR="$HOME/.local/share/attendance-journal"
DESKTOP_FILE="$HOME/.local/share/applications/attendance-journal.desktop"
BIN_FILE="$HOME/.local/bin/attendance-journal"

echo ""
echo "📋 Журнал посещаемости — установщик"
echo "=================================================="

# Check Java
if ! command -v java &>/dev/null; then
  echo "⚠ Java не найдена. Установите Java 17+:"
  echo "  Ubuntu/Debian: sudo apt install openjdk-17-jdk"
  echo "  macOS:         brew install openjdk@17"
  exit 1
fi

echo "✔ Java: $(java -version 2>&1 | head -1)"

# Get latest release info
echo "⤵ Получаю информацию о последнем выпуске..."
LATEST_URL=$(curl -fsSL "https://api.github.com/repos/$REPO/releases/latest" \
  | grep '"browser_download_url"' \
  | grep '\.jar' \
  | head -1 \
  | sed -E 's/.*"(https[^"]+)".*/\1/')

if [ -z "$LATEST_URL" ]; then
  echo "⚠ JAR-файл не найден в релизах. Загрузите вручную: https://github.com/$REPO/releases/latest"
  exit 1
fi

mkdir -p "$INSTALL_DIR"
mkdir -p "$HOME/.local/bin"
mkdir -p "$HOME/.local/share/applications"

echo "⤵ Скачиваю: $LATEST_URL"
curl -fsSL -o "$INSTALL_DIR/attendance.jar" "$LATEST_URL"

# Create launcher script
cat > "$BIN_FILE" << EOF
#!/bin/sh
exec java -jar "$INSTALL_DIR/attendance.jar" "\$@"
EOF
chmod +x "$BIN_FILE"

# Create .desktop file (Linux app menu shortcut)
cat > "$DESKTOP_FILE" << EOF
[Desktop Entry]
Type=Application
Name=Журнал посещаемости
Exec=java -jar $INSTALL_DIR/attendance.jar
Icon=accessories-text-editor
Terminal=false
Categories=Education;
Comment=Отметка посещаемости для учебных групп
EOF

if command -v update-desktop-database &>/dev/null; then
  update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true
fi

echo ""
echo "✅ Установка завершена!"
echo "   Запуск: attendance-journal"
echo "   Или через меню приложений — Ярлык создан в ~/.local/share/applications/"
echo ""
