# ================================================================
# Attendance Journal -- Windows Auto-Installer (PowerShell)
# Run: Right-click -> "Run with PowerShell"
# ================================================================

$ErrorActionPreference = "Stop"
$REPO = "sadfsadfs345345/attendance-journal"
$AppName = "Журнал посещаемости"
$InstallDir = "$env:LOCALAPPDATA\AttendanceJournal"
$JarPath = "$InstallDir\attendance.jar"
$ShortcutPath = "$env:USERPROFILE\Desktop\$AppName.lnk"

Write-Host ""
Write-Host "📋 $AppName -- установщик" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

# --- Check Java ---
try {
    $javaVersion = & java -version 2>&1 | Select-Object -First 1
    Write-Host "[OK] Java: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "[!] Java не найдена. Скачиваю Java 17..." -ForegroundColor Yellow
    $javaUrl = "https://aka.ms/download-jdk/microsoft-jdk-17-windows-x64.msi"
    $javaMsi = "$env:TEMP\java17.msi"
    Invoke-WebRequest -Uri $javaUrl -OutFile $javaMsi -UseBasicParsing
    Start-Process msiexec -ArgumentList "/i `"$javaMsi`" /quiet" -Wait
    Write-Host "[OK] Java установлена" -ForegroundColor Green
}

# --- Get latest release ---
Write-Host "" 
Write-Host "[...] Получаю информацию о последнем выпуске..." -ForegroundColor Cyan
$apiUrl = "https://api.github.com/repos/$REPO/releases/latest"
$release = Invoke-RestMethod -Uri $apiUrl -Headers @{"User-Agent"="attendance-installer"}
$jarAsset = $release.assets | Where-Object { $_.name -like "*.jar" } | Select-Object -First 1
$apkAsset = $release.assets | Where-Object { $_.name -like "*.apk" } | Select-Object -First 1
$version = $release.tag_name

Write-Host "[OK] Найдена версия: $version" -ForegroundColor Green

# --- Download JAR ---
if (-not $jarAsset) {
    Write-Host "[!] JAR-файл не найден в Releases." -ForegroundColor Red
    Write-Host "    Загрузите вручную: https://github.com/$REPO/releases/latest"
    pause; exit 1
}

Write-Host "[...] Скачиваю $($jarAsset.name) ($([math]::Round($jarAsset.size/1MB,1)) MB)..." -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path $InstallDir | Out-Null
Invoke-WebRequest -Uri $jarAsset.browser_download_url -OutFile $JarPath -UseBasicParsing
Write-Host "[OK] Скачан" -ForegroundColor Green

# --- Create Desktop Shortcut ---
Write-Host "[...] Создаю ярлык на рабочем столе..." -ForegroundColor Cyan
$WshShell = New-Object -ComObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut($ShortcutPath)
$Shortcut.TargetPath = "javaw"
$Shortcut.Arguments = "-jar `"$JarPath`""
$Shortcut.WorkingDirectory = $InstallDir
$Shortcut.Description = $AppName
$Shortcut.IconLocation = "$env:SystemRoot\System32\imageres.dll,109"
$Shortcut.Save()
Write-Host "[OK] Ярлык создан: $ShortcutPath" -ForegroundColor Green

# --- Also create Start Menu shortcut ---
$StartMenuPath = "$env:APPDATA\Microsoft\Windows\Start Menu\Programs\$AppName.lnk"
$Shortcut2 = $WshShell.CreateShortcut($StartMenuPath)
$Shortcut2.TargetPath = "javaw"
$Shortcut2.Arguments = "-jar `"$JarPath`""
$Shortcut2.WorkingDirectory = $InstallDir
$Shortcut2.Description = $AppName
$Shortcut2.IconLocation = "$env:SystemRoot\System32\imageres.dll,109"
$Shortcut2.Save()
Write-Host "[OK] Ярлык добавлен в Меню Пуск" -ForegroundColor Green

# --- APK info ---
if ($apkAsset) {
    Write-Host ""
    Write-Host "📱 APK для Android: $($apkAsset.browser_download_url)" -ForegroundColor Magenta
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " ✅ Установка завершена! Версия: $version" -ForegroundColor Green
Write-Host " 🖥  Ярлык появился на Рабочем столе" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Launch the app
$answer = Read-Host "Запустить приложение сейчас? (y/n)"
if ($answer -match "^[YyдД]$") {
    Start-Process javaw -ArgumentList "-jar `"$JarPath`""
}

pause
