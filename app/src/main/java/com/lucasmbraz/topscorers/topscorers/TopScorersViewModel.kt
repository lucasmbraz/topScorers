package com.lucasmbraz.topscorers.topscorers

import android.arch.lifecycle.ViewModel
import com.lucasmbraz.topscorers.topscorers.TopScorersAction.LoadTopScorersAction
import com.lucasmbraz.topscorers.topscorers.TopScorersIntent.InitialIntent
import com.lucasmbraz.topscorers.topscorers.TopScorersResult.LoadTopScorersResult
import com.lucasmbraz.topscorers.topscorers.TopScorersViewState.TopScorersError.GENERIC
import com.lucasmbraz.topscorers.topscorers.TopScorersViewState.TopScorersError.NO_SCORERS
import com.lucasmbraz.topscorers.mvibase.*
import com.lucasmbraz.topscorers.utils.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class TopScorersViewModel(
        private val actionProcessorHolder: TopScorersActionProcessorHolder
) : ViewModel(), MviViewModel<TopScorersIntent, TopScorersViewState> {

    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentsSubject: PublishSubject<TopScorersIntent> = PublishSubject.create()
    private val statesObservable: Observable<TopScorersViewState> = compose()

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<TopScorersIntent, TopScorersIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(TopScorersIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(TopScorersIntent.InitialIntent::class.java)
                )
            }
        }

    override fun processIntents(intents: Observable<TopScorersIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<TopScorersViewState> = statesObservable

    /**
     * Compose all components to create the stream logic
     */
    private fun compose(): Observable<TopScorersViewState> {
        return intentsSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            // Cache each state and pass it to the reducer to create a new state from
            // the previous cached one and the latest Result emitted from the action processor.
            // The Scan operator is used here for the caching.
            .scan(TopScorersViewState.idle(), reducer)
            // When a reducer just emits previousState, there's no reason to call render. In fact,
            // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
            // by showing the same snackbar twice in rapid succession).
            .distinctUntilChanged()
            // Emit the last one event of the stream on subscription
            // Useful when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream on creation without waiting for anyone to subscribe
            // This allows the stream to stay alive even when the UI disconnects and
            // match the stream's lifecycle to the ViewModel's one.
            .autoConnect(0)
    }

    /**
     * Translate an [MviIntent] to an [MviAction].
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     */
    private fun actionFromIntent(intent: TopScorersIntent): TopScorersAction {
        return when (intent) {
            is InitialIntent -> LoadTopScorersAction()
        }
    }

    companion object {
        /**
         * The Reducer is where [MviViewState]s, that the [MviView] will use to
         * render itself, are created.
         * It takes the last cached [MviViewState], the latest [MviResult] and
         * creates a new [MviViewState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [MviResult]
         */
        private val reducer = BiFunction { previousState: TopScorersViewState, result: TopScorersResult ->
            when (result) {
                is LoadTopScorersResult -> when (result) {
                    is LoadTopScorersResult.InFlight -> previousState.copy(isLoading = previousState.players.isEmpty())
                    is LoadTopScorersResult.Failure -> previousState.copy(error = GENERIC, isLoading = false)
                    is LoadTopScorersResult.Success -> {
                        if (result.players.isNotEmpty()) {
                            val combinedPlayers = previousState.players + result.players
                            previousState.copy(players = combinedPlayers, isLoading = false, error = null)
                        } else {
                            // Show sad path
                            previousState.copy(error = NO_SCORERS, isLoading = false)
                        }
                    }
                }
            }
        }
    }
}