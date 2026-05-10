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
import com.example.vinilos_grupo11.fake.FakeCollectorRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorDetailViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorsViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU06-05 y TC-HU06-06: Pruebas de navegación para la HU06 (Detalle de Coleccionista).
 */
@RunWith(AndroidJUnit4::class)
class CollectorDetailNavigationTest {

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        CollectorsViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository() }
        CollectorDetailViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository() }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        CollectorsViewModel.testRepositoryFactory = null
        CollectorDetailViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU06-05 (Positivo - Navegación): Al tocar un coleccionista en la lista se navega a la pantalla de detalle.
     */
    @Test
    fun tc_hu06_05_clickingCollectorNavigatesToDetailScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
            onView(withId(R.id.tv_collector_name)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU06-06 (Positivo - Navegación): El botón de retroceso desde el detalle vuelve a la lista de coleccionistas.
     */
    @Test
    fun tc_hu06_06_backButtonFromDetailReturnsToCollectorList() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.popBackStack()
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            onView(withId(R.id.rv_collectors)).check(matches(isDisplayed()))
        }
    }
}
