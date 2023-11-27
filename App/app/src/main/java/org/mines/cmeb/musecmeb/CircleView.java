package org.mines.cmeb.musecmeb;

import static android.opengl.ETC1.getWidth;

import android.graphics.Canvas;import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

// CircleView is a helper class to draw a circle; it is used in the Session class
public class CircleView extends View {
    private float radius;
    private Paint paint;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        radius = 50; // Default radius
        paint = new Paint();
        paint.setColor(Color.BLUE); // Set the circle color
        paint.setStyle(Paint.Style.FILL);
        startPulsatingAnimation();
    }

    public void setRadius(float radius) {
        this.radius = radius;
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


    private void startPulsatingAnimation() {
        // Define the pulsating animation
        ValueAnimator pulsatingAnimator = ValueAnimator.ofFloat(50f, 200f, 50f); // ValueAnimator.ofFloat(startRadius, endRadius, intermediateRadius)
        pulsatingAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulsatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulsatingAnimator.setInterpolator(new DecelerateInterpolator());

        // Duration for each phase of the animation (in ms)
        pulsatingAnimator.setDuration(10_000); // Adjust the duration as needed (here 10 seconds)
        pulsatingAnimator.setStartDelay(2000); // Wait 2 seconds before starting the animation
        // Shrinking time = 10 000 - 2000 = 8000 ms

        // Update the radius during the animation
        pulsatingAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            setRadius(animatedValue);
        });

        // Start the animation
        pulsatingAnimator.start();
    }
}
