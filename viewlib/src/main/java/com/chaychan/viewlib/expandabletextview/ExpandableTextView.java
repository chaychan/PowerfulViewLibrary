
package com.chaychan.viewlib.expandabletextview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaychan.viewlib.R;
import com.chaychan.viewlib.utils.UIUtils;

/**
 * des:可伸展textview
 * Created by xsf
 * on 2016.08.24
 */
public class ExpandableTextView extends LinearLayout implements View.OnClickListener {

    /* 默认最高行数 */
    private static final int MAX_COLLAPSED_LINES = 5;

    /* 默认动画执行时间 */
    private static final int DEFAULT_ANIM_DURATION = 200;

    /*内容textview*/
    protected AlignTextView mTvContent;

    /*展开收起textview*/
    protected TextView mTvExpandCollapse;

    /*是否有重新绘制*/
    private boolean mRelayout;

    /*默认收起*/
    private boolean mCollapsed = true;

    /*展开图片*/
    private Drawable mExpandDrawable;
    /*收起图片*/
    private Drawable mCollapseDrawable;
    /*动画执行时间*/
    private int mAnimationDuration;
    /*是否正在执行动画*/
    private boolean mAnimating;
    /* 展开收起状态回调 */
    private OnExpandStateChangeListener mListener;
    /* listview等列表情况下保存每个item的收起/展开状态 */
    private SparseBooleanArray mCollapsedStatus;
    /* 列表位置 */
    private int mPosition;

    /*设置内容最大行数，超过隐藏*/
    private int mMaxCollapsedLines;

    /*这个linerlayout容器的高度*/
    private int mCollapsedHeight;

    /*内容tv真实高度（含padding）*/
    private int mTextHeightWithMaxLines;

    /*内容tvMarginTopAmndBottom高度*/
    private int mMarginBetweenTxtAndBottom;

    /*内容颜色*/
    private int contentTextColor;
    /*收起展开颜色*/
    private int collapseExpandTextColor;
    /*内容字体大小*/
    private float contentTextSize;
    /*收起展字体大小*/
    private float collapseExpandTextSize;
    /*收起文字*/
    private String textCollapse;
    /*展开文字*/
    private String textExpand;

    /*收起展开位置，默认左边*/
    private int grarity;

    /*收起展开图标位置，默认在右边*/
    private int drawableGrarity;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.");
        }
        super.setOrientation(orientation);
    }
    /**
     * 初始化属性
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        mCollapsedStatus=new SparseBooleanArray();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION);
        mExpandDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_expandDrawable);
        mCollapseDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_collapseDrawable);

        textCollapse = typedArray.getString(R.styleable.ExpandableTextView_textCollapse);
        textExpand = typedArray.getString(R.styleable.ExpandableTextView_textExpand);

        if (mExpandDrawable == null) {
            mExpandDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_green_arrow_up);
        }
        if (mCollapseDrawable == null) {
            mCollapseDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_green_arrow_down);
        }

        if (TextUtils.isEmpty(textCollapse)) {
            textCollapse = getContext().getString(R.string.collapse);
        }
        if (TextUtils.isEmpty(textExpand)) {
            textExpand = getContext().getString(R.string.expand);
        }
        contentTextColor = typedArray.getColor(R.styleable.ExpandableTextView_contentTextColor, ContextCompat.getColor(getContext(), R.color.gray));
        contentTextSize = typedArray.getDimension(R.styleable.ExpandableTextView_contentTextSize, UIUtils.sp2px(getContext(),14));

        collapseExpandTextColor = typedArray.getColor(R.styleable.ExpandableTextView_collapseExpandTextColor, ContextCompat.getColor(getContext(), R.color.main_color));
        collapseExpandTextSize = typedArray.getDimension(R.styleable.ExpandableTextView_collapseExpandTextSize, UIUtils.sp2px(getContext(),14));

        grarity = typedArray.getInt(R.styleable.ExpandableTextView_collapseExpandGrarity, Gravity.LEFT);
        drawableGrarity=typedArray.getInt(R.styleable.ExpandableTextView_drawableGrarity, Gravity.RIGHT);

        typedArray.recycle();
        // enforces vertical orientation
        setOrientation(LinearLayout.VERTICAL);
        // default visibility is gone
        setVisibility(GONE);
    }

    /**
     * 渲染完成时初始化view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    /**
     * 初始化viwe
     */
    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_expand_collapse, this);
        mTvContent = (AlignTextView) findViewById(R.id.expandable_text);
        mTvContent.setOnClickListener(this);
        mTvExpandCollapse = (TextView) findViewById(R.id.expand_collapse);
        setDrawbleAndText();
        mTvExpandCollapse.setOnClickListener(this);

        mTvContent.setTextColor(contentTextColor);
        mTvContent.getPaint().setTextSize(contentTextSize);

        mTvExpandCollapse.setTextColor(collapseExpandTextColor);
        mTvExpandCollapse.getPaint().setTextSize(collapseExpandTextSize);

        //设置收起展开位置：左或者右
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = grarity;
        mTvExpandCollapse.setLayoutParams(lp);
    }
    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (mTvExpandCollapse.getVisibility() != View.VISIBLE) {
            return;
        }
        mCollapsed = !mCollapsed;
        //修改收起/展开图标、文字
        setDrawbleAndText();
        //保存位置状态
        if (mCollapsedStatus != null) {
            mCollapsedStatus.put(mPosition, mCollapsed);
        }
        // 执行展开/收起动画
        mAnimating = true;
        ValueAnimator valueAnimator;
        if (mCollapsed) {
//            mTvContent.setMaxLines(mMaxCollapsedLines);
            valueAnimator = new ValueAnimator().ofInt(getHeight(), mCollapsedHeight);
        } else {
            valueAnimator = new ValueAnimator().ofInt(getHeight(), getHeight() +
                    mTextHeightWithMaxLines - mTvContent.getHeight());
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                mTvContent.setMaxHeight(animatedValue - mMarginBetweenTxtAndBottom);
                getLayoutParams().height = animatedValue;
                requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                // 动画结束后发送结束的信号
                /// clear the animation flag
                mAnimating = false;
                // notify the listener
                if (mListener != null) {
                    mListener.onExpandStateChanged(mTvContent, !mCollapsed);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(mAnimationDuration);
        valueAnimator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当动画还在执行状态时，拦截事件，不让child处理
        return mAnimating;
    }
    /**
     * 重新测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no change, measure and return
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mTvExpandCollapse.setVisibility(View.GONE);
        mTvContent.setMaxLines(Integer.MAX_VALUE);

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果内容真实行数小于等于最大行数，不处理
        if (mTvContent.getLineCount() <= mMaxCollapsedLines) {
            return;
        }
        // 获取内容tv真实高度（含padding）
        mTextHeightWithMaxLines = getRealTextViewHeight(mTvContent);

        // 如果是收起状态，重新设置最大行数
        if (mCollapsed) {
            mTvContent.setMaxLines(mMaxCollapsedLines);
        }
        mTvExpandCollapse.setVisibility(View.VISIBLE);

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTvContent.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mTvContent.getHeight();
                }
            });
            // 保存这个容器的测量高度
            mCollapsedHeight = getMeasuredHeight();
        }
    }
    /**
     * 获取内容tv真实高度（含padding）
     * @param textView
     * @return
     */
    private static int getRealTextViewHeight( TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    /**
     * 设置收起展开图标位置和文字
     */
    private void setDrawbleAndText(){
        if(Gravity.LEFT==drawableGrarity){
            mTvExpandCollapse.setCompoundDrawablesWithIntrinsicBounds(mCollapsed ? mCollapseDrawable : mExpandDrawable,null,null,null);
        }else{
            mTvExpandCollapse.setCompoundDrawablesWithIntrinsicBounds(null,null,mCollapsed ? mCollapseDrawable : mExpandDrawable,null);
        }
        mTvExpandCollapse.setText(mCollapsed ? getResources().getString(R.string.expand) : getResources().getString(R.string.collapse));
    }


    /*********暴露给外部调用方法***********/

    /**
     * 设置收起/展开监听
     * @param listener
     */
    public void setOnExpandStateChangeListener( OnExpandStateChangeListener listener) {
        mListener = listener;
    }

    /**
     * 设置内容
     * @param text
     */
    public void setText( CharSequence text) {
        mRelayout = true;
        mTvContent.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置内容，列表情况下，带有保存位置收起/展开状态
     * @param text
     * @param position
     */
    public void setText( CharSequence text,int position) {
        mPosition = position;
        //获取状态，如无，默认是true:收起
        mCollapsed = mCollapsedStatus.get(position, true);
        clearAnimation();
        //设置收起/展开图标和文字
        setDrawbleAndText();
        mTvExpandCollapse.setText(mCollapsed ? getResources().getString(R.string.expand) : getResources().getString(R.string.collapse));

        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    /**
     * 获取内容
     * @return
     */
    public CharSequence getText() {
        if (mTvContent == null) {
            return "";
        }
        return mTvContent.getText();
    }

    /**
     * 定义状态改变接口
     */
    public interface OnExpandStateChangeListener {
        /**
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);
    }
}