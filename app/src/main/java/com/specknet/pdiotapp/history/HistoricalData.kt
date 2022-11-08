package com.specknet.pdiotapp.history

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specknet.pdiotapp.R
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel



class HistoricalData : AppCompatActivity() {
    // Create the object of TextView
    // and PieChart class
    lateinit var tvR: TextView
    lateinit var tvPython: TextView // Create the object of TextView
    // and PieChart class
    lateinit var tvCPP: TextView// Create the object of TextView
    // and PieChart class
    lateinit var tvJava: TextView // Create the object of TextView
    // and PieChart class
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_data)

        // Link those objects with their
        // respective id's that
        // we have given in .XML file
        tvR = findViewById(R.id.tvR)
        tvPython = findViewById<TextView>(R.id.tvPython)
        tvCPP = findViewById(R.id.tvCPP)
        tvJava = findViewById<TextView>(R.id.tvJava)
        pieChart = findViewById(R.id.piechart)

        // Creating a method setData()
        // to set the text in text view and pie chart
        setData()
    }

    private fun setData() {

        // Set the percentage of language used
        tvR.text = Integer.toString(40)
        tvPython.setText(Integer.toString(30))
        tvCPP.text = Integer.toString(5)
        tvJava.setText(Integer.toString(25))

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
            PieModel(
                "R", tvR.text.toString().toInt().toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Python", tvPython.getText().toString().toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "C++", tvCPP.text.toString().toInt().toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Java", tvJava.getText().toString().toFloat(),
                Color.parseColor("#29B6F6")
            )
        )

        // To animate the pie chart
        pieChart.startAnimation()
    }



}