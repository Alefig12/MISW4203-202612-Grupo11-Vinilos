package com.example.vinilos_grupo11

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AddTrackViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU08-01 a TC-HU08-05: Pruebas de pantalla para la HU08 (Asociar Track).
 */
@RunWith(AndroidJUnit4::class)
class AddTrackScreenTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Coleccionista").commit()
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AlbumDetailViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        AddTrackViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        AlbumDetailViewModel.testRepositoryFactory = null
        AddTrackViewModel.testRepositoryFactory = null
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("VinilosPrefs", Context.MODE_PRIVATE)
            .edit().putString("user_role", "Visitante").commit()
    }

    private fun openAddTrackForm() {
        onView(withId(R.id.albumsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.btn_associate_track)).perform(click())
    }

    /**
     * TC-HU08-01 (Positivo): Todos los campos del formulario son visibles al abrir Asociar Track.
     */
    @Test
    fun tc_hu08_01_allFormFieldsAreVisible() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAddTrackForm()
            onView(withId(R.id.til_album_name)).check(matches(isDisplayed()))
            onView(withId(R.id.et_album_name)).check(matches(isDisplayed()))
            onView(withId(R.id.til_track_name)).check(matches(isDisplayed()))
            onView(withId(R.id.til_track_duration)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_save_track)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_add_track_title)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU08-02 (Negativo – Validación): Form vacío muestra error en nombre y duración.
     */
    @Test
    fun tc_hu08_02_emptyFormShowsValidationErrors() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAddTrackForm()
            onView(withId(R.id.btn_save_track)).perform(click())
            onView(withId(R.id.til_track_name))
                .check(matches(hasDescendant(withText(R.string.error_field_required))))
            onView(withId(R.id.til_track_duration))
                .check(matches(hasDescendant(withText(R.string.error_field_required))))
        }
    }

    /**
     * TC-HU08-03 (Negativo – Validación): Duraciones malformadas disparan error de formato.
     */
    @Test
    fun tc_hu08_03_invalidDurationFormatShowsError() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAddTrackForm()
            onView(withId(R.id.et_track_name)).perform(replaceText("Pista Test"), closeSoftKeyboard())

            // Caso A: minutos fuera de rango
            onView(withId(R.id.et_track_duration)).perform(replaceText("99:99"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())
            onView(withId(R.id.til_track_duration))
                .check(matches(hasDescendant(withText(R.string.error_invalid_duration))))

            // Caso B: no es un tiempo
            onView(withId(R.id.et_track_duration)).perform(replaceText("abc"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())
            onView(withId(R.id.til_track_duration))
                .check(matches(hasDescendant(withText(R.string.error_invalid_duration))))

            // Caso C: falta separador
            onView(withId(R.id.et_track_duration)).perform(replaceText("5"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())
            onView(withId(R.id.til_track_duration))
                .check(matches(hasDescendant(withText(R.string.error_invalid_duration))))
        }
    }

    /**
     * TC-HU08-04 (Positivo – Flujo): El diálogo de confirmación de guardar aparece y "Cancelar" mantiene el formulario.
     */
    @Test
    fun tc_hu08_04_confirmSaveDialogCancelKeepsForm() {
        ActivityScenario.launch(MainActivity::class.java).use {
            openAddTrackForm()
            onView(withId(R.id.et_track_name)).perform(replaceText("Pista Test"), closeSoftKeyboard())
            onView(withId(R.id.et_track_duration)).perform(replaceText("3:45"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())

            // Diálogo visible
            onView(withText(R.string.title_confirm_save)).check(matches(isDisplayed()))
            // Cancelar mantiene el formulario
            onView(withText(R.string.btn_cancel)).perform(click())
            onView(withId(R.id.til_track_name)).check(matches(isDisplayed()))
            onView(withId(R.id.til_track_duration)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU08-05 (Negativo – API): Si la API falla al asociar, el formulario sigue visible
     * y el botón de guardar vuelve a estar habilitado.
     */
    @Test
    fun tc_hu08_05_apiFailureKeepsFormVisibleAndReenablesButton() {
        AddTrackViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository(shouldFail = true) }
        ActivityScenario.launch(MainActivity::class.java).use {
            openAddTrackForm()
            onView(withId(R.id.et_track_name)).perform(replaceText("Pista Test"), closeSoftKeyboard())
            onView(withId(R.id.et_track_duration)).perform(replaceText("3:45"), closeSoftKeyboard())
            onView(withId(R.id.btn_save_track)).perform(click())
            // Confirmar en el diálogo (positive = android.R.id.button1)
            onView(withId(android.R.id.button1)).perform(click())

            // Tras el error, el form sigue visible y el botón está habilitado
            onView(withId(R.id.btn_save_track)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_save_track)).check(matches(isEnabled()))
            onView(withId(R.id.til_track_name)).check(matches(isDisplayed()))
        }
    }
}
