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

class EventsAdapter(
    var context: Context,
    private val itemList: List<Event>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var title = view.findViewById<TextView>(R.id.LV_title);
        var description = view.findViewById<TextView>(R.id.LV_description)
        var location = view.findViewById<TextView>(R.id.LV_location);
        var date = view.findViewById<TextView>(R.id.LV_date)
        var start_time = view.findViewById<TextView>(R.id.LV_start_time)
        var end_time = view.findViewById<TextView>(R.id.LV_end_time)
        var image = view.findViewById<ImageView>(R.id.LV_image);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return EventViewHolder(view);
    }

    override fun getItemCount(): Int {
        return itemList.count();
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = itemList[position]
        holder.title.text = event.title;
        holder.location.text = event.location;
        holder.description.text = event.description
        holder.date.text = event.date
        holder.start_time.text = event.startTime
        holder.end_time.text = event.endTime
        Glide.with(context).load(event.image).into(holder.image);

        holder.itemView.setOnClickListener {
            event.id?.let { onItemClick(it) } ?: run { print("Id is null") }
        }
    }
}