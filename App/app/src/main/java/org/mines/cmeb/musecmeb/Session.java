package org.mines.cmeb.musecmeb;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Button;


public class Session extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);

        // Assuming you have a layout file (e.g., activity_main.xml) with a FrameLayout
        // where you want to display the CircleView
        CircleView circleView = findViewById(R.id.circleView);
        circleView.setRadius(200); // Set the desired radius

        Paint paint = new Paint();
        paint.setColor(hexToColor("#83C5BE")); // Set the desired color
        paint.setStyle(Paint.Style.FILL);
        circleView.setPaint(paint);// Set the desired color

        Button button = findViewById(R.id.sessionBt);
        button.setBackgroundColor(hexToColor("#F96E46"));
        //setContentView(circleView);
    }

    // Converts an hexadecimal color string to an array of RGB values
    private int hexToColor(String colorHex) {
        return Color.parseColor(colorHex);
    }

}
