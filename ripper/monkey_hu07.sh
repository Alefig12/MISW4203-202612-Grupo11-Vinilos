#!/usr/bin/env bash
# =============================================================================
# Pruebas de reconocimiento con Monkey – HU07 Crear Álbum
# Uso: ./ripper/monkey_hu07.sh [eventos] [semilla] [throttle_ms]
# Ejemplo reproducible: ./ripper/monkey_hu07.sh 500 42 200
# =============================================================================
set -euo pipefail

PACKAGE="com.example.vinilos_grupo11"
MAIN_ACTIVITY=".ui.MainActivity"
EVENTS=${1:-500}
SEED=${2:-42}
THROTTLE=${3:-200}
OUTPUT="monkey_hu07_$(date +%Y%m%d_%H%M%S).txt"

echo "============================================================"
echo " Vinilos – Monkey Ripper para HU07 (Crear Álbum)"
echo "============================================================"
echo " Package  : $PACKAGE"
echo " Eventos  : $EVENTS"
echo " Semilla  : $SEED"
echo " Throttle : ${THROTTLE}ms"
echo " Salida   : $OUTPUT"
echo "============================================================"
echo ""

# 1. Verificar que adb está disponible
if ! command -v adb &>/dev/null; then
    echo "[ERROR] 'adb' no encontrado. Instala Android SDK Platform-Tools." >&2
    exit 1
fi

# 2. Verificar que hay un dispositivo/emulador conectado
DEVICE_COUNT=$(adb devices | grep -c "device$" || true)
if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "[ERROR] No se detectó ningún dispositivo/emulador conectado." >&2
    exit 1
fi
echo "[OK] Dispositivo detectado: $(adb devices | grep 'device$' | head -1 | cut -f1)"

# 3. Configurar rol "Coleccionista" para que el FAB de HU07 sea visible
echo ""
echo "[1/3] Configurando rol Coleccionista en SharedPreferences..."
PREFS_DIR="/data/data/$PACKAGE/shared_prefs"
PREFS_FILE="VinilosPrefs.xml"
PREFS_CONTENT='<?xml version="1.0" encoding="utf-8" standalone="yes" ?><map><string name="user_role">Coleccionista</string></map>'

# Intentar escritura directa (requiere run-as o root)
if adb shell "run-as $PACKAGE sh -c 'mkdir -p $PREFS_DIR && echo \"$PREFS_CONTENT\" > $PREFS_DIR/$PREFS_FILE'" 2>/dev/null; then
    echo "[OK] SharedPreferences configurado via run-as."
else
    echo "[WARN] No se pudo configurar SharedPreferences via run-as."
    echo "       Ejecuta la app manualmente y selecciona rol Coleccionista antes de correr este script."
fi

# 4. Lanzar la app
echo ""
echo "[2/3] Lanzando $PACKAGE..."
adb shell "am start -n ${PACKAGE}/${MAIN_ACTIVITY}" >/dev/null
sleep 2

# 5. Ejecutar Monkey
echo ""
echo "[3/3] Ejecutando Monkey con $EVENTS eventos (semilla=$SEED)..."
echo "      Logs en: $OUTPUT"
echo ""

adb shell monkey \
    -p "$PACKAGE" \
    --seed "$SEED" \
    --throttle "$THROTTLE" \
    --ignore-crashes \
    --ignore-timeouts \
    --ignore-security-exceptions \
    --monitor-native-crashes \
    -v -v \
    "$EVENTS" 2>&1 | tee "$OUTPUT"

# 6. Análisis de resultados
echo ""
echo "============================================================"
echo " Análisis de resultados"
echo "============================================================"
CRASHES=$(grep -c "CRASH\|** System appears to have crashed\|ANR\|** Monkey aborted" "$OUTPUT" 2>/dev/null || echo "0")
EVENTS_DONE=$(grep "Events injected:" "$OUTPUT" | tail -1 | grep -o '[0-9]*' || echo "?")

echo " Eventos completados : $EVENTS_DONE"
echo " Crashes / ANRs      : $CRASHES"
echo ""

if [ "$CRASHES" -eq 0 ]; then
    echo " RESULTADO: PASS ✓ — No se detectaron crashes ni ANRs"
    exit 0
else
    echo " RESULTADO: FAIL ✗ — Se detectaron $CRASHES incidente(s)"
    echo ""
    echo " Detalle:"
    grep "CRASH\|ANR\|Monkey aborted" "$OUTPUT" | head -20
    exit 1
fi
