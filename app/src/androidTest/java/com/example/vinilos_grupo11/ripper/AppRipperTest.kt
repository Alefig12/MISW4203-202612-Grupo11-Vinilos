package com.example.vinilos_grupo11.ripper

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.fake.FakeArtistRepository
import com.example.vinilos_grupo11.fake.FakeCollectorRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AddTrackViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistDetailViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorDetailViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorsViewModel
import com.example.vinilos_grupo11.viewmodels.CreateAlbumViewModel
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-R-APP-01 a TC-R-APP-04: Pruebas de reconocimiento con ripper para toda la app.
 *
 * Explora sistemáticamente las tres secciones principales (álbumes, artistas,
 * coleccionistas) y el flujo completo de creación de álbum, verificando que la
 * app no crashea al navegar entre pantallas y acceder a detalles.
 */
@RunWith(AndroidJUnit4::class)
class AppRipperTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private var scenario: ActivityScenario<MainActivity>? = null

    companion object {
        private const val PACKAGE = "com.example.vinilos_grupo11"
        private const val LAUNCH_TIMEOUT = 5_000L
        private const val UI_TIMEOUT = 3_000L
        private const val LIST_TIMEOUT = 4_000L
        private const val TAG = "VinilosRipper"
    }

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Coleccionista").commit()
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AlbumDetailViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        ArtistViewModel.testRepositoryFactory = { _ -> FakeArtistRepository() }
        ArtistDetailViewModel.testRepositoryFactory = { _ -> FakeArtistRepository() }
        CollectorsViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository() }
        CollectorDetailViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository() }
        CreateAlbumViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AddTrackViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        // Lanza MainActivity directamente (igual que los tests Espresso), evitando HomeActivity
        scenario = ActivityScenario.launch(MainActivity::class.java)
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LAUNCH_TIMEOUT)
        Thread.sleep(500)
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        AlbumDetailViewModel.testRepositoryFactory = null
        ArtistViewModel.testRepositoryFactory = null
        ArtistDetailViewModel.testRepositoryFactory = null
        CollectorsViewModel.testRepositoryFactory = null
        CollectorDetailViewModel.testRepositoryFactory = null
        CreateAlbumViewModel.testRepositoryFactory = null
        AddTrackViewModel.testRepositoryFactory = null
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Visitante").commit()
        scenario?.close()
    }

    private fun appIsInForeground() = device.currentPackageName == PACKAGE

    private fun scrollDown() {
        device.swipe(540, 1_400, 540, 500, 20)
        Thread.sleep(300)
    }

    /**
     * TC-R-APP-01: El ripper explora el catálogo de álbumes, navega a detalle
     * de cada álbum disponible y regresa a la lista.
     */
    @Test
    fun tcR_app_01_exploresAlbumCatalog() {
        Log.i(TAG, "RIPPER | screen=AlbumList | action=explore_catalog")
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LIST_TIMEOUT)
        Thread.sleep(500)

        // Cuenta inicial de items (referencia válida solo antes de navegar)
        val initialCount = device.findObject(By.res(PACKAGE, "albumsRecyclerView"))
            ?.findObjects(By.clickable(true))?.size ?: 0
        Log.i(TAG, "RIPPER | screen=AlbumList | found=$initialCount clickable items")

        // Re-busca el primer item en CADA iteración para evitar StaleObjectException:
        // después de pressBack() el RecyclerView se reconstruye e invalida referencias previas.
        repeat(minOf(initialCount, 3)) { i ->
            val item = device.findObject(By.res(PACKAGE, "albumsRecyclerView"))
                ?.findObjects(By.clickable(true))?.firstOrNull() ?: return@repeat
            Log.i(TAG, "RIPPER | action=tap_album | index=$i")
            item.click()
            device.wait(Until.hasObject(By.res(PACKAGE, "tv_album_name")), UI_TIMEOUT)
            Thread.sleep(500)
            device.pressBack()
            device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), UI_TIMEOUT)
            Thread.sleep(300)
        }

        Log.i(TAG, "RIPPER | screen=AlbumList | complete | appAlive=${appIsInForeground()}")
        assertTrue("La app no debe crashear al explorar el catálogo de álbumes", appIsInForeground())
    }

    /**
     * TC-R-APP-02: El ripper navega al tab de artistas, explora la lista
     * y accede al detalle de cada artista disponible.
     */
    @Test
    fun tcR_app_02_exploresArtistCatalog() {
        Log.i(TAG, "RIPPER | action=navigate_to_artists_tab")
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LIST_TIMEOUT)

        device.findObject(By.res(PACKAGE, "artistListFragment"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "rv_artists")), LIST_TIMEOUT)
        Thread.sleep(500)
        Log.i(TAG, "RIPPER | screen=ArtistList | action=explore_catalog")

        val initialCount = device.findObject(By.res(PACKAGE, "rv_artists"))
            ?.findObjects(By.clickable(true))?.size ?: 0
        Log.i(TAG, "RIPPER | screen=ArtistList | found=$initialCount clickable items")

        repeat(minOf(initialCount, 3)) { i ->
            val item = device.findObject(By.res(PACKAGE, "rv_artists"))
                ?.findObjects(By.clickable(true))?.firstOrNull() ?: return@repeat
            Log.i(TAG, "RIPPER | action=tap_artist | index=$i")
            item.click()
            device.wait(Until.hasObject(By.res(PACKAGE, "tvArtistName")), UI_TIMEOUT)
            Thread.sleep(500)
            device.pressBack()
            device.wait(Until.hasObject(By.res(PACKAGE, "rv_artists")), UI_TIMEOUT)
            Thread.sleep(300)
        }

        Log.i(TAG, "RIPPER | screen=ArtistList | complete | appAlive=${appIsInForeground()}")
        assertTrue("La app no debe crashear al explorar el catálogo de artistas", appIsInForeground())
    }

    /**
     * TC-R-APP-03: El ripper navega al tab de coleccionistas, explora la lista
     * y accede al detalle de cada coleccionista disponible.
     */
    @Test
    fun tcR_app_03_exploresCollectorCatalog() {
        Log.i(TAG, "RIPPER | action=navigate_to_collectors_tab")
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LIST_TIMEOUT)

        device.findObject(By.res(PACKAGE, "collectorListFragment"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "rv_collectors")), LIST_TIMEOUT)
        Thread.sleep(500)
        Log.i(TAG, "RIPPER | screen=CollectorList | action=explore_catalog")

        val initialCount = device.findObject(By.res(PACKAGE, "rv_collectors"))
            ?.findObjects(By.clickable(true))?.size ?: 0
        Log.i(TAG, "RIPPER | screen=CollectorList | found=$initialCount clickable items")

        repeat(minOf(initialCount, 3)) { i ->
            val item = device.findObject(By.res(PACKAGE, "rv_collectors"))
                ?.findObjects(By.clickable(true))?.firstOrNull() ?: return@repeat
            Log.i(TAG, "RIPPER | action=tap_collector | index=$i")
            item.click()
            device.wait(Until.hasObject(By.res(PACKAGE, "tv_collector_name")), UI_TIMEOUT)
            Thread.sleep(500)
            device.pressBack()
            device.wait(Until.hasObject(By.res(PACKAGE, "rv_collectors")), UI_TIMEOUT)
            Thread.sleep(300)
        }

        Log.i(TAG, "RIPPER | screen=CollectorList | complete | appAlive=${appIsInForeground()}")
        assertTrue("La app no debe crashear al explorar el catálogo de coleccionistas", appIsInForeground())
    }

    /**
     * TC-R-APP-04: El ripper ejecuta el flujo completo de la app como Coleccionista:
     * visita cada sección, accede a un detalle por sección y crea un álbum nuevo.
     */
    @Test
    fun tcR_app_04_fullAppFlowAsCollector() {
        Log.i(TAG, "RIPPER | flow=full_app | role=Coleccionista | start")
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LIST_TIMEOUT)
        Thread.sleep(500)

        // 1. Detalle de un álbum
        device.findObject(By.res(PACKAGE, "albumsRecyclerView"))
            ?.findObjects(By.clickable(true))?.firstOrNull()?.let { item ->
                Log.i(TAG, "RIPPER | flow=step1 | action=open_album_detail")
                item.click()
                device.wait(Until.hasObject(By.res(PACKAGE, "tv_album_name")), UI_TIMEOUT)
                Thread.sleep(400)
                device.pressBack()
                device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), UI_TIMEOUT)
            }

        // 2. Detalle de un artista
        device.findObject(By.res(PACKAGE, "artistListFragment"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "rv_artists")), LIST_TIMEOUT)
        device.findObject(By.res(PACKAGE, "rv_artists"))
            ?.findObjects(By.clickable(true))?.firstOrNull()?.let { item ->
                Log.i(TAG, "RIPPER | flow=step2 | action=open_artist_detail")
                item.click()
                device.wait(Until.hasObject(By.res(PACKAGE, "tvArtistName")), UI_TIMEOUT)
                Thread.sleep(400)
                device.pressBack()
                device.wait(Until.hasObject(By.res(PACKAGE, "rv_artists")), UI_TIMEOUT)
            }

        // 3. Detalle de un coleccionista
        device.findObject(By.res(PACKAGE, "collectorListFragment"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "rv_collectors")), LIST_TIMEOUT)
        device.findObject(By.res(PACKAGE, "rv_collectors"))
            ?.findObjects(By.clickable(true))?.firstOrNull()?.let { item ->
                Log.i(TAG, "RIPPER | flow=step3 | action=open_collector_detail")
                item.click()
                device.wait(Until.hasObject(By.res(PACKAGE, "tv_collector_name")), UI_TIMEOUT)
                Thread.sleep(400)
                device.pressBack()
                device.wait(Until.hasObject(By.res(PACKAGE, "rv_collectors")), UI_TIMEOUT)
            }

        // 4. Crear un álbum nuevo
        device.findObject(By.res(PACKAGE, "albumListFragment"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "fab_create_album")), LIST_TIMEOUT)
        Log.i(TAG, "RIPPER | flow=step4 | action=create_album")
        device.findObject(By.res(PACKAGE, "fab_create_album"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "et_name")), UI_TIMEOUT)

        device.findObject(By.res(PACKAGE, "et_name"))?.apply { click(); text = "Album Ripper Full Flow" }
        device.findObject(By.res(PACKAGE, "et_cover"))?.apply {
            click(); text = "https://example.com/full-flow.jpg"
        }
        device.findObject(By.res(PACKAGE, "et_date"))?.click()
        device.wait(Until.findObject(By.res("android", "button1")), UI_TIMEOUT)?.click()

        device.swipe(540, 1_400, 540, 500, 20)
        Thread.sleep(400)

        device.findObject(By.res(PACKAGE, "et_description"))?.apply {
            click(); text = "Full flow ripper test"
        }
        device.findObject(By.res(PACKAGE, "acv_genre"))?.click()
        device.wait(Until.findObject(By.text("Rock")), UI_TIMEOUT)?.click()
        device.findObject(By.res(PACKAGE, "acv_record_label"))?.click()
        device.wait(Until.findObject(By.text("EMI")), UI_TIMEOUT)?.click()
        device.findObject(By.res(PACKAGE, "btn_save"))?.click()

        Thread.sleep(2_000)
        Log.i(TAG, "RIPPER | flow=full_app | complete | appAlive=${appIsInForeground()}")
        assertTrue("La app debe permanecer estable tras el flujo completo", appIsInForeground())
    }

    /**
     * TC-R-APP-05: El ripper ejecuta el flujo de asociar canción como Coleccionista:
     * abre el detalle de un álbum, toca "Asociar", llena el formulario y confirma.
     */
    @Test
    fun tcR_app_05_collectorAddsTrack() {
        Log.i(TAG, "RIPPER | flow=add_track | role=Coleccionista | start")
        device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), LIST_TIMEOUT)
        Thread.sleep(500)

        // 1. Abrir detalle del primer álbum
        device.findObject(By.res(PACKAGE, "albumsRecyclerView"))
            ?.findObjects(By.clickable(true))?.firstOrNull()?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "btn_associate_track")), UI_TIMEOUT)
        Thread.sleep(400)

        // 2. Tocar el botón de asociar
        Log.i(TAG, "RIPPER | flow=step2 | action=tap_associate")
        device.findObject(By.res(PACKAGE, "btn_associate_track"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "et_track_name")), UI_TIMEOUT)
        Thread.sleep(400)

        // 3. Llenar el formulario
        Log.i(TAG, "RIPPER | flow=step3 | action=fill_form")
        device.findObject(By.res(PACKAGE, "et_track_name"))?.apply {
            click(); clear(); text = "Track App Ripper"
        }
        device.findObject(By.res(PACKAGE, "et_track_duration"))?.apply {
            click(); clear(); text = "4:20"
        }

        // 4. Guardar y confirmar
        device.findObject(By.res(PACKAGE, "btn_save_track"))?.click()
        device.wait(Until.findObject(By.res("android", "button1")), UI_TIMEOUT)?.click()

        Thread.sleep(2_000)
        Log.i(TAG, "RIPPER | flow=add_track | complete | appAlive=${appIsInForeground()}")
        assertTrue("La app debe permanecer estable tras asociar una canción", appIsInForeground())
    }
}
