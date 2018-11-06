package com.lucasmbraz.topscorers.data

import com.lucasmbraz.topscorers.model.Player
import io.reactivex.Single

class FakeTopScorersRepository(private val response: Single<List<Player>>) : TopScorersRepository {

    override fun getTopScorers() = response
}