package com.example.backvynils_app_grupo_11

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.backvynils_app_grupo_11.fake.FakeCollectorRepository
import com.example.backvynils_app_grupo_11.viewmodel.CollectorsViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TC-HU05-01 y TC-HU05-02: Pruebas E2E positivas con datos fake.
 * Validan CA1 (visualizar todos los coleccionistas) y CA2 (ver nombre).
 */
@RunWith(AndroidJUnit4::class)
class CollectorsScreenTest {

    private val fakeRepo = FakeCollectorRepository(shouldFail = false)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        CollectorsViewModel.testRepositoryFactory = { _ -> fakeRepo }
    }

    @After
    fun tearDown() {
        CollectorsViewModel.testRepositoryFactory = null
    }

    /**
     * TC-HU05-01 (Positivo): Al navegar a Coleccionistas el título es visible.
     * Valida CA1 y CA2.
     */
    @Test
    fun tc_hu05_01_collectorsTitleIsDisplayed() {
        onView(withId(R.id.collectorsFragment)).perform(click())
        onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
    }

    /**
     * TC-HU05-02 (Positivo - variación): Los nombres de coleccionistas son visibles en la lista.
     * Valida CA1 y CA2.
     */
    @Test
    fun tc_hu05_02_collectorsNamesAreDisplayed() {
        onView(withId(R.id.collectorsFragment)).perform(click())
        onView(withId(R.id.rv_collectors)).check(matches(isDisplayed()))
        fakeRepo.fakeCollectors.forEach { collector ->
            onView(withText(collector.name)).check(matches(isDisplayed()))
        }
    }
}
