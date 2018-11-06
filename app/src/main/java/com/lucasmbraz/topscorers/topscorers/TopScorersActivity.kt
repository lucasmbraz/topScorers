package com.lucasmbraz.topscorers.topscorers

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import com.lucasmbraz.topscorers.R
import com.lucasmbraz.topscorers.topscorers.TopScorersViewState.TopScorersError
import com.lucasmbraz.topscorers.topscorers.TopScorersViewState.TopScorersError.*
import com.lucasmbraz.topscorers.model.Player
import com.lucasmbraz.topscorers.mvibase.MviIntent
import com.lucasmbraz.topscorers.mvibase.MviView
import com.lucasmbraz.topscorers.mvibase.MviViewModel
import com.lucasmbraz.topscorers.mvibase.MviViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_top_scorers.*

class TopScorersActivity : AppCompatActivity(), MviView<TopScorersIntent, TopScorersViewState> {

    private val disposables = CompositeDisposable()

    private lateinit var adapter: PlayersAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val viewModel: TopScorersViewModel by lazy {
        ViewModelProviders.of(this, TopScorersViewModelFactory()).get(TopScorersViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_scorers)

        layoutManager = LinearLayoutManager(this@TopScorersActivity)
        adapter = PlayersAdapter()

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = this@TopScorersActivity.layoutManager
            adapter = this@TopScorersActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        bind()
    }

    /**
     * Connect the [MviView] with the [MviViewModel]
     * We subscribe to the [MviViewModel] before passing it the [MviView]'s [MviIntent]s.
     * If we were to pass [MviIntent]s to the [MviViewModel] before listening to it,
     * emitted [MviViewState]s could be lost
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun intents() = initialIntent()

    /**
     * The initial Intent the [MviView] emit to convey to the [MviViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [MviViewModel] might need
     * to render the initial [MviViewState] (e.g. the task id to load).
     */
    private fun initialIntent(): Observable<TopScorersIntent> = Observable.just(TopScorersIntent.InitialIntent)

    override fun render(state: TopScorersViewState) {
        renderProgressIndicator(state.isLoading)
        renderSadPath(state.error)
        renderPlayers(state.players)
    }

    private fun renderProgressIndicator(isLoading: Boolean) {
        progress.visibility = if (isLoading) VISIBLE else GONE
    }

    private fun renderSadPath(error: TopScorersError?) {
        if (error == null) {
            sadPath.visibility = GONE
        } else {
            sadPath.visibility = VISIBLE
            when (error) {
                GENERIC -> {
                    sadPathImage.setImageResource(R.drawable.red_card)
                    sadPathTitle.setText(R.string.sad_path_generic_title)
                    sadPathMessage.setText(R.string.sad_path_generic_message)
                }
                NO_SCORERS -> {
                    sadPathImage.setImageResource(R.drawable.no_scorers)
                    sadPathTitle.setText(R.string.sad_path_no_scorers_title)
                    sadPathMessage.setText(R.string.sad_path_no_scorers_message)
                }
            }
        }
    }

    private fun renderPlayers(players: List<Player>) {
        recyclerView.visibility = if (players.isNotEmpty()) VISIBLE else GONE
        adapter.updateData(players)
    }
}
