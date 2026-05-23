# Vinilos - Grupo 11

Aplicación Android para consultar catálogos de álbumes, artistas y coleccionistas de música.

## Requisitos

- Android Studio Hedgehog o superior
- JDK 11
- Android SDK con API 36 instalado
- Emulador Android o dispositivo físico con Android 5.0+ (API 21+)

## Configuración del backend

La app consume el API: `https://backvynils-q6yc.onrender.com`

## Construir y ejecutar

### Clonar el repositorio

```bash
git clone https://github.com/Alefig12/MISW4203-202612-Grupo11-Vinilos.git
cd MISW4203-202612-Grupo11-Vinilos
```

### Generar el APK de debug

```bash
./gradlew assembleDebug
```

El APK queda en:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Instalar directo en emulador o dispositivo conectado

```bash
./gradlew installDebug
```

### Instalar el APK manualmente en un dispositivo

1. Copia `app-debug.apk` al dispositivo (cable USB, Google Drive, etc.)
2. Abre el archivo desde el dispositivo
3. Acepta el permiso de **instalar apps de fuentes desconocidas** si se solicita
4. Instala y abre la app

## Ejecutar las pruebas instrumentadas

Con un emulador o dispositivo conectado:

```bash
./gradlew connectedAndroidTest
```

Los resultados quedan en:
```
app/build/reports/androidTests/connected/
```

### Suite de pruebas (Sprint 3)

| Clase de prueba | HU | Casos de prueba |
|---|---|---|
| `AlbumsScreenTest` | HU01 | Catálogo de álbumes: carga, error, caché |
| `AlbumsNavigationTest` | HU01 | Navegación y menú inferior |
| `AlbumDetailScreenTest` | HU02 | Detalle de álbum: nombre, género, tracks, error |
| `AlbumDetailNavigationTest` | HU02 | Navegar a detalle y volver a la lista |
| `ArtistsScreenTest` | HU03 | Catálogo de artistas: carga, error |
| `ArtistsNavigationTest` | HU03 | Navegación al tab de artistas |
| `ArtistDetailScreenTest` | HU04 | Detalle de artista: nombre, descripción, error |
| `ArtistDetailNavigationTest` | HU04 | Navegar a detalle y volver a la lista |
| `CollectorsScreenTest` | HU05 | Catálogo de coleccionistas: carga, error |
| `CollectorsNavigationTest` | HU05 | Navegación al tab de coleccionistas |
| `CollectorDetailScreenTest` | HU06 | Detalle de coleccionista: nombre, email, error |
| `CollectorDetailNavigationTest` | HU06 | Navegar a detalle y volver a la lista |
| `CreateAlbumScreenTest` | HU07 | Formulario visible, validación vacío, URL inválida, error de API |
| `CreateAlbumNavigationTest` | HU07 | FAB por rol, navegar al formulario, volver, flujo completo |
| `ripper/CreateAlbumRipperTest` | HU07 | Reconocimiento UIAutomator: inyección, dropdowns, DatePicker, navegación, flujo completo |
| `AddTrackScreenTest` | HU08 | Formulario visible, validación vacío, duración inválida, diálogo cancelar, error de API |
| `AddTrackNavigationTest` | HU08 | Botón asociar por rol, navegar con nombre precargado, back con/sin cambios, flujo completo |
| `ripper/AddTrackRipperTest` | HU08 | Reconocimiento UIAutomator: inyección nombre, inyección duración, diálogos repetidos, flujo completo |
| `ripper/AppRipperTest` | Toda la app | Reconocimiento UIAutomator: catálogos, detalles, crear álbum y asociar track por rol Coleccionista |

Todos los tests usan repositorios falsos (`FakeAlbumRepository`, `FakeArtistRepository`, `FakeCollectorRepository`) para no depender de la red.

## Pruebas de desempeño en dispositivo físico

La app registra automáticamente métricas de tiempo de respuesta para cada historia de usuario usando `PerformanceTracker` (tag Logcat: `VinilosPerf`).

### Capturar métricas

1. Instala el APK en el dispositivo y conecta por USB con depuración habilitada
2. Limpia el buffer y empieza la captura:
   ```bash
   adb logcat -c
   adb logcat -s VinilosPerf > metricas_dispositivo.txt
   ```
3. Usa la app: abre cada catálogo y toca al menos 3 elementos de detalle por sección
4. Detén la captura con `Ctrl + C`

### Formato de las métricas

```
METRIC | story=HU01-AlbumCatalog | device=samsung SM-S938B (API 36) | duration=6622ms | result=ok
METRIC | story=HU04-AlbumDetail(id=101) | device=... | duration=688ms | result=ok
```

Las historias medidas son: `HU01-AlbumCatalog`, `HU02-AlbumDetail`, `HU03-ArtistCatalog`, `HU04-ArtistDetail`, `HU05-CollectorCatalog`, `HU06-CollectorDetail`, `HU07-CreateAlbum`, `HU08-AddTrack`.

## Estructura del proyecto

```
app/src/main/java/com/example/vinilos_grupo11/
├── application/       # VinilosApplication (inicialización de Volley y repositorios)
├── database/dao/      # DAOs en memoria (caché de álbumes, artistas y coleccionistas)
├── models/            # Modelos de datos (Album, AlbumDetail, Artist, Collector, Track...)
├── network/           # Adaptadores de red (Volley)
├── performance/       # PerformanceTracker (métricas de desempeño por HU)
├── repositories/      # Repositorios e interfaces (IAlbumRepository, IArtistRepository, ICollectorRepository)
├── ui/
│   ├── home/          # HomeActivity (pantalla de selección de rol)
│   ├── albums/        # Catálogo y detalle de álbumes (HU01, HU04)
│   ├── artists/       # Catálogo y detalle de artistas (HU02, HU03)
│   └── collectors/    # Catálogo y detalle de coleccionistas (HU05, HU06)
└── viewmodels/        # ViewModels (MVVM): AlbumListViewModel, AlbumDetailViewModel, etc.

app/src/androidTest/java/com/example/vinilos_grupo11/
├── fake/              # Repositorios falsos para pruebas (sin red)
├── ripper/            # Pruebas de reconocimiento con UIAutomator (CreateAlbumRipperTest, AddTrackRipperTest, AppRipperTest)
├── DisableAnimationsRule.kt  # JUnit Rule para deshabilitar animaciones en tests
├── *ScreenTest.kt     # Pruebas E2E de contenido de pantalla
└── *NavigationTest.kt # Pruebas de navegación entre pantallas

ripper/
├── monkey_hu07.sh     # Script Monkey para HU07 (bash – macOS/Linux)
├── monkey_hu07.ps1    # Script Monkey para HU07 (PowerShell – Windows)
├── monkey_hu08.sh     # Script Monkey para HU08 (bash – macOS/Linux)
└── monkey_hu08.ps1    # Script Monkey para HU08 (PowerShell – Windows)
```

## Pruebas de reconocimiento con rippers

### UIAutomator (código)

Las pruebas en `ripper/` usan UIAutomator 2.x para explorar la UI de forma sistemática sin depender de la red (usan repositorios falsos):

```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.vinilos_grupo11.ripper.CreateAlbumRipperTest
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.vinilos_grupo11.ripper.AddTrackRipperTest
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.vinilos_grupo11.ripper.AppRipperTest
```

Los logs del ripper se emiten con tag `VinilosRipper`:

```bash
adb logcat -s VinilosRipper
```

### Android Monkey (script)

El Monkey inyecta eventos aleatorios sobre la app instalada. Requiere un dispositivo/emulador conectado con ADB:

```bash
# macOS / Linux
chmod +x ripper/monkey_hu07.sh ripper/monkey_hu08.sh
./ripper/monkey_hu07.sh [eventos] [semilla] [throttle_ms]
./ripper/monkey_hu07.sh 500 42 200
./ripper/monkey_hu08.sh 500 42 200

# Windows (PowerShell)
.\ripper\monkey_hu07.ps1 -Events 500 -Seed 42 -Throttle 200
.\ripper\monkey_hu08.ps1 -Events 500 -Seed 42 -Throttle 200
```

Los scripts configuran automáticamente el rol `Coleccionista` (necesario para que las acciones de HU07 y HU08 sean accesibles), ejecutan el Monkey y generan un reporte de crashes/ANRs en `monkey_hu07_<timestamp>.txt` o `monkey_hu08_<timestamp>.txt`. Nota: Monkey arranca desde `MainActivity`, así que alcanzar el formulario de Asociar Track (3 pantallas adentro) es probabilístico — el script valida estabilidad global de la app con el rol cargado.
