package com.example.vinilos_grupo11

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.fake.FakeArtistRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistDetailViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU02-01 a TC-HU02-03: Pruebas E2E de pantalla para la HU02 (Detalle de Artista).
 * Validan visualización de datos, descripción y manejo de errores.
 */
@RunWith(AndroidJUnit4::class)
class ArtistDetailScreenTest {

    private val fakeArtistRepo = FakeArtistRepository(shouldFail = false)

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        ArtistViewModel.testRepositoryFactory = { _ -> fakeArtistRepo }
        ArtistDetailViewModel.testRepositoryFactory = { _ -> fakeArtistRepo }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        ArtistViewModel.testRepositoryFactory = null
        ArtistDetailViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU02-01 (Positivo): Al tocar un artista el nombre se muestra en la pantalla de detalle.
     * Valida CA1 (visualizar nombre del artista).
     */
    @Test
    fun tc_hu02_01_artistNameIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tvArtistName)).check(matches(isDisplayed()))
            onView(withText(fakeArtistRepo.fakeArtists[0].name)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU02-02 (Positivo): El texto de descripción/fecha del artista es visible en el detalle.
     * Valida CA2 (visualizar descripción y fecha de nacimiento).
     */
    @Test
    fun tc_hu02_02_artistDetailTextIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tvArtistDetail)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU02-03 (Negativo): Cuando el API falla al cargar el detalle, el ProgressBar no queda visible.
     * Valida OBJ-02 (manejo de error) y OBJ-03 (estabilidad sin crash).
     */
    @Test
    fun tc_hu02_03_errorLoadingDetailHidesProgressBar() {
        ArtistDetailViewModel.testRepositoryFactory = { _ -> FakeArtistRepository(shouldFail = true) }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        }
    }
}
