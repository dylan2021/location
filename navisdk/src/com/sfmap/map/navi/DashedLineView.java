package com.sfmap.map.navi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DashedLineView extends View {
    public Context ctx;

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
//        paint.setColor(getResources().getColor(R2.color.c3_color));
        paint.setStrokeWidth(dip2px(ctx, 2));
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, 1000);
        PathEffect effects = new DashPathEffect(new float[]{6, 4, 4, 4}, 2);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("@@@", "dip2px: density = " + scale);
        return (int) (dipValue * scale + 0.5f);
    }
}
