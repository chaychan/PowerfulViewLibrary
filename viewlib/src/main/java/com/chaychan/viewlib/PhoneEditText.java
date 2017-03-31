package com.chaychan.viewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

public class PhoneEditText extends EditText implements View.OnFocusChangeListener {
    private int lastLength = 0;
    private TextWatcher mTextWatcher;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    private String divider  = " ";


    public PhoneEditText(Context context) {
        this(context, null);
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle); // Attention here !
    }

    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText);
        String dividerString = typedArray.getString(R.styleable.PhoneEditText_dividerString);
        if (dividerString != null && dividerString.length() > 0){
            divider = dividerString;
        }
        typedArray.recycle();
        initContent();
    }

    private void initContent() {

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(hasFoucs){
                    //setClearIconVisible(s.length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lastLength){
                    lastLength = s.length();
                    return;
                }
                //防止进入死循环
                removeTextChangedListener(mTextWatcher);
                String text = getText().toString().trim();
                if (text.contains(divider)){//删除已添加的divider
                    String[] ses = text.split(divider);
                    for (int j = 0; j< ses.length -1;j++){
                        int length = 0;
                        for (int k = 0; k <= j;k++){
                            length = length + ses[k].length();
                        }
                        s.delete(length,length + divider.length());
                    }
                }
                if (s.length() > 3){//插入divider
                    s.insert(3,divider);
                }
                if (s.length() > (7 + divider.length())){
                    s.insert((7 + divider.length()),divider);
                }
                if (s.length() > (11 + divider.length()*2)){//删除最后添加的divider
                    s.delete((11 + divider.length()*2),getText().length());
                }

                lastLength = s.length();
                addTextChangedListener(mTextWatcher);
            }
        };
        addTextChangedListener(mTextWatcher);

        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.delete_selector);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
    }

    /**
     *
     */
    public String getTextString(){
        return getText().toString().replace(divider,"");
    }


    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
//        if (hasFocus) {
//            setClearIconVisible(getText().length() > 0);
//        } else {
//            setClearIconVisible(false);
//        }
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
        this.setAnimation(shakeAnimation(50));
    }


    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}