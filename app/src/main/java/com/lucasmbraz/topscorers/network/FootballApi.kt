package com.lucasmbraz.topscorers.network

import io.reactivex.Single
import retrofit2.http.GET

interface FootballApi {
    @GET("competitions/CL/scorers?limit=50")
    fun topScorers(): Single<TopScorersResponse>
}

data class TopScorersResponse(val scorers: List<ScorerResponse>)

data class ScorerResponse(val player: PlayerResponse, val team: TeamResponse, val numberOfGoals: Int)

data class PlayerResponse(val id: String, val name: String, val position: String)

data class TeamResponse(val name: String)