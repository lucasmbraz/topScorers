package com.lucasmbraz.topscorers.topscorers

import android.util.Log
import com.lucasmbraz.topscorers.data.TopScorersRepository
import com.lucasmbraz.topscorers.topscorers.TopScorersAction.LoadTopScorersAction
import com.lucasmbraz.topscorers.topscorers.TopScorersResult.LoadTopScorersResult
import com.lucasmbraz.topscorers.mvibase.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Contains and executes the business logic for all emitted [MviAction]
 * and returns one unique [Observable] of [MviResult].
 *
 *
 * This could have been included inside the [MviViewModel]
 * but was separated to ease maintenance, as the [MviViewModel] was getting too big.
 */
class TopScorersActionProcessorHolder(private val repository: TopScorersRepository) {

    private val loadTopScorersProcessor =
        ObservableTransformer<LoadTopScorersAction, LoadTopScorersResult> { actions ->
          actions.flatMap {
            repository.getTopScorers()
                // Transform the Single to an Observable to allow emission of multiple
                // events down the stream (e.g. the InFlight event)
                .toObservable()
                // Wrap returned data into an immutable object
                .map { players -> LoadTopScorersResult.Success(players) }
                .cast(LoadTopScorersResult::class.java)
                // Wrap any error into an immutable object and pass it down the stream
                // without crashing.
                // Because errors are data and hence, should just be part of the stream.
                .onErrorReturn { e ->
                    Log.e("ActionProcessorHolder", e.message, e)
                    LoadTopScorersResult.Failure(error = e)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                // doing work and waiting on a response.
                // We emit it after observing on the UI thread to allow the event to be emitted
                // on the current frame and avoid jank.
                .startWith(LoadTopScorersResult.InFlight)
          }
        }

    /**
     * Splits the [Observable] to match each type of [MviAction] to
     * its corresponding business logic processor. Each processor takes a defined [MviAction],
     * returns a defined [MviResult]
     * The global actionProcessor then merges all [Observable] back to
     * one unique [Observable].
     *
     *
     * The splitting is done using [Observable.publish] which allows almost anything
     * on the passed [Observable] as long as one and only one [Observable] is returned.
     *
     *
     * An security layer is also added for unhandled [MviAction] to allow early crash
     * at runtime to easy the maintenance.
     */
    internal val actionProcessor =
        ObservableTransformer<TopScorersAction, TopScorersResult> { actions ->
          actions.publish { shared ->
              Observable.merge(
                  shared.ofType(LoadTopScorersAction::class.java).compose(loadTopScorersProcessor),
                  // Error for not implemented actions
                  shared.filter { it !is LoadTopScorersAction }
                        .flatMap { Observable.error<TopScorersResult>(
                                IllegalArgumentException("Unknown Action type: $it"))
                        }
              )
          }
        }
}