package com.chaychan.viewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chaychan.viewlib.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数字滚动的textView
 */
public class NumberRunningTextView extends TextView {

    private static final int MONEY_TYPE = 0;
    private static final int NUM_TYPE = 1;

    private int frameNum;// 总共跳跃的帧数,默认30跳
    private int textType;//内容的类型，默认是金钱类型
    private boolean useCommaFormat;//是否使用每三位数字一个逗号的格式，让数字显得比较好看，默认使用
    private boolean runWhenChange;//是否当内容有改变才使用动画,默认是

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);// 1个线程的线程池;
    private DecimalFormat formatter = new DecimalFormat("0.00");// 格式化金额，保留两位小数

    private double nowMoneyNum = 0.00;// 记录每帧增加后的金额数字
    private double finalMoneyNum;// 目标金额数字（最终的金额数字）

    private int nowNum;//记录每帧增加后的数字
    private int finalNum;//目标数字（最终的数字）
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
        frameNum = ta.getInt(R.styleable.NumberRunningTextView_frameNum, 30);
        textType = ta.getInt(R.styleable.NumberRunningTextView_textType, MONEY_TYPE);
        useCommaFormat = ta.getBoolean(R.styleable.NumberRunningTextView_useCommaFormat, true);
        runWhenChange = ta.getBoolean(R.styleable.NumberRunningTextView_runWhenChange,true);

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
        } else {
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
            finalMoneyNum = Double.parseDouble(money);
            if (finalMoneyNum == 0) {
                //如果传入的为0，则直接使用setText()
                NumberRunningTextView.this.setText(moneyStr);
                return;
            }
            nowMoneyNum = 0;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    double size = finalMoneyNum / frameNum;//数字除以帧数，得到每帧跳跃的大小
                    msg.what = MONEY_TYPE;
                    msg.obj = size < 0.01 ? 0.01 : size;// 如果每帧的间隔比0.01小，就设置为0.01
                    handler.sendMessage(msg);// 发送通知改变UI
                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
            NumberRunningTextView.this.setText(moneyStr);//如果转换Double失败则直接用setText
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
            finalNum = Integer.parseInt(num);
            if (finalNum < frameNum) {
                //由于是整数，每次是递增1，所以如果传入的数字比帧数小，则直接使用setText()
                NumberRunningTextView.this.setText(numStr);
                return;
            }
            nowNum = 0;// 默认都是从0开始动画
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    int temp = finalNum / frameNum;//数字除以帧数，得到每帧跳跃的大小
                    msg.what = NUM_TYPE;
                    msg.obj = temp;
                    handler.sendMessage(msg);// 发送通知改变UI
                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
            NumberRunningTextView.this.setText(numStr);//如果转换Double失败则直接用setText
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MONEY_TYPE://金钱数字的滚动
                    String str = formatter.format(nowMoneyNum).toString();//保留两位小数的字符串

                    // 更新显示的内容
                    if (useCommaFormat) {
                        //使用每三位数字一个逗号的格式
                        String formatStr = StringUtils.addComma(str);//三位一个逗号格式的字符串
                        NumberRunningTextView.this.setText(formatStr);
                    } else {
                        NumberRunningTextView.this.setText(str);
                    }

                    nowMoneyNum += (double) msg.obj;//记录当前每帧递增后的数字

                    if (nowMoneyNum < finalMoneyNum) {
                        //如果当前记录的金额数字小于目标金额数字，即还没达到目标金额数字，继续递增
                        Message msg2 = handler.obtainMessage();
                        msg2.what = MONEY_TYPE;
                        msg2.obj = msg.obj;
                        handler.sendMessage(msg2);// 继续发送通知改变UI
                    } else {
                        //已经达到目标的金额数字，显示最终的数字
                        if (useCommaFormat) {
                            NumberRunningTextView.this.setText(StringUtils.addComma(formatter.format(finalMoneyNum)));
                        } else {
                            NumberRunningTextView.this.setText(formatter.format(finalMoneyNum));
                        }
                    }
                    break;

                case NUM_TYPE://普通数字滚动
                    NumberRunningTextView.this.setText(String.valueOf(nowNum));
                    nowNum += (Integer) msg.obj;//记录当前每帧递增后的数字
                    if (nowNum < finalNum) {
                        //如果当前记录的数字小于目标数字，即还没达到目标数字，继续递增
                        Message msg2 = handler.obtainMessage();
                        msg2.what = NUM_TYPE;
                        msg2.obj = msg.obj;
                        handler.sendMessage(msg2);// 继续发送通知改变UI
                    } else {
                        //已经达到目标的数字，显示最终的内容
                        NumberRunningTextView.this.setText(String.valueOf(finalNum));
                    }
                    break;
            }
        }
    };
}