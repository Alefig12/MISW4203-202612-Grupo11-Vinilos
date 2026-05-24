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

# 1. Verificar adb (compatible con Windows PowerShell 5.1)
$adbCmd = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adbCmd) {
    Write-Error "'adb' no encontrado. Agrega Android SDK Platform-Tools al PATH."
    exit 1
}

# 2. Verificar dispositivo conectado
#    @(...) fuerza array para evitar el colapso a string cuando hay 1 solo dispositivo
#    (sin esto, $devices[0] devolveria el primer caracter en vez de la primera linea).
$devices = @(adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "device$" })
if ($devices.Count -eq 0) {
    Write-Error "No se detecto ningun dispositivo/emulador conectado."
    exit 1
}
$firstSerial = ($devices[0] -split "`t")[0]
Write-Host "[OK] Dispositivo: $firstSerial" -ForegroundColor Green

# 3. Configurar SharedPreferences (rol Coleccionista) para que el FAB de HU07 sea visible
Write-Host ""
Write-Host "[1/3] Configurando rol Coleccionista..." -ForegroundColor Yellow

$prefsXml = '<?xml version="1.0" encoding="utf-8" standalone="yes" ?><map><string name="user_role">Coleccionista</string></map>'
$tmpLocal = [System.IO.Path]::GetTempFileName()
[System.IO.File]::WriteAllText($tmpLocal, $prefsXml, [System.Text.Encoding]::UTF8)

try {
    adb push $tmpLocal "/sdcard/VinilosPrefs_tmp.xml" | Out-Null
    adb shell "run-as $PACKAGE cp /sdcard/VinilosPrefs_tmp.xml /data/data/$PACKAGE/shared_prefs/VinilosPrefs.xml" | Out-Null
    # Capturar exit code del cp ANTES de que el rm lo sobrescriba
    $cpExit = $LASTEXITCODE
    adb shell "rm /sdcard/VinilosPrefs_tmp.xml" | Out-Null
    if ($cpExit -eq 0) {
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
    "-s", $Seed,                          # Forma corta: el Monkey en API 21 no acepta "--seed"
    "--throttle", $Throttle,
    "--ignore-crashes",
    "--ignore-timeouts",
    "--ignore-security-exceptions",
    "--monitor-native-crashes",
    "-v", "-v",
    $Events
)

# Sin "2>&1": en PS 5.1 redirigir stderr de un .exe envuelve cada linea como
# ErrorRecord y, con $ErrorActionPreference=Stop, puede abortar el script.
& adb @monkeyArgs | Tee-Object -FilePath $OutputFile

# 6. Analisis de resultados
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Analisis de resultados"                                      -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# @(...) garantiza que las operaciones .Count / pipelines no rompan si el archivo esta vacio
$content    = @(Get-Content $OutputFile -ErrorAction SilentlyContinue)
# Patrones especificos de Monkey: evitan falsos positivos si el help llega a volcarse
# (el help contiene "--ignore-crashes" y "--monitor-native-crashes", que un patron laxo
# como "CRASH" matchearia incorrectamente). (?im) = multilinea + case-insensitive porque
# Monkey emite "monkey aborted" en minuscula cuando no encuentra actividades.
$crashPattern = '(?im)^//\s*CRASH:|^\*\*\s+(monkey aborted|no activities found)|^//\s*NOT RESPONDING:|^\*\*\s+ANR\b|System appears to have crashed'
$crashMatches = @($content | Select-String -Pattern $crashPattern)
$crashCount   = $crashMatches.Count

$eventsLine = $content | Select-String "Events injected:" | Select-Object -Last 1
$eventsDone = if ($eventsLine) {
    ($eventsLine.Line -replace ".*Events injected:\s*", "").Trim()
} else {
    "?"
}

Write-Host " Eventos completados : $eventsDone"
Write-Host " Crashes / ANRs      : $crashCount"
Write-Host ""

# Si Monkey no llego a inyectar nada, es un problema de setup (app no instalada,
# launcher mal declarado), no de estabilidad. Reportar distinto.
$noActivities = @($content | Select-String -Pattern "No activities found").Count -gt 0
if ($noActivities) {
    Write-Host " RESULTADO: SETUP ERROR - Monkey no encontro actividades para correr." -ForegroundColor Yellow
    Write-Host "   Verifica que la app este instalada: adb shell pm list packages | findstr vinilos" -ForegroundColor Yellow
    Write-Host "   Reinstala si hace falta:           .\gradlew installDebug" -ForegroundColor Yellow
    exit 2
}

if ($crashCount -eq 0) {
    Write-Host " RESULTADO: PASS - No se detectaron crashes ni ANRs" -ForegroundColor Green
    exit 0
} else {
    Write-Host " RESULTADO: FAIL - Se detectaron $crashCount incidente(s)" -ForegroundColor Red
    Write-Host ""
    Write-Host " Detalle:" -ForegroundColor Red
    $crashMatches | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    exit 1
}
