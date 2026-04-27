package com.example.vinilos_grupo11

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilos_grupo11.fake.FakeAlbumRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumsScreenTest {

    private val fakeRepo = FakeAlbumRepository(shouldFail = false)

    @Before
    fun setUp() {
        // Establecer el repositorio falso ANTES de lanzar la actividad
        AlbumListViewModel.testRepositoryFactory = { _ -> fakeRepo }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
    }

    @Test
    fun albumNamesAreDisplayedInList() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.albumsRecyclerView)).check(matches(isDisplayed()))
            
            // Esperar un poco o asegurarse de que el looper se procese
            // Espresso debería manejar esto, pero al ser el fragmento inicial, 
            // lanzarlo manualmente asegura que el Factory ya esté seteado.
            
            fakeRepo.fakeAlbums.forEach { album ->
                onView(withText(album.name)).check(matches(isDisplayed()))
            }
        }
    }
}
