package com.example.widget_and_mqtt_test.customDrawables

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class CircleDrawable(
    private val fillColor: Int,
    private val strokeColor: Int,
    private val radius: Float
) :
    Drawable() {
    private val circlePaint: Paint

    init {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun draw(canvas: Canvas) {
        val x = bounds.centerX()
        val y = bounds.centerY()
        //draw fill color circle
        circlePaint.setStyle(Paint.Style.FILL)
        circlePaint.setColor(fillColor)
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius, circlePaint)
        // draw stroke circle
        circlePaint.setStyle(Paint.Style.STROKE)
        circlePaint.setColor(strokeColor)
        circlePaint.setStrokeWidth(5F)
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius, circlePaint)
    }

    override fun setAlpha(alpha: Int) {
        circlePaint.setAlpha(alpha)
    }

    override fun setColorFilter(@Nullable colorFilter: ColorFilter?) {
        circlePaint.setColorFilter(colorFilter)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}
