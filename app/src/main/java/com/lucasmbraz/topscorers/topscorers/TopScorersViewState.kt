package com.lucasmbraz.topscorers.topscorers

import com.lucasmbraz.topscorers.model.Player
import com.lucasmbraz.topscorers.mvibase.MviViewState

data class TopScorersViewState(
        val players: List<Player>,
        val isLoading: Boolean,
        val error: TopScorersError?
) : MviViewState {

    enum class TopScorersError { GENERIC, NO_SCORERS }

    companion object {
        fun idle() = TopScorersViewState(players = emptyList(), isLoading = false, error = null)
    }
}