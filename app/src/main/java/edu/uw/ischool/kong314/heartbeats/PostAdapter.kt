package edu.uw.ischool.kong314.heartbeats

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter (
    var posts:List<Post>
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(postView: View): RecyclerView.ViewHolder(postView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvTitle).text = posts[position].title
//            findViewById<TextView>(R.id.tvImage). = posts[position].image
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}