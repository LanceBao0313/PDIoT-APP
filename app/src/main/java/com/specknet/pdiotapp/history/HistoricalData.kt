package com.specknet.pdiotapp.history

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specknet.pdiotapp.R
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel




class HistoricalData : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_data)

        val intent_pie = Intent(this, PieChartActivity::class.java)
        val intent_bar = Intent(this, BarChartActivity::class.java)
        startActivity(intent_pie)
        startActivity(intent_bar)
    }

}