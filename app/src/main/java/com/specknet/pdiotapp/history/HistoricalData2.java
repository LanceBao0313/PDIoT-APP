package com.specknet.pdiotapp.history;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.specknet.pdiotapp.MainActivity;
import com.specknet.pdiotapp.MainActivity2;
import com.specknet.pdiotapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.specknet.pdiotapp.notimportant.DemoBase;

import android.graphics.Typeface;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.specknet.pdiotapp.pose.Pose;
import com.specknet.pdiotapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HistoricalData2 extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    private SwipeRefreshLayout swipeContainer;
    public List<String> serverData;
    public ArrayList<Integer> color_list_bar;
    public ArrayList<Integer> color_list_pie;

    int colour1 = ColorTemplate.VORDIPLOM_COLORS[0];
    int colour2 = ColorTemplate.JOYFUL_COLORS[0];
    int colour3 = ColorTemplate.COLORFUL_COLORS[0];
    int colour4 = ColorTemplate.LIBERTY_COLORS[0];
    int colour5 = ColorTemplate.PASTEL_COLORS[0];
    int colour6 = ColorTemplate.JOYFUL_COLORS[1];
    int colour7 = ColorTemplate.LIBERTY_COLORS[1];

    public String lastType = "minute";

    private BarChart chart_bar;
    private PieChart chart_pie;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    private Context context;

    private Typeface tf;

    Button hourButton;
    Button dayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_historical_data);

        Button hourButton = findViewById(R.id.hour);
        Button dayButton = findViewById(R.id.day);
        context = getApplicationContext();


        chart_bar = findViewById(R.id.chart2);
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

        XAxis xAxis = chart_bar.getXAxis();
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


        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setTitle("Activity History");

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);

        seekBarX = findViewById(R.id.seekBar1);
        seekBarY = findViewById(R.id.seekBar2);

        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);

        chart_pie = findViewById(R.id.chart1);
        chart_pie.setUsePercentValues(true);
        chart_pie.getDescription().setEnabled(false);
        chart_pie.setExtraOffsets(5, 10, 5, 5);

        chart_pie.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        chart_pie.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        chart_pie.setCenterText(generateCenterSpannableText());

        chart_pie.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        chart_pie.setDrawHoleEnabled(true);
        chart_pie.setHoleColor(Color.WHITE);

        chart_pie.setTransparentCircleColor(Color.WHITE);
        chart_pie.setTransparentCircleAlpha(110);

        chart_pie.setHoleRadius(58f);
        chart_pie.setTransparentCircleRadius(61f);

        chart_pie.setDrawCenterText(true);

        chart_pie.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart_pie.setRotationEnabled(true);
        chart_pie.setHighlightPerTapEnabled(true);

        // chart_pie.setUnit(" €");
        // chart_pie.setDrawUnitsInChart(true);

        // add a selection listener
        chart_pie.setOnChartValueSelectedListener(this);

        seekBarX.setProgress(7);
        seekBarY.setProgress(100);

        chart_pie.animateY(1400, Easing.EaseInOutQuad);
        chart_bar.animateX(1400);
        // chart_pie.spin(2000, 0, 360);

        Legend l = chart_pie.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                chart_pie.animateXY(1400, 1400);
                chart_bar.animateX(1400);
                if(lastType.equals("hour")){
                    getHourData(chart_bar);
                }else if(lastType.equals("minute")){
                    getMinData(chart_pie);
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void getHourData(View v)
    {
        lastType = "hour";
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);

        JSONObject json = new JSONObject();

        //String feedback = "false";
        try {
            json.put("student_id", sharedPreferences.getString(Constants.USER_ID, ""));
            json.put("get_type", "hour");

            makeGet(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMinData(View v)
    {
        lastType = "minute";
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);

        JSONObject json = new JSONObject();

        //String feedback = "false";
        try {
            json.put("student_id", sharedPreferences.getString(Constants.USER_ID, ""));
            json.put("get_type", "miniute");

            makeGet(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeGet(JSONObject json) {
        Log.i("history", json.toString());
        String url = "http://34.89.117.73:5000/history";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        serverData = stringToList(response.toString());
                        Log.i("history", serverData.toString());
                        final List<HistoricalData2.Data> data = new ArrayList<>();
                        color_list_bar = new ArrayList<>();
                        for(int i = 0;i< 60;i++){
                            if(serverData.get(i) == null || serverData.get(i).equals("") || serverData.get(i).equals("\"\"")){
                                color_list_bar.add(colour1);
                                data.add(new HistoricalData2.Data((float) i, 0f, String.valueOf(59-i)));
                            } else {
                                Log.i("history", serverData.get(i));
                                String type = serverData.get(i).replace("\"","");
                                Log.i("history", String.valueOf(serverData.get(i).equals("\"general_movement\"")));
                                switch(type) {
                                    case "sitting":
                                    case "sitting_bent_backward":
                                    case "sitting_bent_forward":
                                    case "desk_work":
                                        color_list_bar.add(colour1);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "ascending_stairs":
                                    case "descending_stairs":
                                        color_list_bar.add(colour2);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "lying_down_left":
                                    case "lying_down_right":
                                    case "lying_down_on_back":
                                    case "lying_down_on_stomach":
                                        color_list_bar.add(colour3);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "walking":
                                        color_list_bar.add(colour4);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "running":
                                        color_list_bar.add(colour5);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "standing":
                                        color_list_bar.add(colour6);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    case "general_movement":
                                        Log.i("history", serverData.get(i));
                                        color_list_bar.add(colour7);
                                        data.add(new HistoricalData2.Data((float) i, 100f, String.valueOf(59-i)));
                                        break;
                                    default:
                                        Log.i("history", "missed！！");
                                        // code block
                                }
                            }

                        }
                        XAxis xAxis = chart_bar.getXAxis();
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return data.get(Math.min(Math.max((int) value, 0), data.size()-1)).xAxisValue;
                            }

                        });

                        setDataBar(data);

                        final List<HistoricalData2.Data> data_pie = new ArrayList<>();
                        color_list_pie = new ArrayList<>();
                        for(int i = 60;i< 67;i++){
                            Log.i("history", serverData.get(i));
                            String type = serverData.get(i).replace("\"","");
                            List<String> sector = Arrays.asList(type.split(":"));
                            String name = sector.get(0);
                            float percent = Float.parseFloat(sector.get(1));
                            if (percent != 0){
                                switch(name) {
                                    case "Lying":
                                        color_list_pie.add(colour3);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Lying"));
                                        break;
                                    case "Movement":
                                        color_list_pie.add(colour7);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Movement"));
                                        break;
                                    case "Running":
                                        color_list_pie.add(colour5);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Running"));
                                        break;
                                    case "Sitting":
                                        color_list_pie.add(colour1);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Sitting"));
                                        break;
                                    case "Standing":
                                        color_list_pie.add(colour6);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Standing"));
                                        break;
                                    case "Up\\/Down stairs":
                                        color_list_pie.add(colour2);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Up/Down stairs"));
                                        break;
                                    case "Walking":
                                        color_list_pie.add(colour4);
                                        data_pie.add(new HistoricalData2.Data(percent, 0f, "Walking"));
                                        break;
                                    default:
                                        Log.i("history", "missed！！");
                                        // code block
                                }
                            }


                        }

                        setDataPie(data_pie);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("history", error.toString());
                        Toast.makeText(context, "Server error!", Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public List<String> stringToList(String string){
        String replace = string.replace("[","");
        String replace1 = replace.replace("]","");
        String replace2 = replace1.replace("{","");
        String replace3 = replace2.replace("}","");
        String replace4 = replace3.replace("\"data\":","");
        String replace5 = replace4.replace("\"percentage\":","");
        List<String> myList = new ArrayList<String>(Arrays.asList(replace5.split(",")));
        return myList;
    }


    private void setDataBar(List<HistoricalData2.Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<>();
        //List<Integer> colors = new ArrayList<>();


        for (int i = 0; i < dataList.size(); i++) {

            HistoricalData2.Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // specific colors
           //colors.add(color_list.get(i % 7));

        }

        BarDataSet set;

//        if (chart_bar.getData() != null &&
//                chart_bar.getData().getDataSetCount() > 0) {
//            set = (BarDataSet) chart_bar.getData().getDataSetByIndex(0);
//            set.setValues(values);
//            chart_bar.getData().notifyDataChanged();
//            chart_bar.notifyDataSetChanged();
//        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(color_list_bar);
            set.setValueTextColors(color_list_bar);

            BarData data = new BarData(set);
            data.setValueTextSize(0f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new HistoricalData2.Formatter());
            data.setBarWidth(0.8f);

            chart_bar.setData(data);
            chart_bar.invalidate();
        //}
        chart_bar.animateX(1400);
    }

    private void setDataPie(List<HistoricalData2.Data> dataList) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < dataList.size(); i++) {
            entries.add(new PieEntry(dataList.get(i).xValue, dataList.get(i).xAxisValue));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

//        ArrayList<Integer> colors = new ArrayList<>();
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(color_list_pie);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        chart_pie.setData(data);

        // undo all highlights
        chart_pie.highlightValues(null);

        chart_pie.invalidate();
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

    //@Override
    //public void saveToGallery() { /* Intentionally left empty */ }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        //setDataPie(seekBarX.getProgress(), seekBarY.getProgress());
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart_pie, "PiePolylineChartActivity");
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Activity History\ndeveloped by GroupT");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 16, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 16, s.length() - 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 16, s.length() - 6, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 16, s.length() - 6, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 16, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 6, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


}