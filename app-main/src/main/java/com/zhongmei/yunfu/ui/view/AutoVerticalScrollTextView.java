package com.zhongmei.yunfu.ui.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 自动垂直滚动的TextView
 */
public class AutoVerticalScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private Context mContext;

    //mInUp,mOutUp分别构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;
    //private int textColor = Color.BLACK;
    //private int textSize = 10;
    private AttributeSet attrs;

    public AutoVerticalScrollTextView(Context context) {
        this(context, null);
    }

    public AutoVerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.attrs = attrs;
        /*final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoVerticalScrollTextView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.AutoVerticalScrollTextView_textColor:
                    textColor = a.getColor(i, textColor);
                    break;
                case R.styleable.AutoVerticalScrollTextView_textSize:
                    textSize = a.getDimensionPixelSize(i, textSize);
                    break;
            }
        }
        a.recycle();*/

        init();
    }

    private void init() {
        setFactory(this);
        mInUp = createAnim(true, true);
        mOutUp = createAnim(false, true);
        setInAnimation(mInUp);//当View显示时动画资源ID
        setOutAnimation(mOutUp);//当View隐藏是动画资源ID。
    }

    private Rotate3dAnimation createAnim(boolean turnIn, boolean turnUp) {
        Rotate3dAnimation rotation = new Rotate3dAnimation(turnIn, turnUp);
        rotation.setDuration(200);//执行动画的时间
        rotation.setFillAfter(false);//是否保持动画完毕之后的状态
        rotation.setInterpolator(new AccelerateInterpolator());//设置加速模式
        return rotation;
    }

    @Override
    public void setText(CharSequence text) {
        next();
        super.setText(text);
    }

    //这里返回的TextView，就是我们看到的View,可以设置自己想要的效果
    @Override
    public View makeView() {
        TextView textView = new TextView(mContext, attrs);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        //textView.setGravity(Gravity.LEFT);
        //textView.setGravity(Gravity.CENTER_VERTICAL);
        //textView.setTextColor(textColor);
        //textView.setTextSize(textSize);
        //textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        //textView.setTextColor(getResources().getColor(R.color.color_666666));
        return textView;
    }

    //定义动作，向上滚动翻页
    public void next() {
        //显示动画
        if (getInAnimation() != mInUp) {
            setInAnimation(mInUp);
        }
        //隐藏动画
        if (getOutAnimation() != mOutUp) {
            setOutAnimation(mOutUp);
        }
    }

    class Rotate3dAnimation extends Animation {
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;

        public Rotate3dAnimation(boolean turnIn, boolean turnUp) {
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight();
            mCenterX = getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1 : -1;
            final Matrix matrix = t.getMatrix();
            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime), 0.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

}
