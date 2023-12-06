package org.mines.cmeb.musecmeb;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class CircleView extends View {

    private final float  MAX_RADIUS = 400;
    private final float MIN_RADIUS = 50;
    private float radius;  // Circle radius for the animation
    private Paint paint;
    private ValueAnimator pulsatingAnimator;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        radius = 50; // Default radius
        paint = new Paint();
        paint.setColor(Color.BLUE); // Set the circle color
        paint.setStyle(Paint.Style.FILL);
    }

    private void setRadius(float radius) {
        this.radius = radius;
        this.setupPulsatingAnimation();
        invalidate(); // Redraw the view when the radius changes
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        invalidate(); // Redraw the view when the radius changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    public void stressIdxToRadius(double stressIdx) {
        if (stressIdx >= 100)  // in case the stress index is greater than 100, we set it to the max radius
            setRadius(MAX_RADIUS);
        else if (stressIdx <= 10)  // in case the stress index is lower than 10 (changeable), we consider the user  relaxed, and set it to the min radius
            setRadius(MIN_RADIUS);
        else {
            double new_radius = MappingFunctions.lin(stressIdx, 0, 100, MIN_RADIUS, MAX_RADIUS, MIN_RADIUS);
            setRadius((float) new_radius);
        }
    }

    private void setupPulsatingAnimation() {
        // Define the pulsating animation
        pulsatingAnimator = ValueAnimator.ofFloat(radius, radius * 1.5f, radius);
        pulsatingAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulsatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulsatingAnimator.setInterpolator(new DecelerateInterpolator());

        // Duration for each phase of the animation (in ms)
        pulsatingAnimator.setDuration(10_000); // Adjust the duration as needed (here 10 seconds)

        // Update the radius during the animation
        pulsatingAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            setRadius(animatedValue);
        });
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
