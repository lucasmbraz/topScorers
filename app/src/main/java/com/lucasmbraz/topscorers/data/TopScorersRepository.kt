package com.lucasmbraz.topscorers.data

import com.lucasmbraz.topscorers.model.Player
import com.lucasmbraz.topscorers.network.FootballApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder

interface TopScorersRepository {
    fun getTopScorers(): Single<List<Player>>
}

class ProductionTopScorersRepository(private val footballApi: FootballApi) : TopScorersRepository {
    override fun getTopScorers(): Single<List<Player>> {
        return footballApi.topScorers()
                .map { it.scorers.map { scorerResponse ->
                    val id = scorerResponse.player.id
                    val name = scorerResponse.player.name
                    val position = scorerResponse.player.position
                    val team = scorerResponse.team.name
                    val goals = scorerResponse.numberOfGoals
                    val encodedName = URLEncoder.encode(name, "utf-8")
                    val picture = ("https://robohash.org/$encodedName")
                    Player(id, name, position, team, goals, picture)
                } }
                .subscribeOn(Schedulers.io())
    }
}