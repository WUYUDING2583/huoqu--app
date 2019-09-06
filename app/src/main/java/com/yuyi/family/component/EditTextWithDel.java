package com.yuyi.family.component;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.support.v7.widget.AppCompatEditText;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.yuyi.family.R;

public class EditTextWithDel extends AppCompatEditText {

    private final static String TAG = "EditTextWithDel";
    private Drawable imgRight;
    private Drawable imgLeft;
    private Context mContext;

    public EditTextWithDel(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        imgRight = mContext.getResources().getDrawable(R.drawable.delete);
        imgLeft=getCompoundDrawables()[0];//获取左侧的图片
        if(imgLeft!=null) {
            System.out.println("asdf");
            imgLeft.setBounds(0, 0, 90, 90);
        }else {
            System.out.println("rrr");
        }
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    /**
     *设置图片的显示
     */
    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawables(imgLeft, null, null, null);
        else {
            //设置图片大小
            imgRight.setBounds(0,0,80,80);
            setCompoundDrawables(imgLeft, null, imgRight, null);
        }
        setCompoundDrawablePadding(60);
    }

    /**
     * 处理删除事件
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 判断触摸点是否在图标边界内
         */
        if (imgLeft != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.startAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 20, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}

