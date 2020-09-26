package com.nnoboa.istudy.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

public class TextDrawable extends Drawable {
    private final String text;
    private final Paint paint;

    public TextDrawable(String text) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(getRandomColor());
        paint.setTextSize(22f);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(6f,0,0,getRandomColor());
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawText(text,0,0,paint);
        canvas.drawOval(22f,22f,22f,22f,paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.argb(180, random.nextInt(200), random.nextInt(210), random.nextInt(250));
    }
}
