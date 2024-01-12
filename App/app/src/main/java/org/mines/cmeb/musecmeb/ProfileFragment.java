package org.mines.cmeb.musecmeb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements Session.OnSessionEndListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LineChart indexesChart;
    private TextView valueTextView;
    private BarChart sessionActivity;
    int light_blue;
    int dark_blue;
    int pink;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*
        * This fragment displays the user's profile.
        * It displays a chart of the relaxation times of the user's past sessions, and a chart of the
        * number of sessions per day over the last week.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        indexesChart = view.findViewById(R.id.indexesChart);
        valueTextView = view.findViewById(R.id.valueTextView);
        sessionActivity = view.findViewById(R.id.sessionActivity);

        Context context = getContext();
        light_blue = ContextCompat.getColor(context, R.color.our_light_blue);
        pink = ContextCompat.getColor(context, R.color.our_pink);
        dark_blue = ContextCompat.getColor(context, R.color.our_dark_blue);

        // Set the OnSessionEndListener in the Session activity
        Session.setOnSessionEndListener(this);

        // Retrieve data from past sessions
        lineChart();
        barChart();

        return view;
    }

    /*
        * This method is called when a session ends.
        * It refreshes the charts.
     */
    private void lineChart(){
        // get float[] from database with all relaxation times
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        float[] relaxationTimes = dbHelper.getAllRelaxationTimes();

        // Line Chart Definitions and settings
        LineDataSet lineDataSet1 = new LineDataSet(dataValues(relaxationTimes), "Relaxation Times (min)");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        // Don't show values on the chart
        lineDataSet1.setDrawValues(false);
        // When the chart is clicked, show the value of the point
        lineDataSet1.setDrawHighlightIndicators(true);
        lineDataSet1.setHighlightEnabled(true);
        lineDataSet1.setHighLightColor(pink);
        lineDataSet1.setCircleHoleColor(pink);
        lineDataSet1.setCircleColor(pink);
        lineDataSet1.setCircleRadius(3);


        indexesChart.getAxisRight().setEnabled(false);
        indexesChart.getDescription().setEnabled(false);
        indexesChart.setBackgroundColor(Color.TRANSPARENT);
        indexesChart.setNoDataText("No data was recorded in this session");
        indexesChart.setNoDataTextColor(pink);
        indexesChart.setKeepPositionOnRotation(true);
        indexesChart.setTouchEnabled(true);
        indexesChart.setDrawGridBackground(false);

        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(pink);
        lineDataSet1.setCircleColor(pink);
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setValueTextColor(pink);

        // Position the X-axis at the bottom and change colors
        XAxis xAxis = indexesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(pink);
        xAxis.setAxisLineColor(pink);
        xAxis.setTextColor(pink);
        xAxis.setDrawGridLines(false);

        // Get y Axis and change colors
        YAxis yAxis = indexesChart.getAxisLeft();
        yAxis.setGridColor(pink);
        yAxis.setAxisLineColor(pink);
        yAxis.setTextColor(pink);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);

        indexesChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                // Display information when a point is clicked
                float xValue = h.getX();
                float yValue = h.getY();

                MPPointD pos = indexesChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(xValue, yValue);
                // Convert the position to the position in the screen in pixels

                valueTextView.setX((float) pos.x);
                valueTextView.setY((float) (pos.y) + 225);


                String text = "Session " + (int) xValue + "\n " + String.format("%.2f", yValue) + " min";
                valueTextView.setText(text);
                // Make text smaller
                valueTextView.setTextSize(10);
                valueTextView.setTextColor(dark_blue);
                valueTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                // Hide the information when no point is clicked
                valueTextView.setVisibility(View.INVISIBLE);
            }
        });

        // Plot the chart
        LineData data = new LineData(dataSets);
        indexesChart.setData(data);
        indexesChart.invalidate();
    }

    /*
        * This creates a bar chart of the number of sessions per day over the last week.
     */
    private void barChart(){

        // Get the number of sessions per day on the last week
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        int[] sessionCounts = dbHelper.getSessionCountsLast7Days();

        // Line Chart Definitions and settings
        // Make the value in the x axis the days of the week back from today
        String[] days = new String[7];
        Date today = new Date();
        for (int i = 0; i < 7; i++) {
            days[i] = today.toString().substring(0, 3);
            today.setTime(today.getTime() - 86400000);
        }
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(days);
        sessionActivity.getXAxis().setValueFormatter(formatter);

        BarDataSet barDataSet1 = new BarDataSet(dataValues(sessionCounts), "Sessions last week");
        BarData barData = new BarData(barDataSet1);
        barDataSet1.setValueFormatter(new IntegerValueFormatter());
        sessionActivity.setData(barData);

        sessionActivity.getAxisRight().setEnabled(false);
        sessionActivity.getDescription().setEnabled(false);
        sessionActivity.setBackgroundColor(Color.TRANSPARENT);
        sessionActivity.setNoDataText("No data was recorded in this session");
        sessionActivity.setNoDataTextColor(pink);
        sessionActivity.setKeepPositionOnRotation(true);
        sessionActivity.setTouchEnabled(true);
        sessionActivity.setDrawGridBackground(false);

        barDataSet1.setColor(pink);
        barDataSet1.setValueTextSize(10);
        barDataSet1.setValueTextColor(dark_blue);


        // Position the X-axis at the bottom and change colors
        XAxis xAxis = sessionActivity.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(pink);
        xAxis.setAxisLineColor(pink);
        xAxis.setTextColor(pink);
        xAxis.setDrawGridLines(false);

        // Get y Axis and change colors
        YAxis yAxis = sessionActivity.getAxisLeft();
        yAxis.setGridColor(pink);
        yAxis.setAxisLineColor(pink);
        yAxis.setTextColor(pink);
        yAxis.setDrawGridLines(false);

        // Plot the chart
        BarData data = new BarData();
        data.addDataSet(barDataSet1);
        sessionActivity.setData(data);
        sessionActivity.invalidate();

    }

    /*
    * This method converts an array of floats to an ArrayList of Entries.
    * It is used to plot the relaxation times over time.
    * @param indexes: the array of floats to convert
    * @return an ArrayList of Entries
     */
    private ArrayList<Entry> dataValues(float[] indexes)
    {
        ArrayList<Entry> dataVal = new ArrayList<Entry>();
        for (int i = 0; i < indexes.length; i++) {
            dataVal.add(new Entry(i, indexes[i]));
        }
        return dataVal;
    }

    /*
    * This method converts an array of ints to an ArrayList of BarEntries.
    * It is used to plot the number of sessions per day over the last week.
    * @param indexes: the array of ints to convert
    * @return an ArrayList of BarEntries
     */
    private ArrayList<BarEntry> dataValues(int[] indexes)
    {
        ArrayList<BarEntry> dataVal = new ArrayList<BarEntry>();
        for (int i = 0; i < indexes.length; i++) {
            dataVal.add(new BarEntry(i, indexes[i]));
        }
        return dataVal;
    }

    /*
        * This method is called when a session ends.
        * It refreshes the charts.
     */
    @Override
    public void onSessionEnd() {
        // Refresh the session list when a session ends
        lineChart();
        barChart();
    }

    /*
        * This class is used to format the values on the y axis of the bar chart.
     */
    private static class IntegerValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            // if value is zero, return an empty string
            if (value == 0f) {
                return "";
            }
            return String.valueOf((int) value);
        }
    }
}