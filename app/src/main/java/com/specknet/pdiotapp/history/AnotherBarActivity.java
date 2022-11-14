package com.specknet.pdiotapp.history;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.specknet.pdiotapp.R;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.specknet.pdiotapp.notimportant.DemoBase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class AnotherBarActivity extends DemoBase implements OnSeekBarChangeListener  {
    private BarChart chart_bar;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_another_bar);

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);

//        seekBarX = findViewById(R.id.seekBar1);
//        seekBarX.setOnSeekBarChangeListener(this);
//
//        seekBarY = findViewById(R.id.seekBar2);
//        seekBarY.setOnSeekBarChangeListener(this);

        chart_bar = findViewById(R.id.chart1);

//        chart_bar.getDescription().setEnabled(false);
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        chart_bar.setMaxVisibleValueCount(60);
//
//        // scaling can now only be done on x- and y-axis separately
//        chart_bar.setPinchZoom(false);
//
//        chart_bar.setDrawBarShadow(false);
//        chart_bar.setDrawGridBackground(false);

        XAxis xAxis = chart_bar.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart_bar.getAxisLeft().setDrawGridLines(false);


        chart_bar.setBackgroundColor(Color.WHITE);
        chart_bar.setExtraTopOffset(-30f);
        chart_bar.setExtraBottomOffset(10f);
        chart_bar.setExtraLeftOffset(70f);
        chart_bar.setExtraRightOffset(70f);
        chart_bar.setDrawBarShadow(false);
        chart_bar.setDrawValueAboveBar(true);
        chart_bar.getDescription().setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        chart_bar.setPinchZoom(true);

        chart_bar.setDrawGridBackground(false);

        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(10);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);

        YAxis left = chart_bar.getAxisLeft();
        left.setDrawLabels(false);
        left.setAxisMaximum(100f);
        //left.setAxisMinimum(100f);
        left.setSpaceTop(0f);
        left.setSpaceBottom(0f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(3f);
        chart_bar.getAxisRight().setEnabled(false);
        chart_bar.getLegend().setEnabled(false);

        // setting data
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
        //seekBarX.setProgress(2000);
        //seekBarY.setProgress(100);

        // add a nice and smooth animation
        chart_bar.animateY(1400);

        chart_bar.getLegend().setEnabled(false);
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

        if (chart_bar.getData() != null &&
                chart_bar.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart_bar.getData().getDataSetByIndex(0);
            set.setValues(values);
            chart_bar.getData().notifyDataChanged();
            chart_bar.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new Formatter());
            data.setBarWidth(0.8f);

            chart_bar.setData(data);
            chart_bar.invalidate();
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < seekBarX.getProgress(); i++) {
            float multi = (seekBarY.getProgress() + 1);
            float val = (float) (Math.random() * multi) + multi / 3;
            values.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (chart_bar.getData() != null &&
                chart_bar.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart_bar.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart_bar.getData().notifyDataChanged();
            chart_bar.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart_bar.setData(data);
            chart_bar.setFitBars(true);
        }

        chart_bar.invalidate();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bar, menu);
//        menu.removeItem(R.id.actionToggleIcons);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.viewGithub: {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/AnotherBarActivity.java"));
//                startActivity(i);
//                break;
//            }
//            case R.id.actionToggleValues: {
//
//                for (IDataSet set : chart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//
//                chart.invalidate();
//                break;
//            }
//            /*
//            case R.id.actionToggleIcons: { break; }
//             */
//            case R.id.actionToggleHighlight: {
//
//                if(chart.getData() != null) {
//                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
//                    chart.invalidate();
//                }
//                break;
//            }
//            case R.id.actionTogglePinch: {
//                if (chart.isPinchZoomEnabled())
//                    chart.setPinchZoom(false);
//                else
//                    chart.setPinchZoom(true);
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.actionToggleAutoScaleMinMax: {
//                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
//                chart.notifyDataSetChanged();
//                break;
//            }
//            case R.id.actionToggleBarBorders: {
//                for (IBarDataSet set : chart.getData().getDataSets())
//                    ((BarDataSet)set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.animateX: {
//                chart.animateX(2000);
//                break;
//            }
//            case R.id.animateY: {
//                chart.animateY(2000);
//                break;
//            }
//            case R.id.animateXY: {
//
//                chart.animateXY(2000, 2000);
//                break;
//            }
//            case R.id.actionSave: {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    saveToGallery();
//                } else {
//                    requestStoragePermission(chart);
//                }
//                break;
//            }
//        }
//        return true;
//    }
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
    @Override
    protected void saveToGallery() {
        saveToGallery(chart_bar, "AnotherBarActivity");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}