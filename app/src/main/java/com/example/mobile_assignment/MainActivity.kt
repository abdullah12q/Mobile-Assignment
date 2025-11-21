package com.example.mobile_assignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.app.DatePickerDialog
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.mobile_assignment.adapter.ActivityAdapter
import com.example.mobile_assignment.db.AppDatabase
import com.example.mobile_assignment.model.ActivityEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class MainActivity : AppCompatActivity() {
    private lateinit var etActivityName: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnSave: Button
    private lateinit var btnFilter: Button
    private lateinit var btnShowAll: Button
    private lateinit var listView: ListView
    private lateinit var tvTotalForDate: TextView
    private lateinit var calendarView: CalendarView

    private var selectedDate: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etActivityName = findViewById(R.id.etActivityName)
        etDuration = findViewById(R.id.etDuration)
        btnSave = findViewById(R.id.btnPickDate)
        btnFilter = findViewById(R.id.btnFilter)
        btnShowAll = findViewById(R.id.btnShowAll)
        tvTotalForDate = findViewById(R.id.tvTotalForDate)
        listView = findViewById(R.id.listView)
        calendarView = findViewById(R.id.calendarView)

        db = AppDatabase.getInstance(applicationContext)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            selectedDate = dateFormat.format(c.time)
        }

        btnSave.setOnClickListener { saveActivity() }
        btnFilter.setOnClickListener { filterByDate() }
        btnShowAll.setOnClickListener { loadAll() }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = listView.adapter.getItem(position) as ActivityEntity
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra("name", item.activityName)
            intent.putExtra("duration", item.duration)
            intent.putExtra("date", item.date)
            startActivity(intent)
        }

        loadAll()
    }

    private fun saveActivity() {
        val name = etActivityName.text.toString().trim()
        val durationStr = etDuration.text.toString().trim()
        val date = selectedDate

        if (name.isEmpty()) { toast("Enter activity name"); return }
        if (durationStr.isEmpty()) { toast("Enter duration"); return }
        if (date == null) { toast("Pick a date"); return }

        val duration = try { durationStr.toInt() } catch (e: Exception) { toast("Invalid duration"); return }

        val entity = ActivityEntity(activityName = name, duration = duration, date = date)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.activityDao().insert(entity)
            }
            toast("Saved")
            etActivityName.text.clear()
            etDuration.text.clear()
            loadAll()
        }
    }

    private fun filterByDate() {
        val date = selectedDate
        if (date == null) { toast("Pick a date to filter"); return }
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) { db.activityDao().getByDate(date) }
            updateList(items)
            val total = withContext(Dispatchers.IO) { db.activityDao().totalDurationForDate(date) } ?: 0
            tvTotalForDate.text = "Total for $date: $total min"
        }
    }

    private fun loadAll() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) { db.activityDao().getAll() }
            updateList(items)
            tvTotalForDate.text = ""
        }
    }

    private fun updateList(items: List<ActivityEntity>) {
        val adapter = ActivityAdapter(this, items)
        listView.adapter = adapter
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}