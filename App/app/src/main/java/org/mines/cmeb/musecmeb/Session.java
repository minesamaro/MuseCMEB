package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Button;

public class Session extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);

        CircleView circleView = findViewById(R.id.circleView);

        // Color settings
        Paint paint = new Paint();
        paint.setColor(hexToColor("#83C5BE"));
        paint.setStyle(Paint.Style.FILL);
        circleView.setPaint(paint);

        // Size settings
        circleView.stressIdxToRadius(50);
        circleView.startPulsatingAnimation();

        Button button = findViewById(R.id.sessionExitBt);
        button.setBackgroundColor(hexToColor("#F96E46"));

        button.setOnClickListener(v -> onBackPressed());
    }

    private int hexToColor(String colorHex) {
        return Color.parseColor(colorHex);
    }
}
