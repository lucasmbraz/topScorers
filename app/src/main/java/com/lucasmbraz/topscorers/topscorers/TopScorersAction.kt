package com.lucasmbraz.topscorers.topscorers

import com.lucasmbraz.topscorers.mvibase.MviAction

sealed class TopScorersAction : MviAction {
    class LoadTopScorersAction : TopScorersAction()
}