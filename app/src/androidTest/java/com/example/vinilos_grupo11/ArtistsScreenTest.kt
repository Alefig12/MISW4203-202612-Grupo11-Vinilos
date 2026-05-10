package com.example.vinilos_grupo11

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.fake.FakeArtistRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU03-01 al TC-HU03-02: Pruebas E2E para la historia de usuario HU03 (Artistas).
 * Validan la visualización de datos (OBJ-01), manejo de errores (OBJ-02) y estabilidad (OBJ-03).
 */
@RunWith(AndroidJUnit4::class)
class ArtistsScreenTest {

    private val fakeRepo = FakeArtistRepository(shouldFail = false)

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        ArtistViewModel.testRepositoryFactory = { _ -> fakeRepo }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        ArtistViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU03-01 (Positivo): Al navegar a la sección de Artistas, el encabezado es visible.
     * Valida OBJ-01 (flujo funcional E2E) y OBJ-03 (estabilidad).
     */
    @Test
    fun artistsHeaderIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.tv_artists_header)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU03-02 (Positivo - Variación): Los nombres de los artistas se visualizan correctamente en la lista.
     * Valida OBJ-01 (criterios de aceptación) y OBJ-03 (estabilidad).
     */
    @Test
    fun artistsNamesAreDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists)).check(matches(isDisplayed()))

            fakeRepo.fakeArtists.forEach { artist ->
                onView(withText(artist.name)).check(matches(isDisplayed()))
            }
        }
    }

    /**
     * TC-HU03-05 (Negativo): Manejo de error cuando falla el consumo de datos de la API.
     * Valida OBJ-02 (ausencia de conectividad / respuesta no exitosa) y OBJ-03 (estabilidad). */
    @Test
    fun errorIsShownWhenApiFails() {
        ArtistViewModel.testRepositoryFactory = { _ -> FakeArtistRepository(shouldFail = true) }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())

            onView(withId(R.id.rv_artists)).check(matches(not(isDisplayed())))
        }
    }
}
