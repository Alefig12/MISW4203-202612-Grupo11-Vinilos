package com.example.vinilos_grupo11

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeCollectorRepository
import com.example.vinilos_grupo11.viewmodel.CollectorsViewModel
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU05-03 al TC-HU05-05: Navegación y escenario negativo.
 * Validan CA3 (menú inferior) y OBJ-02 (manejo de error).
 */
@RunWith(AndroidJUnit4::class)
class CollectorsNavigationTest {

    @After
    fun tearDown() {
        CollectorsViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU05-03 (Negativo): Cuando el API falla se muestra el mensaje de error.
     * Valida OBJ-02 de la estrategia de pruebas.
     */
    @Test
    fun tc_hu05_03_errorMessageIsShownWhenApiFails() {
        CollectorsViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository(shouldFail = true) }
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorsFragment)).perform(click())
            onView(withId(R.id.tv_error)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU05-04 (Positivo - navegación): El menú de navegación inferior es visible al abrir la app.
     * Valida CA3.
     */
    @Test
    fun tc_hu05_04_bottomNavigationIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU05-05 (Positivo - navegación): Al tocar la pestaña Coleccionistas
     * la pantalla muestra el título por ID.
     * Valida CA3 y CA1.
     */
    @Test
    fun tc_hu05_05_navigatingToCollectorsShowsTitle() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorsFragment)).perform(click())
            onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
        }
    }
}
