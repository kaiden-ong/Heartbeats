package edu.uw.ischool.kong314.heartbeats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter (private var posts:ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(postView: View): RecyclerView.ViewHolder(postView) {
        val postImage: ImageView = itemView.findViewById(R.id.rvImage)
        val postText: TextView = itemView.findViewById(R.id.rvTitle)
        val postUsername: TextView = itemView.findViewById(R.id.rvUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = posts[position]
        Glide.with(holder.itemView.context)
            .load(currentItem.image) // Assuming `imageUrl` is the URL of the image
            .into(holder.postImage)
        holder.postText.text = currentItem.title
        holder.postUsername.text = currentItem.username
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}