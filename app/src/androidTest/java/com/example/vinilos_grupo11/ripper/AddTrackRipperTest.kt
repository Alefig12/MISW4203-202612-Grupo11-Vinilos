package com.example.vinilos_grupo11.ripper

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AddTrackViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-R-HU08-01 a TC-R-HU08-06: Pruebas de reconocimiento con ripper para HU08 (Asociar Track).
 *
 * Usa UIAutomator para explorar el formulario de forma sistemática e inyectar entradas
 * de borde, verificando que la app no crashea bajo condiciones extremas.
 * Los logs se emiten con tag "VinilosRipper" para su análisis posterior.
 */
@RunWith(AndroidJUnit4::class)
class AddTrackRipperTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private var scenario: ActivityScenario<MainActivity>? = null

    companion object {
        private const val PACKAGE = "com.example.vinilos_grupo11"
        private const val LAUNCH_TIMEOUT = 5_000L
        private const val UI_TIMEOUT = 3_000L
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

    private fun openAlbumDetail() {
        device.findObject(By.res(PACKAGE, "albumsRecyclerView"))
            ?.findObjects(By.clickable(true))?.firstOrNull()?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "btn_associate_track")), UI_TIMEOUT)
        Thread.sleep(400)
    }

    private fun navigateToAddTrack() {
        openAlbumDetail()
        device.findObject(By.res(PACKAGE, "btn_associate_track"))?.click()
        device.wait(Until.hasObject(By.res(PACKAGE, "et_track_name")), UI_TIMEOUT)
        Thread.sleep(400)
    }

    /**
     * Inyecta texto en un EditText sin tocarlo (evita abrir el teclado y la consiguiente
     * recomposición del TextInputLayout, que invalida UiObject2 referencias previas).
     * setText / clear van por AccessibilityService, así que no necesitan foco.
     * Tolera StaleObjectException porque algunos payloads agresivos (emojis, 250+ chars)
     * pueden disparar layouts intermedios; el test mide estabilidad global, no éxito por payload.
     */
    private fun injectText(resId: String, value: String) {
        val maxAttempts = 3
        repeat(maxAttempts) { attempt ->
            try {
                val field = device.findObject(By.res(PACKAGE, resId)) ?: return
                field.clear()
                field.text = value
                device.waitForIdle(500)
                return
            } catch (e: StaleObjectException) {
                if (attempt == maxAttempts - 1) {
                    Log.w(TAG, "RIPPER | field=$resId | stale after $maxAttempts attempts; skipping payload")
                    return
                }
                Thread.sleep(200)
            }
        }
    }

    /**
     * TC-R-HU08-01 (Reconocimiento): El ripper identifica y registra todos los
     * elementos interactivos del formulario.
     */
    @Test
    fun tcR_hu08_01_recognizesAllFormElements() {
        navigateToAddTrack()
        Log.i(TAG, "RIPPER | screen=AddTrack | action=reconnaissance | start")

        val fieldIds = listOf("et_album_name", "et_track_name", "et_track_duration", "btn_save_track")

        var found = 0
        fieldIds.forEach { id ->
            var el = device.findObject(By.res(PACKAGE, id))
            if (el == null) { scrollDown(); el = device.findObject(By.res(PACKAGE, id)) }
            val exists = el != null
            if (exists) found++
            Log.i(TAG, "RIPPER | element=$id | found=$exists | class=${el?.className}")
        }

        Log.i(TAG, "RIPPER | screen=AddTrack | found=$found/${fieldIds.size} | appAlive=${appIsInForeground()}")
        assertEquals("Todos los elementos del formulario deben estar presentes", fieldIds.size, found)
    }

    /**
     * TC-R-HU08-02 (Inyección): El ripper inyecta cadenas especiales, SQL y HTML
     * en el campo nombre para verificar robustez ante entradas inesperadas.
     */
    @Test
    fun tcR_hu08_02_injectsSpecialCharactersInTrackName() {
        navigateToAddTrack()
        // Payloads ordenados ASCENDENTE por longitud para neutralizar la race del
        // AccessibilityService: si el texto solo crece entre iteraciones, los setSelection
        // pendientes nunca apuntan a un offset mayor a la longitud actual.
        // Maximo a 100 chars (no 250) para no agotar buffers intermedios de emoji2.
        val payloads = listOf(
            " ",                                   // 1
            "-1",                                  // 2
            "null",                                // 4
            "   \n\t\r   ",                        // 9
            "undefined",                           // 9
            "🎵🎸🎹🎺🎻🎼",                       // 12 chars (24 code units por surrogate pairs)
            "99999999999999",                      // 14
            "'; DROP TABLE tracks; --",            // 25
            "<script>alert('xss')</script>",       // 29
            "A".repeat(100)                        // 100
        )
        payloads.forEach { payload ->
            Log.i(TAG, "RIPPER | field=et_track_name | inject=${payload.take(40)}")
            injectText("et_track_name", payload)
            // 400ms: tiempo suficiente para que la cola de Accessibility drene los
            // setSelection encolados por payloads largos antes del siguiente clear().
            Thread.sleep(400)
        }
        assertTrue("La app no debe crashear con inyecciones en el campo nombre", appIsInForeground())
    }

    /**
     * TC-R-HU08-03 (Validación duración): El ripper intenta duraciones malformadas
     * en el campo duración y verifica que la app no crashea al validar.
     */
    @Test
    fun tcR_hu08_03_injectsMalformedDurations() {
        navigateToAddTrack()
        val invalidDurations = listOf(
            "60:00",
            "5:99",
            "-1:00",
            "::",
            "3:",
            ":30",
            "abc",
            "🎵:🎵",
            "A".repeat(500)
        )
        // Nombre válido para aislar el error a la duración
        injectText("et_track_name", "Pista Ripper")
        invalidDurations.forEach { duration ->
            Log.i(TAG, "RIPPER | field=et_track_duration | inject=${duration.take(40)}")
            injectText("et_track_duration", duration)
            Thread.sleep(150)
        }
        // Dispara validación con la última (inválida)
        device.findObject(By.res(PACKAGE, "btn_save_track"))?.click()
        Thread.sleep(600)
        assertTrue("La app no debe crashear al validar duraciones inválidas", appIsInForeground())
    }

    /**
     * TC-R-HU08-04 (Estabilidad del diálogo): El ripper abre y cancela el diálogo de
     * confirmar guardar repetidamente para verificar que no hay leaks ni crashes.
     */
    @Test
    fun tcR_hu08_04_repeatedConfirmSaveDialogOpenClose() {
        navigateToAddTrack()
        device.findObject(By.res(PACKAGE, "et_track_name"))?.apply {
            click(); clear(); text = "Pista Estable"
        }
        device.findObject(By.res(PACKAGE, "et_track_duration"))?.apply {
            click(); clear(); text = "2:30"
        }
        repeat(5) { i ->
            Log.i(TAG, "RIPPER | action=open_confirm_dialog | iteration=$i")
            device.findObject(By.res(PACKAGE, "btn_save_track"))?.click()
            Thread.sleep(400)
            // Negative button (Cancelar) = android:id/button2
            val cancel = device.wait(Until.findObject(By.res("android", "button2")), UI_TIMEOUT)
            if (cancel != null) cancel.click() else device.pressBack()
            Thread.sleep(300)
        }
        assertTrue("La app no debe crashear con aperturas repetidas del diálogo de confirmar", appIsInForeground())
    }

    /**
     * TC-R-HU08-05 (Navegación): El ripper alterna entre el formulario con y sin
     * cambios para verificar la estabilidad del diálogo de confirmación de salida.
     */
    @Test
    fun tcR_hu08_05_repeatedNavigationWithAndWithoutChanges() {
        repeat(4) { i ->
            Log.i(TAG, "RIPPER | action=enter_form | iteration=$i")
            navigateToAddTrack()
            if (i % 2 == 1) {
                // Con cambios: debe aparecer diálogo de confirmación
                device.findObject(By.res(PACKAGE, "et_track_name"))?.apply { click(); text = "Test" }
                Thread.sleep(100)
                device.pressBack()
                Thread.sleep(500)
                val btnSalir = device.wait(Until.findObject(By.text("Salir")), 2_000L)
                btnSalir?.click()
            } else {
                // Sin cambios: vuelve directamente sin diálogo
                device.pressBack()
            }
            device.wait(Until.hasObject(By.res(PACKAGE, "btn_associate_track")), UI_TIMEOUT)
            device.pressBack()  // volver de detalle a lista para la siguiente iteración
            device.wait(Until.hasObject(By.res(PACKAGE, "albumsRecyclerView")), UI_TIMEOUT)
            Thread.sleep(300)
        }
        assertTrue("La app no debe crashear con navegación repetida hacia/desde el formulario", appIsInForeground())
    }

    /**
     * TC-R-HU08-06 (Flujo completo): El ripper envía el formulario con datos válidos
     * y verifica que la app permanece estable tras el guardado.
     */
    @Test
    fun tcR_hu08_06_submitValidFormVerifiesStability() {
        navigateToAddTrack()
        Log.i(TAG, "RIPPER | action=fill_and_submit_valid_form | start")

        device.findObject(By.res(PACKAGE, "et_track_name"))?.apply {
            click(); clear(); text = "Pista Ripper Submission"
        }
        device.findObject(By.res(PACKAGE, "et_track_duration"))?.apply {
            click(); clear(); text = "3:45"
        }
        device.findObject(By.res(PACKAGE, "btn_save_track"))?.click()
        // Positive button (GUARDAR) = android:id/button1
        device.wait(Until.findObject(By.res("android", "button1")), UI_TIMEOUT)?.click()

        Thread.sleep(2_000)
        Log.i(TAG, "RIPPER | action=submit_complete | appAlive=${appIsInForeground()} | pkg=${device.currentPackageName}")
        assertTrue("La app debe permanecer estable tras enviar el formulario", appIsInForeground())
    }
}
