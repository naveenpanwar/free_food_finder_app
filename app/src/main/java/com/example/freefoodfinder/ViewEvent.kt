package com.example.freefoodfinder

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.freefoodfinder.models.Event
import com.example.freefoodfinder.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewEvent : AppCompatActivity() {
    lateinit var apiClient: ApiClient

    lateinit var title: TextView
    lateinit var description: TextView
    lateinit var location: TextView
    lateinit var date: TextView
    lateinit var startTime: TextView
    lateinit var endTime: TextView
    lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = findViewById(R.id.LV_title);
        description = findViewById(R.id.LV_description)
        location = findViewById(R.id.LV_location);
        date = findViewById(R.id.LV_date)
        startTime = findViewById(R.id.LV_start_time)
        endTime = findViewById(R.id.LV_end_time)
        image = findViewById(R.id.LV_image);
        apiClient = ApiClient()

        val itemId = intent.getIntExtra("ITEM_ID", -1)
        if (itemId != -1) {
            fetchEvent(itemId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun fetchEvent(eventId: Int) {
        apiClient.getApiService(this).getEvent(eventId)
            .enqueue(object : Callback<Event> {
                override fun onFailure(event: Call<Event>, t: Throwable) {
                    Log.d("Failure", t.toString())
                    // Error fetching posts
                }

                override fun onResponse(event: Call<Event>, response: Response<Event>) {
                    var data = response.body()
                    data?.let {
                        title.text = it.title
                        description.text = it.description
                        location.text = it.location
                        date.text = it.date
                        startTime.text = it.startTime
                        endTime.text = it.endTime
                        Glide.with(this@ViewEvent).load(it.image).into(image);
                    } ?: run {
                        Log.d("Data", "Is NULL" )
                    }
                }
            })
    }
}
