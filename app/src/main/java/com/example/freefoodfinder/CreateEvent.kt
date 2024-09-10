package com.example.freefoodfinder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.freefoodfinder.models.Event
import com.example.freefoodfinder.network.ApiClient
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateEvent : ComponentActivity() {

    private lateinit var apiClient: ApiClient
    private val calendar = Calendar.getInstance()
    private lateinit var textViewDate: TextView
    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private lateinit var date: Date
    private lateinit var startTime: Time
    private lateinit var endTime: Time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        apiClient = ApiClient()

        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = findViewById<EditText>(R.id.editTextDescription)
        val locationEditText = findViewById<EditText>(R.id.editTextLocation)
        val textViewDate = findViewById<TextView>(R.id.textViewDate)
        val imageButtonDate = findViewById<ImageButton>(R.id.imageButtonDate)
        val textViewStartTime = findViewById<TextView>(R.id.textViewStartTime)
        val imageButtonStartTime = findViewById<ImageButton>(R.id.imageButtonStartTime)
        val textViewEndTime = findViewById<TextView>(R.id.textViewEndTime)
        val imageButtonEndTime = findViewById<ImageButton>(R.id.imageButtonEndTime)
        val createButton = findViewById<Button>(R.id.buttonCreate)

        imageButtonDate.setOnClickListener {
            showDatePicker()
        }
        imageButtonStartTime.setOnClickListener { showStartTimePicker() }
        imageButtonEndTime.setOnClickListener { showEndTimePicker() }

        createButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val location = locationEditText.text.toString()
            val dateString = textViewDate.text.toString()
            val startTimeString = textViewStartTime.text.toString()
            val endTimeString = textViewEndTime.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                var event = Event(
                    id = null,
                    title = title,
                    description = description,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    location = location,
                    latitude = 0.00,
                    longitude = 0.00,
                    image = null,
                )
                createEvent(event)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createEvent(event: Event) {
        apiClient.getApiService(this).createEvent(event).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    val createdItem = response.body()
                    // Pass the item details to the ShowItemActivity
                    val intent = Intent(this@CreateEvent, ViewEvent::class.java).apply {
                        putExtra("ITEM_ID", createdItem?.id)
                        putExtra("ITEM_NAME", createdItem?.title)
                        putExtra("ITEM_DESCRIPTION", createdItem?.description)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CreateEvent, "Failed to create item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Toast.makeText(this@CreateEvent, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePicker() {
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this, {
                  DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(year, monthOfYear, dayOfMonth)
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)
                        textViewDate.text = formattedDate
                        date = Date(year, monthOfYear, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

    private fun showStartTimePicker() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            textStartTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            startTime = Time(hour, minute, 0)
        }
        TimePickerDialog(
            this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }

    private fun showEndTimePicker() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            textEndTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            endTime = Time(hour, minute, 0)
        }
        TimePickerDialog(
            this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }
}
