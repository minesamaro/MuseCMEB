package org.mines.cmeb.musecmeb;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LineChart indexesChart;
    private BarChart sessionActivity;

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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        indexesChart = view.findViewById(R.id.indexesChart);
        sessionActivity = view.findViewById(R.id.sessionActivity);

        // Retrieve data from past sessions
        lineChart();

        return view;
    }

    private void lineChart(){
        // get float[] from database with all relaxation times
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        float[] relaxationTimes = dbHelper.getAllRelaxationTimes();

        // Line Chart Definitions and settings
        LineDataSet lineDataSet1 = new LineDataSet(dataValues(relaxationTimes), "Relaxation Times");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        indexesChart.getAxisRight().setEnabled(false);
        indexesChart.getDescription().setEnabled(false);
        indexesChart.setBackgroundColor(Color.TRANSPARENT);
        indexesChart.setNoDataText("No data was recorded in this session");
        indexesChart.setNoDataTextColor(Color.parseColor("#edf6f9"));
        indexesChart.setKeepPositionOnRotation(true);
        indexesChart.setTouchEnabled(true);
        indexesChart.setDrawGridBackground(false);

        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(Color.parseColor("#edf6f9"));
        lineDataSet1.setCircleColor(Color.parseColor("#edf6f9"));
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setValueTextColor(Color.parseColor("#edf6f9"));

        // Position the X-axis at the bottom and change colors
        XAxis xAxis = indexesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.parseColor("#edf6f9"));

        // Get y Axis and change colors
        YAxis yAxis = indexesChart.getAxisLeft();
        yAxis.setGridColor(Color.WHITE);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setTextColor(Color.parseColor("#edf6f9"));

        // Plot the chart
        LineData data = new LineData(dataSets);
        indexesChart.setData(data);
        indexesChart.invalidate();
    }

    private void barChart(){

        // Get the number of sessions per day on the last week
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        int[] sessionCounts = dbHelper.getSessionCountsLast7Days();

        // Line Chart Definitions and settings
        BarDataSet barDataSet1 = new BarDataSet(dataValues(sessionCounts), "Sessions per day");
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        sessionActivity.getAxisRight().setEnabled(false);
        sessionActivity.getDescription().setEnabled(false);
        sessionActivity.setBackgroundColor(Color.TRANSPARENT);
        sessionActivity.setNoDataText("No data was recorded in this session");
        sessionActivity.setNoDataTextColor(Color.parseColor("#edf6f9"));
        sessionActivity.setKeepPositionOnRotation(true);
        sessionActivity.setTouchEnabled(true);
        sessionActivity.setDrawGridBackground(false);

        barDataSet1.setColor(Color.parseColor("#edf6f9"));
        barDataSet1.setValueTextSize(10);
        barDataSet1.setValueTextColor(Color.parseColor("#edf6f9"));

        // Position the X-axis at the bottom and change colors
        XAxis xAxis = sessionActivity.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.parseColor("#edf6f9"));

        // Get y Axis and change colors
        YAxis yAxis = indexesChart.getAxisLeft();
        yAxis.setGridColor(Color.WHITE);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setTextColor(Color.parseColor("#edf6f9"));

        // Plot the chart
        BarData data = new BarData();
        data.addDataSet(barDataSet1);
        sessionActivity.setData(data);
        sessionActivity.invalidate();

    }
    private ArrayList<Entry> dataValues(float[] indexes)
    {
        ArrayList<Entry> dataVal = new ArrayList<Entry>();
        for (int i = 0; i < indexes.length; i++) {
            dataVal.add(new Entry(i, indexes[i]));
        }
        return dataVal;
    }

    private ArrayList<BarEntry> dataValues(int[] indexes)
    {
        ArrayList<BarEntry> dataVal = new ArrayList<BarEntry>();
        for (int i = 0; i < indexes.length; i++) {
            dataVal.add(new BarEntry(i, indexes[i]));
        }
        return dataVal;
    }
}