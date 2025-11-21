package com.example.mobile_assignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mobile_assignment.R
import com.example.mobile_assignment.model.ActivityEntity

class ActivityAdapter(context: Context, private val items: List<ActivityEntity>)
    : ArrayAdapter<ActivityEntity>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_main, parent, false)
        val item = items[position]
        v.findViewById<TextView>(R.id.tvName).text = item.activityName
        v.findViewById<TextView>(R.id.tvDetails).text = "Duration: ${item.duration} min • Date: ${item.date}"
        return v
    }
}
