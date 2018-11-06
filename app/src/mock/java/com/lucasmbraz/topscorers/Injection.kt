package com.lucasmbraz.topscorers

import com.lucasmbraz.topscorers.data.FakeTopScorersRepository
import com.lucasmbraz.topscorers.data.TopScorersRepository
import io.reactivex.Single

/**
 * Enables injection of mock implementations
 */
object Injection {

    var fakeRepository = FakeTopScorersRepository(Single.just(emptyList()))

    fun provideTopScorersRepository(): TopScorersRepository = fakeRepository
}