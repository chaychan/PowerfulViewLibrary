package com.chaychan.viewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class PowerfulEditText extends EditText {

    private static final String TAG = PowerfulEditText.class.getSimpleName();
    private static final int TYPE_NORMAL = -1;
    private static final int TYPE_CAN_CLEAR = 0;
    private static final int TYPE_CAN_WATCH_PWD = 1;


    /**
     * 右侧图标的Drawable对象
     */
    private Drawable mRightDrawable;

    /**
     * 开启查看密码的drawable对象
     */
    private Drawable mEyeOpenDrawable;

    /*
     * 功能的类型
     * 默认为-1，没有功能
     * 0，带有清除文本功能
     * 1，带有查看密码功能
     */
    private int funcType;

    /**
     * 是否开启查看密码，默认没有
     */
    private boolean eyeOpen = false;
    /**
     * 关闭查看密码图标的资源id
     */
    private int eyeCloseResourseId;
    /**
     * 开启查看密码图标的资源id
     */
    private int eyeOpenResourseId;
    /**
     * 左侧drawable的宽度
     */
    private int leftWidth;
    /**
     * 左侧drawable的高度
     */
    private int leftHeight;
    /**
     * 右侧drawable的宽度
     */
    private int rightWidth;
    /**
     * 右侧drawable的高度
     */
    private int rightHeight;
    private TypedArray ta;


    public PowerfulEditText(Context context) {
        this(context, null);
    }

    public PowerfulEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义  
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public PowerfulEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ta = context.obtainStyledAttributes(attrs, R.styleable.PowerfulEditText);

        funcType = ta.getInt(R.styleable.PowerfulEditText_funcType, TYPE_NORMAL);

        eyeCloseResourseId = ta.getResourceId(R.styleable.PowerfulEditText_eyeClose, R.mipmap.eye_close);
        eyeOpenResourseId = ta.getResourceId(R.styleable.PowerfulEditText_eyeOpen, R.mipmap.eye_open);

        init();
    }


    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,左上右下
        Drawable leftDrawable = getCompoundDrawables()[0];
        mRightDrawable = getCompoundDrawables()[2];

        if (mRightDrawable == null) {
            //如果右侧没有图标
            if (funcType == TYPE_CAN_CLEAR) {
                //有清除功能，设置默认叉号选择器
                mRightDrawable = getResources().getDrawable(R.drawable.delete_selector);
            } else if (funcType == TYPE_CAN_WATCH_PWD) {
                //有查看密码功能，设置默认查看密码功能
                mRightDrawable = getResources().getDrawable(eyeCloseResourseId);
                mEyeOpenDrawable = getResources().getDrawable(eyeOpenResourseId);
            }
        }

        if (leftDrawable != null) {
            leftWidth = ta.getDimensionPixelOffset(R.styleable.PowerfulEditText_leftDrawableWidth,leftDrawable.getIntrinsicWidth());
            leftHeight = ta.getDimensionPixelOffset(R.styleable.PowerfulEditText_leftDrawableHeight,leftDrawable.getIntrinsicHeight());
            leftDrawable.setBounds(0, 0, leftWidth, leftHeight);
        }

        if (mRightDrawable != null) {
            rightWidth = ta.getDimensionPixelOffset(R.styleable.PowerfulEditText_rightDrawableWidth,mRightDrawable.getIntrinsicWidth());
            rightHeight = ta.getDimensionPixelOffset(R.styleable.PowerfulEditText_rightDrawableWidth,mRightDrawable.getIntrinsicHeight());
            mRightDrawable.setBounds(0, 0, rightWidth, rightHeight);
            if (mEyeOpenDrawable != null) {
                mEyeOpenDrawable.setBounds(0, 0, rightWidth, rightHeight);
            }

            //如果是清除功能，则一开始隐藏右侧默认图标，否则不隐藏右侧默认图标
            setRightIconVisible(funcType == 0 ? false : true);
            //设置输入框里面内容发生改变的监听
            addTextChangedListener(new TextWatcher() {
                /**
                 * 当输入框里面内容发生变化的时候回调的方法
                 */
                @Override
                public void onTextChanged(CharSequence s, int start, int count,
                                          int after) {
                    if (funcType == 0) {
                        setRightIconVisible(s.length() > 0);
                    }

                    if (textListener != null) {
                        textListener.onTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    if (textListener != null) {
                        textListener.beforeTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (textListener != null) {
                        textListener.afterTextChanged(s);
                    }
                }

            });
        }
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

                    if (onRightClickListener == null) {
                        if (funcType == TYPE_CAN_CLEAR) {
                            //如果没有设置右边图标的点击事件，并且带有清除功能，默认清除文本
                            this.setText("");
                        } else if (funcType == TYPE_CAN_WATCH_PWD) {
                            //如果没有设置右边图标的点击事件，并且带有查看密码功能，点击查看密码
                            if (eyeOpen) {
                                //变为密文 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
                                this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                eyeOpen = false;
                            } else {
                                //变为明文
                                this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                eyeOpen = true;
                            }
                            switchWatchPwdIcon();//切换图标
                        }
                    } else {
                        //如果有则回调
                        onRightClickListener.onClick(this);
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }


    /**
     * 设置右侧图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setRightIconVisible(boolean visible) {
        Drawable right = visible ? mRightDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 切换查看密码的图标
     */
    private void switchWatchPwdIcon() {
        if (eyeOpen) {
            //开启查看
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], mEyeOpenDrawable, getCompoundDrawables()[3]);
        } else {
            //关闭查看
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], mRightDrawable, getCompoundDrawables()[3]);
        }
    }



    private OnRightClickListener onRightClickListener;

    /**
     * 右边图标点击的回调
     */
    public interface OnRightClickListener {
        void onClick(EditText editText);
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }


    private TextListener textListener;

    public void addTextListener(TextListener textListener) {
        this.textListener = textListener;
    }

    /**
     * 输入框文本变化的回调，如果需要进行多一些操作判断，则设置此listen替代TextWatcher
     */
    public interface TextListener {
        void onTextChanged(CharSequence s, int start, int count, int after);

        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void afterTextChanged(Editable s);
    }
}