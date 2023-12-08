package org.mines.cmeb.musecmeb;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Button;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Date;

public class Session extends AppCompatActivity {
    private int[] stressIndexes;
    ArrayList<Integer> dynamicList;
    private float relaxationTime;
    private Date startDate;
    private Date finishDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);


        // Start the session (save start time) and create array list of indexes
        startDate = new Date();
        dynamicList = new ArrayList<>();

        CircleView circleView = findViewById(R.id.circleView);

        // Color settings
        Paint paint = new Paint();
        paint.setColor(hexToColor("#83C5BE"));
        paint.setStyle(Paint.Style.FILL);
        circleView.setPaint(paint);

        // Size settings
        circleView.stressIdxToRadius(10);
        circleView.startPulsatingAnimation();

        Button button = findViewById(R.id.sessionExitBt);
        button.setBackgroundColor(hexToColor("#F96E46"));

        button.setOnClickListener(v -> {
            endSession();
            // Here we should start the end of session activity
            onBackPressed();
        });
    }

    private void endSession() {
        // Get time of session
        finishDate = new Date();
        long duration = finishDate.getTime() - startDate.getTime();
        float durationInMinutes = (float) (duration / 60000.0);

        // Convert the ArrayList to an array of integers (int[])
        stressIndexes = new int[dynamicList.size()];
        for (int i = 0; i < dynamicList.size(); i++) {
            stressIndexes[i] = dynamicList.get(i);
        }

        // Create Relaxation Session Instance to insert in database
        RelaxationSession session = new RelaxationSession(1, stressIndexes, durationInMinutes, startDate);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addSession(session);
    }

    /**
     * Add index from data acquition to the session indexes
     */
    private void addIndex(){
        // Receive integer index from Data Acquisition and store it in the array list
        // Change this line to the method for getting
        int index = 15;
        dynamicList.add(index);
    }

    private boolean checkIfRelaxed(){
        int necessaryLength = 4;
        int stressThreshold = 5;

        if (dynamicList.size() < necessaryLength) {
            // Not enough values in the list, relaxation cannot be determined
            return false;
        }

        // Check the last necessaryLength values
        for (int i = dynamicList.size() - necessaryLength; i < dynamicList.size(); i++) {
            int value = dynamicList.get(i);
            if (value >= stressThreshold) {
                // If any value is greater than or equal to stressThreshold, return false
                return false;
            }
        }
        // All values are lower than stressThreshold
        return true;
    }

    private int hexToColor(String colorHex) {
        return Color.parseColor(colorHex);
    }
}
