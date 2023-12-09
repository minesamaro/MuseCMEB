package org.mines.cmeb.musecmeb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

public class CircularImageView extends androidx.appcompat.widget.AppCompatImageView {

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //this allows a squared image to appear as round
    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int width = this.getWidth();
        int height = this.getHeight();
        path.addCircle(width/2, height/2, Math.min(width, height) / 2.5f, Path.Direction.CCW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}