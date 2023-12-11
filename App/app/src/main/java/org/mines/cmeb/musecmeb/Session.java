package org.mines.cmeb.musecmeb;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Session extends AppCompatActivity {
    ArrayList<Integer> dynamicList;
    private float relaxationTime;
    private Date startDate;
    Runnable updateCircleViewTask;
    Handler handler;
    private double stressIndex;
    private int parsedStressIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);


        // Start the session (save start time) and create array list of indexes
        startDate = new Date();
        dynamicList = new ArrayList<>();

        CircleView circleView = findViewById(R.id.circleView);

        circleView.setupPulsatingAnimation();



        // Exit button settings
        Button button = findViewById(R.id.sessionExitBt);
        button.setBackgroundColor(Color.parseColor("#F96E46"));

        button.setOnClickListener(v -> {
            endSession();
            // Here we should start the end of session activity
            endOfSessionLayout();
        });

        // TEST METHOD (comment if not needed)
        //testCircleView();

        // TEST for LibTest
        acquireData();
    }


    private void acquireData(){
        handler = new Handler();
        updateCircleViewTask = new Runnable() {
            @Override
            public void run() {
                setNewerCircleView();
                if(checkIfRelaxed())
                {
                    endSession();
                    endOfSessionLayout();
                }
                else {
                    handler.postDelayed(this, 1000); // 3 seconds
                }
            }
        };
        handler.postDelayed(updateCircleViewTask, 1000); // Start the task initially
    }

    private void testCircleView(){
        handler = new Handler();
        updateCircleViewTask = new Runnable() {
            @Override
            public void run() {
                setNewCircleView();
                handler.postDelayed(this, 3000); // 3 seconds
            }
        };
        handler.postDelayed(updateCircleViewTask, 3000); // Start the task initially
    }
    private void setNewerCircleView(){
        CircleView circleView = findViewById(R.id.circleView);

        // Start the data acquisition service
        stressIndex = ((GlobalMuse) this.getApplication()).getMomentStressIndex();
        Log.i("StressIdx", String.valueOf(stressIndex));

        parsedStressIndex = MappingFunctions.lin(stressIndex);
        Log.i("parsedStressIdx", String.valueOf(parsedStressIndex));

        circleView.changeColorPulsatingAnimation(parsedStressIndex);
        addIndex(parsedStressIndex);
    }
    private void setNewCircleView(){
        CircleView circleView = findViewById(R.id.circleView);
        int rnd_num = (int) (Math.random() * 101);  // Random number between 0 and 100 (our fake stress index)
        circleView.changeColorPulsatingAnimation(rnd_num);
    }

    private void endSession() {
        // Stop running of updating Circle
        handler.removeCallbacks(updateCircleViewTask);

        // Get time of session
        Date finishDate = new Date();
        long duration = finishDate.getTime() - startDate.getTime();
        relaxationTime = (float) (duration / 60000.0);

        // Convert the ArrayList to an array of integers (int[])
        int[] stressIndexes = new int[dynamicList.size()];
        for (int i = 0; i < dynamicList.size(); i++) {
            stressIndexes[i] = dynamicList.get(i);
        }

        // Create Relaxation Session Instance to insert in database
        RelaxationSession session = new RelaxationSession(1, stressIndexes, relaxationTime, startDate);
        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            dbHelper.addSession(session);
        }
    }
    private void endOfSessionLayout(){
        setContentView(R.layout.end_of_session);
        ImageButton backBt = findViewById(R.id.backButton);

        TextView sessionTime = findViewById(R.id.sessionTimeTextView);
        sessionTime.setText(getFormattedTimeOfRelaxation(relaxationTime));

        backBt.setOnClickListener(view -> onBackPressed());

    }

    /**
     * Add index from data acquisition to the session indexes
     */
    private void addIndex(int index){
        // Receive integer index from Data Acquisition and store it in the array list
        // Change this line to the method for getting
        dynamicList.add(index);
    }

    private boolean checkIfRelaxed(){
        int necessaryLength = 20;
        int stressThreshold = 20;

        if (dynamicList.size() < necessaryLength) {
            // Not enough values in the list, relaxation cannot be determined
            return false;
        }

        // Check the last necessaryLength values consecutively
        for (int i = dynamicList.size() - necessaryLength; i < dynamicList.size(); i++) {
            if (dynamicList.get(i) > stressThreshold) {
                // At least one value is higher than stressThreshold
                return false;
            }
        }
        // All values are lower than stressThreshold
        return true;
    }

    private String getFormattedTimeOfRelaxation(float time){
        int minutes = (int) time;
        int seconds = (int) ((time - minutes) * 60);

        // Create a string representation
        return String.format(Locale.getDefault(),"%d min %d sec", minutes, seconds);
    }
}
