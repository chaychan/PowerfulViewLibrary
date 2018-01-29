package com.chaychan.viewlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chaychan.viewlib.utils.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数字滚动的textView
 */
public class NumberRunningTextView extends TextView {

    private static final int MONEY_TYPE = 0;
    private static final int NUM_TYPE = 1;

    private int textType;//内容的类型，默认是金钱类型
    private boolean useCommaFormat;//是否使用每三位数字一个逗号的格式，让数字显得比较好看，默认使用
    private boolean runWhenChange;//是否当内容有改变才使用动画,默认是
    private int duration;//动画的周期，默认为800ms
    private int minNum;//显示数字最少要达到这个数字才滚动 默认为1
    private float minMoney;//显示金额最少要达到这个数字才滚动 默认为0.3

    private DecimalFormat formatter = new DecimalFormat("0.00");// 格式化金额，保留两位小数
    private String preStr;


    public NumberRunningTextView(Context context) {
        this(context, null);
    }

    public NumberRunningTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public NumberRunningTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberRunningTextView);
        duration = ta.getInt(R.styleable.NumberRunningTextView_duration, 1000);
        textType = ta.getInt(R.styleable.NumberRunningTextView_textType, MONEY_TYPE);
        useCommaFormat = ta.getBoolean(R.styleable.NumberRunningTextView_useCommaFormat, true);
        runWhenChange = ta.getBoolean(R.styleable.NumberRunningTextView_runWhenChange,true);
        minNum = ta.getInt(R.styleable.NumberRunningTextView_minNum, 3);
        minMoney = ta.getFloat(R.styleable.NumberRunningTextView_minMoney,0.1f);

        ta.recycle();
    }


    /**
     * 设置需要滚动的金钱(必须为正数)或整数(必须为正数)的字符串
     *
     * @param str
     */
    public void setContent(String str) {
        //如果是当内容改变的时候才执行滚动动画,判断内容是否有变化
        if (runWhenChange){
            if (TextUtils.isEmpty(preStr)){
                //如果上一次的str为空
                preStr = str;
                useAnimByType(str);
                return;
            }

            //如果上一次的str不为空,判断两次内容是否一致
            if (preStr.equals(str)){
                //如果两次内容一致，则不做处理
                return;
            }

            preStr = str;//如果两次内容不一致，记录最新的str
        }

        useAnimByType(str);
    }

    private void useAnimByType(String str) {
        if (textType == MONEY_TYPE) {
            playMoneyAnim(str);
        } else if (textType == NUM_TYPE){
            playNumAnim(str);
        }
    }


    /**
     * 播放金钱数字动画的方法
     *
     * @param moneyStr
     */
    public void playMoneyAnim(String moneyStr) {
        String money = moneyStr.replace(",", "").replace("-", "");//如果传入的数字已经是使用逗号格式化过的，或者含有符号,去除逗号和负号
        try {
            BigDecimal bigDecimal = new BigDecimal(money);
            float finalFloat = bigDecimal.floatValue();
            if (finalFloat < minMoney) {
                //如果传入的为0，则直接使用setText()
                setText(moneyStr);
                return;
            }
            ValueAnimator floatAnimator =  ValueAnimator.ofObject(new BigDecimalEvaluator(),new BigDecimal(0), bigDecimal);
            floatAnimator.setDuration(duration);
            floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    BigDecimal currentNum = (BigDecimal) animation.getAnimatedValue();
                    String str = formatter.format(Double.parseDouble(currentNum.toString()));//格式化成两位小数
                    // 更新显示的内容
                    if (useCommaFormat) {
                        //使用每三位数字一个逗号的格式
                        String formatStr = StringUtils.addComma(str);//三位一个逗号格式的字符串
                        setText(formatStr);
                    } else {
                        setText(str);
                    }
                }
            });
            floatAnimator.start();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.setText(moneyStr);//如果转换Double失败则直接用setText
        }
    }

    /**
     * 播放数字动画的方法
     *
     * @param numStr
     */
    public void playNumAnim(String numStr) {
        String num = numStr.replace(",", "").replace("-", "");//如果传入的数字已经是使用逗号格式化过的，或者含有符号,去除逗号和负号
        try {
            int finalNum = Integer.parseInt(num);
            if (finalNum < minNum) {
                //由于是整数，每次是递增1，所以如果传入的数字比帧数小，则直接使用setText()
                this.setText(numStr);
                return;
            }
            ValueAnimator intAnimator = new ValueAnimator().ofInt(0, finalNum);
            intAnimator.setDuration(duration);
            intAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentNum = (int) animation.getAnimatedValue();
                    setText(String.valueOf(currentNum));
                }
            });
            intAnimator.start();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            setText(numStr);//如果转换Double失败则直接用setText
        }
    }
}