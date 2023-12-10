package org.mines.cmeb.musecmeb;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;

public class CircleView extends View {

    private float radius;  // Circle radius for the animation

    private final float RADIUS = 300f;  // Fixed radius for the circle view

    private Paint paint = new Paint();
    private ValueAnimator pulsatingAnimator;

    private int[][] COLORS = {   // Color lookup table
            {175, 252, 233},   // 0
            {173, 250, 229},
            {171, 249, 224},
            {170, 247, 219},
            {169, 246, 214},
            {168, 244, 209},   // 5
            {168, 242, 204},
            {167, 240, 199},
            {167, 238, 194},
            {168, 237, 189},
            {168, 235, 184},   // 10
            {169, 233, 178},
            {170, 231, 173},
            {171, 229, 167},
            {172, 226, 162},
            {173, 224, 157},   // 15
            {175, 222, 151},
            {176, 220, 146},
            {178, 217, 140},
            {180, 215, 135},
            {182, 212, 130},   // 20
            {184, 210, 125},
            {186, 207, 120},
            {189, 205, 115},
            {191, 202, 110},
            {191, 202, 110},    // 25
            {193, 199, 105},
            {196, 196, 100},
            {199, 193, 96},
            {201, 190, 91},
            {204, 187, 87},    // 30
            {206, 184, 83},
            {209, 180, 79},
            {212, 177, 76},
            {215, 174, 73},
            {217, 170, 70},    // 35
            {220, 166, 67},
            {222, 163, 65},
            {225, 159, 63},
            {228, 155, 62},
            {230, 151, 61},   // 40
            {233, 147, 61},
            {235, 143, 61},
            {237, 138, 61},
            {240, 134, 62},
            {242, 129, 63},   // 45
            {244, 125, 64},
            {246, 120, 66},
            {247, 115, 68},
            {249, 110, 70}    // 49
            };

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Context context = getContext();
        int color = ContextCompat.getColor(context, R.color.our_light_blue);

        //paint = new Paint();
        paint.setColor(color); // Set the circle color
        paint.setStyle(Paint.Style.FILL);
    }

    private void setRadius(float radius) {
        this.radius = radius;
        invalidate(); // Redraw the view when the radius changes
    }
    private void setColor(int color) {
        this.paint.setColor(color);
        invalidate(); // Redraw the view when the radius changes
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    public void setupPulsatingAnimation() {

        // Define the pulsating animation
        pulsatingAnimator = ValueAnimator.ofFloat(RADIUS, RADIUS * 1.5f, RADIUS);
        pulsatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulsatingAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulsatingAnimator.setInterpolator(new DecelerateInterpolator());

        // Duration for each phase of the animation (in ms)
        pulsatingAnimator.setDuration(10_000); // Adjust the duration as needed (here 10 seconds)

        // Update the radius during the animation
        pulsatingAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            setRadius(animatedValue);
        });

        // Start the animation
        startPulsatingAnimation();
    }

    public void changeColorPulsatingAnimation(int stressIdx){
        stressIdx /= 2;  // integer division will round down, and output an integer (corresponding to the index of the COLORS array)

        // Make sure the index is within the bounds of the array
        if (stressIdx < 0) {
            stressIdx = 0;
        } else if (stressIdx > 49) {
            stressIdx = 49;
        }

        int color = Color.rgb(COLORS[stressIdx][0], COLORS[stressIdx][1], COLORS[stressIdx][2]);
        setColor(color);
    }

    public void startPulsatingAnimation() {
        // Start the animation
        if (pulsatingAnimator != null && !pulsatingAnimator.isRunning()) {
            pulsatingAnimator.start();
        }
    }

    public void stopPulsatingAnimation() {
        // Stop the animation
        if (pulsatingAnimator != null && pulsatingAnimator.isRunning()) {
            pulsatingAnimator.cancel();
        }
    }
}
