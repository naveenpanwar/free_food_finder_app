package com.example.freefoodfinder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freefoodfinder.models.Event

class EventsAdapter(var context: Context, var list: List<Event>) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var title = view.findViewById<TextView>(R.id.LV_title);
        var location = view.findViewById<TextView>(R.id.LV_location);
        var image = view.findViewById<ImageView>(R.id.LV_image);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return list.count();
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].title;
        holder.location.text = list[position].location;
        Glide.with(context).load(list[position].image).into(holder.image);
    }
}