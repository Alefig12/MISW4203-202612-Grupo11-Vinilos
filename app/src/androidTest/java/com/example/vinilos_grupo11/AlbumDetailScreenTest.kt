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
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumDetailViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU04-01 a TC-HU04-04: Pruebas E2E de pantalla para la HU04 (Detalle de Álbum).
 * Validan visualización de datos, manejo de error y estabilidad.
 */
@RunWith(AndroidJUnit4::class)
class AlbumDetailScreenTest {

    private val fakeRepo = FakeAlbumRepository(shouldFail = false)

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> fakeRepo }
        AlbumDetailViewModel.testRepositoryFactory = { _ -> fakeRepo }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        AlbumDetailViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU04-01 (Positivo): Al tocar un álbum el nombre se muestra en la pantalla de detalle.
     * Valida CA1 (visualizar nombre del álbum).
     */
    @Test
    fun tc_hu04_01_albumNameIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.albumsRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_album_name)).check(matches(isDisplayed()))
            onView(withText(fakeRepo.fakeAlbumDetail.name)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU04-02 (Positivo): El género del álbum se muestra en la pantalla de detalle.
     * Valida CA2 (visualizar género).
     */
    @Test
    fun tc_hu04_02_albumGenreIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.albumsRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_album_genre)).check(matches(isDisplayed()))
            onView(withText(fakeRepo.fakeAlbumDetail.genre.uppercase())).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU04-03 (Positivo): El header de tracks es visible en la pantalla de detalle.
     * Valida CA3 (visualizar listado de tracks).
     */
    @Test
    fun tc_hu04_03_trackListHeaderIsVisibleInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.albumsRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_tracks_header)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU04-04 (Negativo): Cuando el API falla al cargar el detalle se muestra el mensaje de error.
     * Valida OBJ-02 (manejo de error de conectividad).
     */
    @Test
    fun tc_hu04_04_errorMessageIsShownWhenDetailApiFails() {
        AlbumDetailViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository(shouldFail = true) }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.albumsRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_detail_error)).check(matches(isDisplayed()))
            onView(withId(R.id.detail_content)).check(matches(not(isDisplayed())))
        }
    }
}
