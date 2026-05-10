package com.example.vinilos_grupo11

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.fake.FakeArtistRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.ArtistViewModel
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU03 al TC-HU03-04: Pruebas de navegación para Artistas.
 * Validan el flujo de navegación (OBJ-01) y la estabilidad (OBJ-03). */
@RunWith(AndroidJUnit4::class)
class ArtistsNavigationTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        ArtistViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU03-03 (Navegación): El menú de navegación inferior es visible al abrir la app.
     * Valida OBJ-01 y OBJ-03. */
    @Test
    fun tc_nav_01_bottomNavigationIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU03-04 (Positivo - Navegación): Al navegar a Artistas se muestra el encabezado correcto.
     * Valida OBJ-01 y OBJ-03.
     */
    @Test
    fun tc_nav_02_navigatingToArtistsShowsHeader() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.artistListFragment)).perform(click())
            onView(withId(R.id.tv_artists_header)).check(matches(isDisplayed()))
        }
    }
}