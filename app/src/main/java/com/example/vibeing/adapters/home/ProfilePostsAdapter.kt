package com.example.vibeing.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vibeing.R
import com.example.vibeing.models.Post
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class ProfilePostsAdapter(val context: Context, private val postsList: ArrayList<Post>) : RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_in_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]
        if (post.postUrl.isNotBlank())
            Picasso.get().load(post.postUrl).placeholder(R.mipmap.ic_launcher).into(holder.postImage)
    }

    override fun getItemCount(): Int {
        return postsList.size.coerceAtMost(8)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val postImage: RoundedImageView = itemView.findViewById(R.id.postImg)
    }
}