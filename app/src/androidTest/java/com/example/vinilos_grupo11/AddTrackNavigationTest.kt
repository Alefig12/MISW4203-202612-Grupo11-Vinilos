package com.example.vinilos_grupo11

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AddTrackViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU08-06 a TC-HU08-10: Pruebas de navegación para la HU08 (Asociar Track).
 */
@RunWith(AndroidJUnit4::class)
class AddTrackNavigationTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    private fun setUserRole(role: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", role).commit()
    }

    @Before
    fun setUp() {
        setUserRole("Coleccionista")
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AlbumDetailViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AddTrackViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        AlbumDetailViewModel.testRepositoryFactory = null
        AddTrackViewModel.testRepositoryFactory = null
        setUserRole("Visitante")
    }

    private fun openAlbumDetail() {
        onView(withId(R.id.albumsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    /**
     * TC-HU08-06 (Positivo – Rol): El botón "Asociar" es visible en el detalle para el rol Coleccionista.
     */
    @Test
    fun tc_hu08_06_associateButtonVisibleForCollector() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU08-06b (Negativo – Rol): El botón "Asociar" NO es visible para el rol Visitante.
     */
    @Test
    fun tc_hu08_06b_associateButtonHiddenForVisitor() {
        setUserRole("Visitante")
        ActivityScenario.launch(MainActivity::class.java).use {
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).check(matches(not(isDisplayed())))
        }
    }

    /**
     * TC-HU08-07 (Positivo – Navegación): Al tocar "Asociar" se navega al formulario con el nombre del álbum precargado.
     */
    @Test
    fun tc_hu08_07_navigatesToAddTrackWithPreloadedAlbumName() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).perform(click())
            onView(withId(R.id.til_track_name)).check(matches(isDisplayed()))
            // FakeAlbumRepository.fakeAlbumDetail.name = "A Night at the Opera"
            onView(withId(R.id.et_album_name)).check(matches(withText("A Night at the Opera")))
        }
    }

    /**
     * TC-HU08-08 (Positivo – Navegación): Back sin cambios regresa al detalle sin diálogo.
     */
    @Test
    fun tc_hu08_08_backWithoutChangesPopsImmediately() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).perform(click())
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.popBackStack()
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            onView(withId(R.id.detail_content)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU08-09 (Positivo – Navegación): Back con cambios muestra diálogo; "Salir" regresa al detalle.
     */
    @Test
    fun tc_hu08_09_backWithChangesShowsExitDialogAndExitConfirms() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).perform(click())
            onView(withId(R.id.et_track_name)).perform(replaceText("Cambios sin guardar"), closeSoftKeyboard())
            androidx.test.espresso.Espresso.pressBack()
            // Diálogo de confirmar salida visible
            onView(withText(R.string.title_confirm_exit)).check(matches(isDisplayed()))
            // "Salir" regresa al detalle
            onView(withId(android.R.id.button1)).perform(click())
            onView(withId(R.id.detail_content)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU08-10 (Positivo – Flujo completo): Llenar form, confirmar guardar y regresar al detalle del álbum.
     */
    @Test
    fun tc_hu08_10_successfulSavePopsBackToAlbumDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAlbumDetail()
            onView(withId(R.id.btn_associate_track)).perform(click())
            onView(withId(R.id.et_track_name)).perform(replaceText("Bohemian Rhapsody"), closeSoftKeyboard())
            onView(withId(R.id.et_track_duration)).perform(replaceText("5:55"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())
            // Confirmar en el diálogo
            onView(withId(android.R.id.button1)).perform(click())
            // Regresa al detalle
            onView(withId(R.id.detail_content)).check(matches(isDisplayed()))
        }
    }
}
