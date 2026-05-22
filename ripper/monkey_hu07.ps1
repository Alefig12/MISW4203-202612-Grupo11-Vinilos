#Requires -Version 5.1
<#
.SYNOPSIS
    Pruebas de reconocimiento con Monkey para HU07 (Crear Album) – Vinilos App.
.DESCRIPTION
    Configura el rol Coleccionista, lanza la app y ejecuta el Android Monkey
    con los parametros dados, generando un reporte de crashes/ANRs.
.PARAMETER Events
    Numero de eventos aleatorios a inyectar (default: 500).
.PARAMETER Seed
    Semilla para reproducibilidad (default: 42).
.PARAMETER Throttle
    Espera en ms entre eventos (default: 200).
.EXAMPLE
    .\ripper\monkey_hu07.ps1
    .\ripper\monkey_hu07.ps1 -Events 1000 -Seed 7 -Throttle 150
#>
param(
    [int]$Events   = 500,
    [int]$Seed     = 42,
    [int]$Throttle = 200
)

$ErrorActionPreference = "Stop"

$PACKAGE    = "com.example.vinilos_grupo11"
$ACTIVITY   = ".ui.MainActivity"
$Timestamp  = Get-Date -Format "yyyyMMdd_HHmmss"
$OutputFile = "monkey_hu07_$Timestamp.txt"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Vinilos - Monkey Ripper para HU07 (Crear Album)"           -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Package  : $PACKAGE"
Write-Host " Eventos  : $Events"
Write-Host " Semilla  : $Seed"
Write-Host " Throttle : ${Throttle}ms"
Write-Host " Salida   : $OutputFile"
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar adb
$adbPath = (Get-Command adb -ErrorAction SilentlyContinue)?.Source
if (-not $adbPath) {
    Write-Error "'adb' no encontrado. Agrega Android SDK Platform-Tools al PATH."
    exit 1
}

# 2. Verificar dispositivo conectado
$devices = adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "device$" }
if ($devices.Count -eq 0) {
    Write-Error "No se detecto ningun dispositivo/emulador conectado."
    exit 1
}
Write-Host "[OK] Dispositivo: $($devices[0].Split("`t")[0])" -ForegroundColor Green

# 3. Configurar SharedPreferences (rol Coleccionista) para que el FAB de HU07 sea visible
Write-Host ""
Write-Host "[1/3] Configurando rol Coleccionista..." -ForegroundColor Yellow

$prefsXml = '<?xml version="1.0" encoding="utf-8" standalone="yes" ?><map><string name="user_role">Coleccionista</string></map>'
$tmpLocal = [System.IO.Path]::GetTempFileName()
[System.IO.File]::WriteAllText($tmpLocal, $prefsXml, [System.Text.Encoding]::UTF8)

try {
    adb push $tmpLocal "/sdcard/VinilosPrefs_tmp.xml" | Out-Null
    $result = adb shell "run-as $PACKAGE cp /sdcard/VinilosPrefs_tmp.xml /data/data/$PACKAGE/shared_prefs/VinilosPrefs.xml 2>&1"
    adb shell "rm /sdcard/VinilosPrefs_tmp.xml" | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] SharedPreferences configurado." -ForegroundColor Green
    } else {
        Write-Warning "No se pudo configurar SharedPreferences via run-as. Asegurate de seleccionar 'Coleccionista' manualmente antes de ejecutar."
    }
} finally {
    Remove-Item $tmpLocal -Force -ErrorAction SilentlyContinue
}

# 4. Lanzar la app
Write-Host ""
Write-Host "[2/3] Lanzando la app..." -ForegroundColor Yellow
adb shell "am start -n ${PACKAGE}/${ACTIVITY}" | Out-Null
Start-Sleep -Seconds 2

# 5. Ejecutar Monkey
Write-Host ""
Write-Host "[3/3] Ejecutando Monkey con $Events eventos (semilla=$Seed)..." -ForegroundColor Yellow
Write-Host "      Guardando log en: $OutputFile"
Write-Host ""

$monkeyArgs = @(
    "shell", "monkey",
    "-p", $PACKAGE,
    "--seed", $Seed,
    "--throttle", $Throttle,
    "--ignore-crashes",
    "--ignore-timeouts",
    "--ignore-security-exceptions",
    "--monitor-native-crashes",
    "-v", "-v",
    $Events
)

& adb @monkeyArgs 2>&1 | Tee-Object -FilePath $OutputFile

# 6. Analisis de resultados
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Analisis de resultados"                                      -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

$content       = Get-Content $OutputFile -ErrorAction SilentlyContinue
$crashCount    = ($content | Select-String "CRASH|System appears to have crashed|ANR|Monkey aborted").Count
$eventsDone    = ($content | Select-String "Events injected:" | Select-Object -Last 1) -replace ".*Events injected:\s*", ""

Write-Host " Eventos completados : $eventsDone"
Write-Host " Crashes / ANRs      : $crashCount"
Write-Host ""

if ($crashCount -eq 0) {
    Write-Host " RESULTADO: PASS - No se detectaron crashes ni ANRs" -ForegroundColor Green
    exit 0
} else {
    Write-Host " RESULTADO: FAIL - Se detectaron $crashCount incidente(s)" -ForegroundColor Red
    Write-Host ""
    Write-Host " Detalle:" -ForegroundColor Red
    $content | Select-String "CRASH|ANR|Monkey aborted" | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    exit 1
}
