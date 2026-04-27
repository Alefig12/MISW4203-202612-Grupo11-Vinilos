package com.example.vinilos_grupo11

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumsNavigationTest {

    @Before
    fun setUp() {
        // No necesitamos inicializar CollectorsViewModel aquí si no lo usamos
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
    }

    /**
     * Test negativo: Cuando el API de álbumes falla.
     */
    @Test
    fun albumErrorMessageIsShownWhenApiFails() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository(shouldFail = true) }
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tv_albums_error)).check(matches(isDisplayed()))
        }
    }

    /**
     * Test positivo: El catálogo de álbumes es visible al abrir la app (es la pantalla inicial).
     */
    @Test
    fun albumsTitleIsVisibleOnLaunch() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tv_catalog_header)).check(matches(isDisplayed()))
        }
    }
}
