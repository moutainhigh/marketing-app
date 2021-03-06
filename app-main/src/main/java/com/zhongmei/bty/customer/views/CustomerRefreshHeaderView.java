package com.zhongmei.bty.customer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshHeaderLayout;
import com.zhongmei.yunfu.R;

/**
 * 顾客下拉刷新z
 *
 * @date 2017/3/13 10:21
 */
public class CustomerRefreshHeaderView extends SwipeRefreshHeaderLayout {
    private ImageView ivArrow;

    private TextView tvRefresh;

    private ImageView ivProgress;

    private int mHeaderHeight;

    private Animation rotateCircle;

//    private Animation rotateDown;

//    private boolean rotated = false;

    public CustomerRefreshHeaderView(Context context) {
        this(context, null);
    }

    public CustomerRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_header_height);
        rotateCircle = AnimationUtils.loadAnimation(context, R.anim.rotate_circle_progress);
//        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
        ivArrow = (ImageView) findViewById(R.id.ivArrow);
        ivProgress = (ImageView) findViewById(R.id.ivProgress);
    }

    @Override
    public void onRefresh() {
        ivArrow.setVisibility(GONE);
        ivProgress.clearAnimation();
        ivProgress.startAnimation(rotateCircle);
        ivProgress.setVisibility(VISIBLE);
        tvRefresh.setText(R.string.customer_refresh_ing);
    }

    @Override
    public void onPrepare() {
        Log.d("TwitterRefreshHeader", "onPrepare()");
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (y > mHeaderHeight) {
                ivArrow.setVisibility(GONE);
                ivProgress.setVisibility(VISIBLE);
                tvRefresh.setText(R.string.customer_refresh_up);
//                if (!rotated) {
//                    ivArrow.clearAnimation();
//                    ivArrow.startAnimation(rotateUp);
//                    rotated = true;
//                }
            } else if (y < mHeaderHeight) {
//                if (rotated) {
//                    ivArrow.clearAnimation();
//                    ivArrow.startAnimation(rotateDown);
//                    rotated = false;
//                }
                ivArrow.setVisibility(VISIBLE);
                ivProgress.setVisibility(VISIBLE);
                tvRefresh.setText(R.string.customer_refresh_down);
            }
        }
    }

    @Override
    public void onRelease() {
        Log.d("TwitterRefreshHeader", "onRelease()");
    }

    @Override
    public void onComplete() {
//        rotated = false;
//        ivSuccess.setVisibility(VISIBLE);
//        ivArrow.clearAnimation();
        ivProgress.clearAnimation();
        ivArrow.setVisibility(GONE);
        ivProgress.setVisibility(GONE);
        tvRefresh.setText(R.string.customer_refresh_complete);
    }

    @Override
    public void onReset() {
//        rotated = false;
//        ivSuccess.setVisibility(GONE);
//        ivArrow.clearAnimation();
        ivProgress.clearAnimation();
        ivArrow.setVisibility(VISIBLE);
        ivProgress.setVisibility(VISIBLE);
    }
}
