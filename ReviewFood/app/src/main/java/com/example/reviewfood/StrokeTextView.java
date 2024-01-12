package com.example.reviewfood;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;


public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {

    private TextPaint strokePaint;
    private int strokeColor;
    private float strokeWidth;

    public StrokeTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StrokeTextView, defStyle, 0);

        if (a.hasValue(R.styleable.StrokeTextView_strokeColor)) {
            strokeColor = a.getColor(
                    R.styleable.StrokeTextView_strokeColor,
                    Color.BLACK);
        }

        strokeWidth = a.getDimension(
                R.styleable.StrokeTextView_strokeWidth,
                0);

        a.recycle();

        // Set up a second TextPaint object for drawing the stroke
        strokePaint = new TextPaint();
        // Copy properties from original TextPaint
        strokePaint.set(getPaint());
        // Set the stroke width and color
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Use the strokePaint to draw the text stroke
        String text = getText().toString();
        canvas.drawText(text, (getWidth() - strokePaint.measureText(text)) / 2, getBaseline(), strokePaint);
        super.onDraw(canvas);
    }

    // Setters for stroke color and width
    public void setStrokeColor(int color) {
        strokeColor = color;
        strokePaint.setColor(strokeColor);
        invalidate();
    }

    public void setStrokeWidth(float width) {
        strokeWidth = width;
        strokePaint.setStrokeWidth(strokeWidth);
        invalidate();
    }
}