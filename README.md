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

## Estructura del proyecto

```
app/src/main/java/com/example/vinilos_grupo11/
├── application/       # VinilosApplication (inicialización de Volley y repositorios)
├── database/dao/      # DAOs en memoria (caché)
├── models/            # Modelos de datos (Album, Collector)
├── network/           # Adaptadores de red (Volley)
├── repositories/      # Repositorios e interfaces
├── ui/
│   ├── home/          # HomeActivity (pantalla de selección de rol)
│   ├── albums/        # Catálogo de álbumes
│   ├── artists/       # Catálogo de artistas
│   └── collectors/    # Catálogo de coleccionistas
└── viewmodels/        # ViewModels (MVVM)
```
