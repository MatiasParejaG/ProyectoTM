package com.example.notas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class OverlayView extends View {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Rect rect;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE); // Cambiar a blanco
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#88000000")); // Semitransparente oscuro

        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int boxWidth = (int) (width * 0.8);
        int boxHeight = (int) (height * 0.25);

        int left = (width - boxWidth) / 2;
        int top = (height - boxHeight) / 2;
        int right = left + boxWidth;
        int bottom = top + boxHeight;

        rect.set(left, top, right, bottom);

        // Dibujar fondo oscurecido (4 zonas fuera del rectángulo)
        canvas.drawRect(0, 0, width, top, backgroundPaint); // Arriba
        canvas.drawRect(0, bottom, width, height, backgroundPaint); // Abajo
        canvas.drawRect(0, top, left, bottom, backgroundPaint); // Izquierda
        canvas.drawRect(right, top, width, bottom, backgroundPaint); // Derecha

        // Dibujar el borde blanco
        canvas.drawRect(rect, borderPaint);
    }
}
