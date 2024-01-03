package org.mines.cmeb.musecmeb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PastSessionFragment extends Fragment{

    LineChart sessionChart;
    private int[] indexes;
    private int sessionId;
    private float relaxationTime;
    private Date startDate;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_past_session, container, false);

        // Get the views on the page
        ImageButton backButton = view.findViewById(R.id.backButton);
        TextView sessionTitle = view.findViewById(R.id.sessionTitle);
        TextView relaxTimeText = view.findViewById(R.id.relaxTimeText);
        sessionChart = (LineChart) view.findViewById(R.id.chart1);


        // Retrieve session id  from the bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            RelaxationSession session = (RelaxationSession) bundle.getSerializable("session");
            indexes = session.getStressIndexes();
            sessionId = session.getId();
            relaxationTime = session.getRelaxationTime();
            startDate = session.getStartDate();
            }

        Context context = getContext();
        int pink = ContextCompat.getColor(context, R.color.our_pink);


        // Line Chart Definitions and settings
        LineDataSet lineDataSet1 = new LineDataSet(dataValues(), "Stress Index over time");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        sessionChart.getAxisRight().setEnabled(false);
        sessionChart.getDescription().setEnabled(false);
        sessionChart.setBackgroundColor(Color.TRANSPARENT);
        sessionChart.setNoDataText("No data was recorded in this session");
        sessionChart.setNoDataTextColor(pink);
        sessionChart.setKeepPositionOnRotation(true);
        sessionChart.setTouchEnabled(true);
        sessionChart.setDrawGridBackground(false);

        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(pink);
        lineDataSet1.setCircleColor(pink);
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setValueTextColor(pink);

        // Position the X-axis at the bottom and change colors
        XAxis xAxis = sessionChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(pink);
        xAxis.setAxisLineColor(pink);
        xAxis.setTextColor(pink);
        xAxis.setDrawGridLines(false);

        // Get y Axis and change colors
        YAxis yAxis = sessionChart.getAxisLeft();
        yAxis.setGridColor(pink);
        yAxis.setAxisLineColor(pink);
        yAxis.setTextColor(pink);
        yAxis.setDrawGridLines(false);

        // Plot the chart
        LineData data = new LineData(dataSets);
        sessionChart.setData(data);
        sessionChart.invalidate();


        // Set a click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the MainActivity and the ViewPager
                MainActivity mainActivity = (MainActivity) getActivity();
                ViewPager viewPager = mainActivity.findViewById(R.id.viewPager);

                // Navigate to HistoryFragment
                viewPager.setCurrentItem(0); // Assuming HistoryFragment is at position 0
            }
        });


        String title = "Session "+ String.valueOf(sessionId)
                + " - " + getFormattedDate(startDate);

        String time = "Time of relaxation: " + getFormattedTimeOfRelaxation(relaxationTime);

        sessionTitle.setText(title);
        relaxTimeText.setText(time);

        return view;
    }

    private ArrayList<Entry> dataValues()
    {
        ArrayList<Entry> dataVal = new ArrayList<Entry>();
        for (int i = 0; i < indexes.length; i++) {
            dataVal.add(new Entry(i, indexes[i]));
        }
        return dataVal;
    }


    private String getFormattedDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Use the format method to format the Date object
        return sdf.format(date);
    }

    private String getFormattedTimeOfRelaxation(float time){
        int minutes = (int) time;
        int seconds = (int) ((time - minutes) * 60);

        // Create a string representation
        return String.format("%d min %d sec", minutes, seconds);
    }


}
