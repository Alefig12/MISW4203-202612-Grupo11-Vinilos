package com.example.vinilos_grupo11

import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.fake.FakeArtistRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistDetailViewModel
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU02-04 y TC-HU02-05: Pruebas de navegación para la HU02 (Detalle de Artista).
 */
@RunWith(AndroidJUnit4::class)
class ArtistDetailNavigationTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        ArtistViewModel.testRepositoryFactory = { _ -> FakeArtistRepository() }
        ArtistDetailViewModel.testRepositoryFactory = { _ -> FakeArtistRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        ArtistViewModel.testRepositoryFactory = null
        ArtistDetailViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU02-04 (Positivo - Navegación): Al tocar un artista en la lista se navega a la pantalla de detalle.
     */
    @Test
    fun tc_hu02_04_clickingArtistNavigatesToDetailScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
            onView(withId(R.id.tvArtistName)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU02-05 (Positivo - Navegación): El botón de retroceso desde el detalle vuelve a la lista de artistas.
     */
    @Test
    fun tc_hu02_05_backButtonFromDetailReturnsToArtistList() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.rv_artists))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.popBackStack()
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            onView(withId(R.id.rv_artists)).check(matches(isDisplayed()))
        }
    }
}
