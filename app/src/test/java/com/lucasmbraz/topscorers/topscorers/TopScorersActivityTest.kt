package com.lucasmbraz.topscorers.topscorers

import android.os.Build
import android.view.View.VISIBLE
import com.lucasmbraz.topscorers.Injection
import com.lucasmbraz.topscorers.data.FakeTopScorersRepository
import com.lucasmbraz.topscorers.model.Player
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_top_scorers.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.RuntimeException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class TopScorersActivityTest {

    @Test fun showSadPath_whenNoScorers() {
        Injection.fakeRepository = FakeTopScorersRepository(Single.just(emptyList()))

        val activity = Robolectric.setupActivity(TopScorersActivity::class.java)

        assertEquals(VISIBLE, activity.sadPath.visibility)
        assertEquals("No one's scored a goal yet", activity.sadPathTitle.text)
        assertEquals("Please try again after a game.", activity.sadPathMessage.text)
    }

    @Test fun showSadPath_whenException() {
        Injection.fakeRepository = FakeTopScorersRepository(Single.error(RuntimeException()))

        val activity = Robolectric.setupActivity(TopScorersActivity::class.java)

        assertEquals(VISIBLE, activity.sadPath.visibility)
        assertEquals("Oh no!", activity.sadPathTitle.text)
        assertEquals("Something went wrong. Please try again later.", activity.sadPathMessage.text)
    }

    @Test fun showTopScorers() {
        val players = listOf(Player(id = "", name = "John Smith", position = "Midfield", goals = 2, team = "Barcelona", picture = ""))
        Injection.fakeRepository = FakeTopScorersRepository(Single.just(players))

        val activity = Robolectric.setupActivity(TopScorersActivity::class.java)
        val recyclerView = activity.recyclerView

        assertEquals(VISIBLE, recyclerView.visibility)
        assertEquals(1, recyclerView.adapter?.itemCount)
        val adapter = recyclerView.adapter as PlayersAdapter
        assertEquals(players, adapter.players)
    }
}