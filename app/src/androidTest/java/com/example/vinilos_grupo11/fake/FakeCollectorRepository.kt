package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Collector
import com.example.vinilos_grupo11.repositories.ICollectorRepository

class FakeCollectorRepository(private val shouldFail: Boolean = false) : ICollectorRepository {

    val fakeCollectors = listOf(
        Collector(id = 1, name = "Manolo Bellon", telephone = "3502457896", email = "manollo@caracol.com.co"),
        Collector(id = 2, name = "Jaime Monsalve", telephone = "3012357989", email = "j.monsalve@me.com"),
        Collector(id = 3, name = "Shepard Fairy", telephone = "3022178798", email = "sfairy@usa.com")
    )

    override fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: () -> Unit
    ) {
        if (shouldFail) {
            onError()
        } else {
            onSuccess(fakeCollectors)
        }
    }
}
