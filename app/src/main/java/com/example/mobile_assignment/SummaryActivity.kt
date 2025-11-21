package com.example.mobile_assignment

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        val duration = intent.getIntExtra("duration", 0)
        val date = intent.getStringExtra("date") ?: ""

        findViewById<TextView>(R.id.tvSummaryDuration).text = "Duration: $duration min"
        findViewById<TextView>(R.id.tvSummaryDate).text = "Date: $date"
    }
}
