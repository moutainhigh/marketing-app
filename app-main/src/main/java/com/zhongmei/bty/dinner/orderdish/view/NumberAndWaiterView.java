package com.zhongmei.bty.dinner.orderdish.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

/**
 * @Date： 17/6/19
 * @Description:
 * @Version: 1.0
 */
public abstract class NumberAndWaiterView extends LinearLayout {

    protected FragmentActivity mActivity;

    protected StatusChangeListener onChangeListener;

    public interface StatusChangeListener {
        void onDataChanged();

        void onSubmit();

        void onCancel();
    }

    public abstract void updateNumberAndWaiter();

    public void setStatusChangeListener(StatusChangeListener listener) {
        this.onChangeListener = listener;
    }

    public NumberAndWaiterView(Context context) {
        super(context);
        this.mActivity = (FragmentActivity) context;
    }
}
