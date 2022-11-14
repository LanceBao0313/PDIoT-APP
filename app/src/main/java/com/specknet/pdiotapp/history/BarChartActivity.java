package com.specknet.pdiotapp.history;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.specknet.pdiotapp.R;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.formatter.IAxisValueFormatter;
//import com.github.mikephil.charting.formatter.IValueFormatter;
//import com.github.mikephil.charting.utils.ViewPortHandler;
import com.specknet.pdiotapp.notimportant.DemoBase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



public class BarChartActivity extends DemoBase {
    private BarChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_historical_data);

        setTitle("BarChartPositiveNegative");

        chart = findViewById(R.id.chart2);
        chart.setBackgroundColor(Color.WHITE);
        chart.setExtraTopOffset(-30f);
        chart.setExtraBottomOffset(10f);
        chart.setExtraLeftOffset(70f);
        chart.setExtraRightOffset(70f);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(10);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);

        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(3f);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        final List<Data> data = new ArrayList<>();

        for(int i = 0;i< 100;i++){
            data.add(new Data((float) i, 100f, String.valueOf(i)));
        }

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return data.get(Math.min(Math.max((int) value, 0), data.size()-1)).xAxisValue;
            }


        });

        setData(data);


    }

    private void setData(List<Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<>();
        //List<Integer> colors = new ArrayList<>();

        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Integer> color_list = new ArrayList<>();
        int colour1 = ColorTemplate.VORDIPLOM_COLORS[0];
        int colour2 = ColorTemplate.JOYFUL_COLORS[0];
        int colour3 = ColorTemplate.COLORFUL_COLORS[0];
        int colour4 = ColorTemplate.LIBERTY_COLORS[0];
        int colour5 = ColorTemplate.PASTEL_COLORS[0];
        int colour6 = ColorTemplate.JOYFUL_COLORS[1];
        int colour7 = ColorTemplate.LIBERTY_COLORS[1];
        color_list.add(colour1);
        color_list.add(colour2);
        color_list.add(colour3);
        color_list.add(colour4);
        color_list.add(colour5);
        color_list.add(colour6);
        color_list.add(colour7);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // specific colors
            colors.add(color_list.get(i % 7));

        }

        BarDataSet set;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new Formatter());
            data.setBarWidth(0.8f);

            chart.setData(data);
            chart.invalidate();
        }
    }

    /**
     * Demo class representing data.
     */
    private class Data {

        final String xAxisValue;
        final float yValue;
        final float xValue;

        Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    private class Formatter extends ValueFormatter
    {

        private final DecimalFormat mFormat;

        Formatter() {
            mFormat = new DecimalFormat("######.0");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.only_github, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.viewGithub: {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartPositiveNegative.java"));
//                startActivity(i);
//                break;
//            }
//        }
//
//        return true;
//    }

    @Override
    public void saveToGallery() { /* Intentionally left empty */ }
}