package com.lucasmbraz.topscorers.topscorers

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lucasmbraz.topscorers.R
import com.lucasmbraz.topscorers.topscorers.PlayersAdapter.PlayerViewHolder
import com.lucasmbraz.topscorers.model.Player
import com.lucasmbraz.topscorers.utils.GlideApp
import kotlinx.android.synthetic.main.list_item_player.view.*

class PlayersAdapter(var players: List<Player> = emptyList()) : RecyclerView.Adapter<PlayerViewHolder>() {

    class PlayerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView by lazy { view.name }
        val team: TextView by lazy { view.team }
        val goals: TextView by lazy { view.goals }
        val image: ImageView by lazy { view.image }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        val context = holder.view.context
        holder.name.text = player.name
        holder.team.text = player.team
        holder.goals.text = context.resources.getQuantityString(R.plurals.goals, player.goals, player.goals)
        GlideApp.with(context)
                .load(player.picture)
                .placeholder(R.drawable.user_image_placeholder)
                .error(R.drawable.user_image_placeholder)
                .circleCrop()
                .into(holder.image)
    }

    fun updateData(players: List<Player>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(this.players, players))
        this.players = players
        diffResult.dispatchUpdatesTo(this)
    }
}

class UserDiffCallback(
        private val oldPlayers: List<Player>,
        private val newPlayers: List<Player>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldPlayers.size

    override fun getNewListSize() = newPlayers.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int)
            = oldPlayers[oldPosition].id == newPlayers[newPosition].id

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int)
            = oldPlayers[oldPosition] == newPlayers[newPosition]
}