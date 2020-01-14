package kr.co.valuesys.vlog.mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Rectangle extends View {
    Paint paint = new Paint();

    public Rectangle(Context context) {
        super(context);
    }


    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        Rect rect = new Rect(120, 100, 620, 900);
        canvas.drawRect(rect, paint );
    }
}