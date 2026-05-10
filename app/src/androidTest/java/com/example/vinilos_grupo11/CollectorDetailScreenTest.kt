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
import com.example.vinilos_grupo11.fake.FakeCollectorRepository
import com.example.vinilos_grupo11.ui.MainActivity
import com.example.vinilos_grupo11.viewmodels.AlbumListViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorDetailViewModel
import com.example.vinilos_grupo11.viewmodels.CollectorsViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU06-01 a TC-HU06-04: Pruebas E2E de pantalla para la HU06 (Detalle de Coleccionista).
 * Validan visualización de datos, manejo de error y estabilidad.
 */
@RunWith(AndroidJUnit4::class)
class CollectorDetailScreenTest {

    private val fakeRepo = FakeCollectorRepository(shouldFail = false)

    @get:Rule
    val disableAnimations = DisableAnimationsRule()

    @Before
    fun setUp() {
        AlbumListViewModel.testRepositoryFactory = { _ -> FakeAlbumRepository() }
        CollectorsViewModel.testRepositoryFactory = { _ -> fakeRepo }
        CollectorDetailViewModel.testRepositoryFactory = { _ -> fakeRepo }
    }

    @After
    fun tearDown() {
        AlbumListViewModel.testRepositoryFactory = null
        CollectorsViewModel.testRepositoryFactory = null
        CollectorDetailViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU06-01 (Positivo): Al tocar un coleccionista el nombre se muestra en la pantalla de detalle.
     * Valida CA1 (visualizar nombre del coleccionista).
     */
    @Test
    fun tc_hu06_01_collectorNameIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_collector_name)).check(matches(isDisplayed()))
            onView(withText(fakeRepo.fakeCollectors[0].name)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU06-02 (Positivo): El email del coleccionista se muestra en la pantalla de detalle.
     * Valida CA2 (visualizar información de contacto).
     */
    @Test
    fun tc_hu06_02_collectorEmailIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_collector_email_value)).check(matches(isDisplayed()))
            onView(withText(fakeRepo.fakeCollectors[0].email)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU06-03 (Positivo): El teléfono del coleccionista se muestra en la pantalla de detalle.
     * Valida CA2 (visualizar información de contacto).
     */
    @Test
    fun tc_hu06_03_collectorPhoneIsDisplayedInDetail() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_collector_phone_value)).check(matches(isDisplayed()))
            onView(withText(fakeRepo.fakeCollectors[0].telephone)).check(matches(isDisplayed()))
        }
    }

    /**
     * TC-HU06-04 (Negativo): Cuando el API falla al cargar el detalle se muestra el mensaje de error.
     * Valida OBJ-02 (manejo de error de conectividad).
     */
    @Test
    fun tc_hu06_04_errorMessageIsShownWhenDetailApiFails() {
        CollectorDetailViewModel.testRepositoryFactory = { _ -> FakeCollectorRepository(shouldFail = true) }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.collectorListFragment)).perform(click())
            onView(withId(R.id.rv_collectors))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tv_collector_detail_error)).check(matches(isDisplayed()))
            onView(withId(R.id.content_group)).check(matches(not(isDisplayed())))
        }
    }
}
