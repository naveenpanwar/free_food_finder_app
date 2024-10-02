package com.example.freefoodfinder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.freefoodfinder.models.Event
import com.example.freefoodfinder.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateEvent : AppCompatActivity() {

    private lateinit var apiClient: ApiClient
    private val calendar = Calendar.getInstance()
    private lateinit var textViewDate: TextView
    private lateinit var textViewStartTime: TextView
    private lateinit var textViewEndTime: TextView
    private lateinit var date: String
    private lateinit var startTime: String
    private lateinit var endTime: String
    private lateinit var chosenImage: File
    private lateinit var uploadImageButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiClient = ApiClient()

        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = findViewById<EditText>(R.id.editTextDescription)
        val locationEditText = findViewById<EditText>(R.id.editTextLocation)
        textViewDate = findViewById<TextView>(R.id.textViewDate)
        val imageButtonDate = findViewById<ImageButton>(R.id.imageButtonDate)
        textViewStartTime = findViewById<TextView>(R.id.textViewStartTime)
        val imageButtonStartTime = findViewById<ImageButton>(R.id.imageButtonStartTime)
        textViewEndTime = findViewById<TextView>(R.id.textViewEndTime)
        val imageButtonEndTime = findViewById<ImageButton>(R.id.imageButtonEndTime)
        val createButton = findViewById<Button>(R.id.buttonCreate)
        uploadImageButton = findViewById<Button>(R.id.buttonUploadImage)

        imageButtonDate.setOnClickListener { showDatePicker() }
        imageButtonStartTime.setOnClickListener { showStartTimePicker() }
        imageButtonEndTime.setOnClickListener { showEndTimePicker() }

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                chosenImage = uriToFile(this, uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        // Launch the photo picker and let the user choose only images.
        uploadImageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        createButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val location = locationEditText.text.toString()

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
                    image = chosenImage,
                )
                createEvent(event, chosenImage)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @Throws(IOException::class)
    fun uriToFile(context: Context, uri: Uri): File {
        // Create a temporary file in the app's cache directory
        val file = File(context.cacheDir, "temp_image.jpg")

        // Use contentResolver to open an InputStream for the Uri
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                // Copy data from the InputStream to the FileOutputStream
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        } ?: throw IOException("Failed to open input stream from URI")

        return file // Return the newly created file
    }

    // Handle the "Up" button click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            // Navigate back to the parent activity
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), value)
    }

    fun prepareFilePart(partName: String, file: File?): MultipartBody.Part? {
        file?.let {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
        return null
    }

    private fun createEvent(event: Event, imageFile: File) {
        Log.d("DEBUG", event.toString())
        // Prepare all parts of the Event object
        val title = createPartFromString(event.title)
        val description = createPartFromString(event.description)
        val date = createPartFromString(event.date)
        val startTime = createPartFromString(event.startTime)
        val endTime = createPartFromString(event.endTime)
        val location = createPartFromString(event.location)
        val latitude = createPartFromString(event.latitude.toString())
        val longitude = createPartFromString(event.longitude.toString())

        // Prepare the image file
        val imagePart = prepareFilePart("image", imageFile)

        // Make the network request
        val call = apiClient.getApiService(this).createEvent(
            title, description, date, startTime, endTime,
            location, latitude, longitude, imagePart
        )
        call.enqueue(object : Callback<Event> {
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
                   response.errorBody()?.let { errorBody ->
                        try {
                            var errorJson = errorBody.string()
                            Log.e("ERROR", errorJson)
                        } catch (e: IOException) {
                            Log.e("ERROR", e.localizedMessage)
                        }
                    }
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
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)
                        textViewDate.text = formattedDate
                        date = formattedDate
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
            var timeString = SimpleDateFormat("HH:mm").format(cal.time)
            textViewStartTime.text = timeString
            startTime = timeString
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
            var timeString = SimpleDateFormat("HH:mm").format(cal.time)
            textViewEndTime.text = timeString
            endTime = timeString
        }
        TimePickerDialog(
            this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }
}
