package org.mines.cmeb.musecmeb;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Map;

public class PastSessionFragment extends Fragment{

    private ImageButton backButton;
    private TextView sessionTitle;
    private TextView relaxTimeText;
    LineChart sessionChart;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_session, container, false);

        sessionChart = (LineChart) view.findViewById(R.id.chart1);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues(), "Data Set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        sessionChart.setBackgroundColor(Color.TRANSPARENT);
        sessionChart.setNoDataText("No data was recorded in this session");
        sessionChart.setNoDataTextColor(Color.parseColor("#edf6f9"));
        sessionChart.setAutoScaleMinMaxEnabled(true);
        sessionChart.setKeepPositionOnRotation(true);
        sessionChart.setTouchEnabled(true);
        sessionChart.setDrawGridBackground(false);

        // Position the X-axis at the bottom
        XAxis xAxis = sessionChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        Description description = new Description();
        description.setText("Stress Index through the session");
        description.setTextColor(Color.parseColor("#edf6f9"));
        description.setTextSize(20);
        sessionChart.setDescription(description);



        LineData data = new LineData(dataSets);
        sessionChart.setData(data);
        sessionChart.invalidate();



        backButton = view.findViewById(R.id.backButton);
        sessionTitle = view.findViewById(R.id.sessionTitle);
        relaxTimeText = view.findViewById(R.id.relaxTimeText);

        // Set a click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click event
            }
        });

        // Set the text for the TextViews and the image for the ImageView
        sessionTitle.setText("Session 2 - 30/10/2023");
        relaxTimeText.setText("Time of relaxation: 3 minutes and 23 seconds");

        return view;
    }

    private ArrayList<Entry> dataValues()
    {
        ArrayList<Entry> dataVal = new ArrayList<Entry>();
        dataVal.add(new Entry(0, 20));
        dataVal.add(new Entry(1, 22));
        dataVal.add(new Entry(2, 24));
        dataVal.add(new Entry(3, 13));
        dataVal.add(new Entry(4, 9));
        dataVal.add(new Entry(5, 50));

        return dataVal;

    }
}
