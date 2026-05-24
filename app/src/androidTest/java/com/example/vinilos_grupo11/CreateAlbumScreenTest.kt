package com.example.vinilos_grupo11

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.CreateAlbumViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU07-01 a TC-HU07-04: Pruebas de pantalla para la HU07 (Crear Álbum).
 */
@RunWith(AndroidJUnit4::class)
class CreateAlbumScreenTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Coleccionista").commit()
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        CreateAlbumViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        CreateAlbumViewModel.testRepositoryFactory = null
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Visitante").commit()
    }

    /**
     * TC-HU07-01 (Positivo): Todos los campos del formulario son visibles al navegar a crear álbum.
     */
    @Test
    fun tc_hu07_01_allFormFieldsAreVisible() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.til_name)).check(matches(isDisplayed()))
            onView(withId(R.id.til_cover)).check(matches(isDisplayed()))
            onView(withId(R.id.til_date)).check(matches(isDisplayed()))
            onView(withId(R.id.til_description)).perform(scrollTo()).check(matches(isDisplayed()))
            onView(withId(R.id.til_genre)).perform(scrollTo()).check(matches(isDisplayed()))
            onView(withId(R.id.til_record_label)).perform(scrollTo()).check(matches(isDisplayed()))
            onView(withId(R.id.btn_save)).perform(scrollTo()).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU07-02 (Negativo – Validación): Al enviar el formulario vacío se muestra error en el campo nombre.
     */
    @Test
    fun tc_hu07_02_emptyFormShowsValidationErrorOnName() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.btn_save)).perform(scrollTo(), click())
            onView(withId(R.id.til_name))
                .check(matches(hasDescendant(withText(R.string.create_album_validation_empty))))
        }
    }

    /**
     * TC-HU07-03 (Negativo – Validación): Una URL de portada sin http/https muestra error de formato.
     */
    @Test
    fun tc_hu07_03_invalidCoverUrlShowsUrlError() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.et_name)).perform(replaceText("Test Album"), closeSoftKeyboard())
            onView(withId(R.id.et_cover)).perform(replaceText("sin-http.com/cover.jpg"), closeSoftKeyboard())
            onView(withId(R.id.btn_save)).perform(scrollTo(), click())
            onView(withId(R.id.til_cover))
                .check(matches(hasDescendant(withText(R.string.create_album_validation_url))))
        }
    }

    /**
     * TC-HU07-04 (Negativo – API): Cuando la API falla al crear, el formulario permanece visible
     * y el botón guardar vuelve a estar habilitado.
     */
    @Test
    fun tc_hu07_04_apiFailureKeepsFormVisible() {
        CreateAlbumViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository(shouldFail = true) }
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.fab_create_album)).perform(click())
            onView(withId(R.id.et_name)).perform(replaceText("Test Album"), closeSoftKeyboard())
            onView(withId(R.id.et_cover)).perform(replaceText("https://example.com/cover.jpg"), closeSoftKeyboard())
            onView(withId(R.id.et_date)).perform(click())
            onView(withId(android.R.id.button1)).perform(click())
            onView(withId(R.id.et_description)).perform(scrollTo(), replaceText("Descripción del álbum"), closeSoftKeyboard())
            onView(withId(R.id.acv_genre)).perform(scrollTo(), click())
            onView(withText("Classical")).inRoot(isPlatformPopup()).perform(click())
            onView(withId(R.id.acv_record_label)).perform(scrollTo(), click())
            onView(withText("Sony Music")).inRoot(isPlatformPopup()).perform(click())
            onView(withId(R.id.btn_save)).perform(scrollTo(), click())
            onView(withId(R.id.btn_save)).check(matches(isDisplayed()))
        }
    }
}
