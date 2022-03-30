//package com.brouken.player;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PreviewView extends View {
//    public PreviewView(Context context) {
//        super(context);
//    }
//
//    public PreviewView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public PreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//
//    private List<Rect> list = new ArrayList();
//
//
//
//
//    private WLEDSettingsActivity.WledInfo info;
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        refresh(null);
//    }
//
//    public void refresh(WLEDSettingsActivity.WledInfo info){
//        if (info == null) {
//            info = WLEDSettingsActivity.read();
//        }
////        list = measureRect(getMeasuredWidth(), getMeasuredHeight(),
////                info.leftNum, info.topNum, info.rightNum, info.bottomNum,
////                info.leftPadding,info.topPadding,info.rightPadding,info.bottomPadding,
////                info.leftOffset,info.topOffset,info.rightOffset,info.bottomOffset);
//    }
//
//    public void setInfo(WLEDSettingsActivity.WledInfo info){
//        this.info = info;
//        refresh(info);
//    }
//
//
//    public void setList(List<Rect> list) {
//        this.list.clear();
//        this.list.addAll(list);
//        postInvalidate();
//    }
//
//    private Paint paint = new Paint();
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        paint.setColor(0x44FF0000);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(3);
//        for (Rect rect : list) {
//            canvas.drawRect(rect, paint);
//        }
//    }
//}
