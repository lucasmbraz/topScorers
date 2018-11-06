package com.lucasmbraz.topscorers.topscorers

import com.lucasmbraz.topscorers.model.Player
import com.lucasmbraz.topscorers.mvibase.MviResult

sealed class TopScorersResult : MviResult {
    sealed class LoadTopScorersResult : TopScorersResult() {
        data class Success(val players: List<Player>) : LoadTopScorersResult()
        data class Failure(val error: Throwable) : LoadTopScorersResult()
        object InFlight : LoadTopScorersResult()
    }
}