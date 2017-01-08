package cn.a6_79.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CircleBar extends View {
    private RectF mColorWheelRectangle = new RectF();
    private Paint mDefaultWheelPaint;
    private Paint mColorWheelPaint;
    private Paint textPaint;
    private float circleStrokeWidth;
    private float pressExtraStrokeWidth;
    private String mText = "0";
    private int mCount = 0;
    private float mSweepAnglePer;
    private float mSweepAngle;
    private int total;
    BarAnimation anim;
    private String endText = "";

    public CircleBar(Context context) {
        super(context);
        init();
    }

    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        circleStrokeWidth = dip2px(getContext(), 10);
        pressExtraStrokeWidth = dip2px(getContext(), 2);
        int mTextSize = dip2px(getContext(), 20);

        mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorWheelPaint.setColor(0xFFCC1D1D);
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);

        mDefaultWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDefaultWheelPaint.setColor(0xFFeeefef);
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(0xFFeeefef);
        textPaint.setStyle(Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setTextSize(mTextSize);

        mText = "0";
        mSweepAngle = 0;

        anim = new BarAnimation();
    }

    public void start() {
        anim.setDuration((int)(mSweepAngle*3000.0/360));
        this.invalidate();
    }

    public void setTotal(int total) { this.total = total; }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mColorWheelRectangle, -90, 360, false, mDefaultWheelPaint);
        canvas.drawArc(mColorWheelRectangle, -90, mSweepAnglePer, false, mColorWheelPaint);
        @SuppressLint("DrawAllocation") Rect bounds = new Rect();
        String textStr;
        if (mCount == Integer.parseInt(mText))
            textStr = endText;
        else
            textStr = mCount+"/"+total;
        textPaint.getTextBounds(textStr, 0, textStr.length(), bounds);
        canvas.drawText(
                textStr,
                (mColorWheelRectangle.centerX())
                        - (textPaint.measureText(textStr) / 2),
                mColorWheelRectangle.centerY() + bounds.height() / 2,
                textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float mColorWheelRadius = min - circleStrokeWidth - pressExtraStrokeWidth;

        mColorWheelRectangle.set(circleStrokeWidth+pressExtraStrokeWidth, circleStrokeWidth+pressExtraStrokeWidth,
                mColorWheelRadius, mColorWheelRadius);
    }


    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        this.invalidate();
    }

    public void setText(String text){
        mText = text;
        this.startAnimation(anim);
    }

    public void setSweepAngle(float sweepAngle){
        mSweepAngle = sweepAngle;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }


    public class BarAnimation extends Animation {
        BarAnimation() {}
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mSweepAnglePer =  interpolatedTime * mSweepAngle;
                mCount = (int)(interpolatedTime * Float.parseFloat(mText));
            } else {
                mSweepAnglePer = mSweepAngle;
                mCount = Integer.parseInt(mText);
            }
            postInvalidate();
        }
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}