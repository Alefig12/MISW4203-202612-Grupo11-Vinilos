package com.example.vinilos_grupo11

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.CreateAlbumViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU07-05 a TC-HU07-08: Pruebas de navegación para la HU07 (Crear Álbum).
 */
@RunWith(AndroidJUnit4::class)
class CreateAlbumNavigationTest {

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
        CreateAlbumViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        CreateAlbumViewModel.testRepositoryFactory = null
        setUserRole("Visitante")
    }

    /**
     * TC-HU07-05 (Positivo – Rol): El FAB de crear álbum es visible para el rol Coleccionista.
     */
    @Test
    fun tc_hu07_05_fabIsVisibleForCollectorRole() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU07-05b (Negativo – Rol): El FAB de crear álbum NO es visible para el rol Visitante.
     */
    @Test
    fun tc_hu07_05b_fabIsNotVisibleForVisitorRole() {
        setUserRole("Visitante")
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).check(matches(not(isDisplayed())))
        }
    }

    /**
     * TC-HU07-06 (Positivo – Navegación): Al tocar el FAB se navega al formulario de crear álbum.
     */
    @Test
    fun tc_hu07_06_fabNavigatesToCreateAlbumForm() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.til_name)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU07-07 (Positivo – Navegación): El botón de retroceso desde el formulario regresa a la lista de álbumes.
     */
    @Test
    fun tc_hu07_07_backFromCreateAlbumReturnsToAlbumList() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.fab_create_album)).perform(click())
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.popBackStack()
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            onView(withId(R.id.albumsRecyclerView)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU07-08 (Positivo – Flujo completo): Al completar el formulario y guardar exitosamente,
     * la app navega de regreso al catálogo de álbumes.
     */
    @Test
    fun tc_hu07_08_successfulAlbumCreationNavigatesBackToList() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.et_name)).perform(replaceText("Nuevo Álbum de Prueba"), closeSoftKeyboard())
            onView(withId(R.id.et_cover)).perform(replaceText("https://example.com/cover.jpg"), closeSoftKeyboard())
            onView(withId(R.id.et_date)).perform(click())
            onView(withId(android.R.id.button1)).perform(click())
            onView(withId(R.id.et_description)).perform(scrollTo(), replaceText("Una descripción del álbum"), closeSoftKeyboard())
            onView(withId(R.id.acv_genre)).perform(scrollTo(), click())
            onView(withText("Classical")).inRoot(isPlatformPopup()).perform(click())
            onView(withId(R.id.acv_record_label)).perform(scrollTo(), click())
            onView(withText("Sony Music")).inRoot(isPlatformPopup()).perform(click())
            onView(withId(R.id.btn_save)).perform(scrollTo(), click())
            onView(withId(R.id.albumsRecyclerView)).check(matches(isDisplayed()))
        }
    }
}
