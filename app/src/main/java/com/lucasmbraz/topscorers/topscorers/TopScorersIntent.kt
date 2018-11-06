package com.lucasmbraz.topscorers.topscorers

import com.lucasmbraz.topscorers.mvibase.MviIntent

sealed class TopScorersIntent : MviIntent {
    object InitialIntent : TopScorersIntent()
}