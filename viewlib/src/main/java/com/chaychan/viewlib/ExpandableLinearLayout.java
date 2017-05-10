package com.chaychan.viewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaychan.viewlib.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * 可以展开的LinearLayout
 */
public class ExpandableLinearLayout extends FrameLayout implements View.OnClickListener {

    private LinearLayout llContainer;
    private TextView tvTip;

    private boolean isExpand = false;//是否是展开状态，默认是隐藏

    private int defaultItemCount;//一开始展示的条目数
    private String expandText;//展开时显示的文字
    private String hideText;//隐藏时显示的文字
    private ImageView ivArrow;
    private RelativeLayout rlBottom;

    public ExpandableLinearLayout(Context context) {
        this(context, null);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLinearLayout);
        defaultItemCount = ta.getInt(R.styleable.ExpandableLinearLayout_defaultItemCount, 0);
        expandText = ta.getString(R.styleable.ExpandableLinearLayout_expandText);
        hideText = ta.getString(R.styleable.ExpandableLinearLayout_hideText);
        float fontSize = ta.getDimension(R.styleable.ExpandableLinearLayout_tipTextSize, UIUtils.sp2px(context,14));
        int textColor = ta.getColor(R.styleable.ExpandableLinearLayout_tipTextColor, Color.parseColor("#666666"));
        ta.recycle();


        View rootView = View.inflate(context, R.layout.expandable_linearlayout, null);
        llContainer = (LinearLayout) rootView.findViewById(R.id.ll_container);
        rlBottom = (RelativeLayout) rootView.findViewById(R.id.rl_bottom);
        ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);

        tvTip = (TextView) rootView.findViewById(R.id.tv_tip);
        tvTip.getPaint().setTextSize(fontSize);
        tvTip.setTextColor(textColor);

        rlBottom.setOnClickListener(this);

        addView(rootView);
    }


    public void addItem(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llContainer.addView(view,params);
        refreshUI();
    }

    /**
     * 刷新UI
     */
    private void refreshUI() {
        int childCount = llContainer.getChildCount();
        rlBottom.setVisibility(childCount > defaultItemCount ? VISIBLE : GONE);//控制隐显
        if (childCount > defaultItemCount) {
            hide(false);
        }
    }

    /**
     * 展开
     */
    private void expand() {
        int childCount = llContainer.getChildCount();
        //如果含有条目数大于默认显示的条目数，则可以展开,改变linearLayout的高度
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llContainer.getLayoutParams();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View view = llContainer.getChildAt(i);

            int viewWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int viewHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            view.measure(viewWidth, viewHeight);
            height += view.getMeasuredHeight();
        }

        doAnimation(params.height,height);//执行动画
    }

    /**
     * 收起
     */
    private void hide(boolean withAnimation) {
        //改变linearLayout的高度为defaultItemCount个条目的高度
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llContainer.getLayoutParams();
        int height = 0;
        for (int i = 0; i < defaultItemCount; i++) {
            View view = llContainer.getChildAt(i);

            int viewWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int viewHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            view.measure(viewWidth, viewHeight);
            height += view.getMeasuredHeight();
        }

        if (withAnimation){
            doAnimation(params.height, height);//执行动画
        }else{
            params.height = height;
            llContainer.setLayoutParams(params);
        }
    }


    private void doAnimation(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);// 由初始值向结束值变化
        animator.setDuration(300);// 设置周期

        // 设置监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            // 每次值变化后会回调此方法
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int height = (int) animator.getAnimatedValue();
                // 重新设置LineartLayout的高度从而实现动画效果
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llContainer.getLayoutParams();
                params.height = height;
                llContainer.setLayoutParams(params);
            }
        });


        //动画执行后滚动至底部,设置监听
        animator.addListener(new Animator.AnimatorListener() {

            //动画结束后回调
            @Override
            public void onAnimationEnd(Animator animator) {
                 if (animationFinishListener != null){
                     animationFinishListener.onFinish();
                 }
            }

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }
        });


        animator.start();
    }

    // 箭头的动画
    private void doArrowAnim() {
        if (isExpand) {
            // 当前是展开，将执行收起，箭头由上变为下
            ObjectAnimator.ofFloat(ivArrow, "rotation", -180, 0).start();
        } else {
            // 当前是收起，将执行展开，箭头由下变为上
            ObjectAnimator.ofFloat(ivArrow, "rotation", 0, 180).start();
        }
    }

    @Override
    public void onClick(View v) {
        if (isExpand) {
            hide(true);
            tvTip.setText(hideText);
        } else {
            expand();
            tvTip.setText(expandText);
        }
        doArrowAnim();
        isExpand = !isExpand;
    }

    private AnimationFinishListener animationFinishListener;

    public void setOnAnimationFinishListener(AnimationFinishListener animationFinishListener) {
        this.animationFinishListener = animationFinishListener;
    }

    /**动画完成后的监听*/
    public interface AnimationFinishListener {
        void onFinish();
    }
}
