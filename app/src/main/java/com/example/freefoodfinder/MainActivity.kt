package com.example.freefoodfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefoodfinder.models.Event
import com.example.freefoodfinder.network.ApiClient
import com.example.freefoodfinder.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var floatingAddEventButton: FloatingActionButton
    lateinit var recyclerViewAdapter: EventsAdapter
    lateinit var apiClient: ApiClient
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        floatingAddEventButton = findViewById(R.id.fab)
        floatingAddEventButton.setOnClickListener {
            startActivity(Intent(this, CreateEvent::class.java))
        }

        sessionManager = SessionManager(this)

        if(!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        recyclerView = findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
        apiClient = ApiClient()
        fetchPosts()
    }

    private fun fetchPosts() {
        apiClient.getApiService(this).getEvents()
            .enqueue(object : Callback<List<Event>> {
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    Log.d("Failure", t.toString())
                    // Error fetching posts
                }

                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    var data = response.body()
                    if (data != null) {
                        recyclerViewAdapter = EventsAdapter(baseContext, data)
                        recyclerView.adapter = recyclerViewAdapter
                        Log.d("Data", data.toString() )
                    } else {
                        Log.d("Data", "Is NULL" )
                    }
                }
            })
    }
}
