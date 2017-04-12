package com.chaychan.viewlib.piechartview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chaychan.viewlib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 可点击的扇形图<br/>
 * Created on 2016/3/23
 *
 * @author yekangqi
 */
public class PieChartView extends View {
    private static final String TAG="PieChartView";
    private static final int Gravity_TOP=0;
    private static final int Gravity_CENTRE=1;
    private static final int Gravity_FIXXY=2;
    private RectF FanRectF;//主绘图区域
    private RectF offsetRectF;//偏移的区域
    private Paint p ;//画笔
    private PointF centre;//圆心
    private PointF startPoint;//开始画扇形的开始点
    private float radius;//外圆的半径
    private float centreRadius;//内圆半径,不可点击
    private int firstOffset= 0;//第一个扇形的偏移值(突出效果)
    private List<FanItem> datas;//扇形Item数据
    private OnFanItemClickListener onFanClick;//单击回调
    private boolean isFistOffSet;//是否 第一个扇形的偏移值
    private double SO=-1;//左边到圆心距离
    private int Gravity=Gravity_TOP;//重力
    public PieChartView(Context context) {
        super(context);
        init(context,null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs)
    {
        int lableTextSize=0;
        if (null!=context && null!=attrs)
        {
            TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
            Gravity=ta.getInt(R.styleable.PieChartView_gravity, Gravity_TOP);
            centreRadius=ta.getDimensionPixelSize(R.styleable.PieChartView_centreRadius, 0);
            firstOffset=ta.getDimensionPixelSize(R.styleable.PieChartView_firstOffset,0);
            lableTextSize=ta.getDimensionPixelSize(R.styleable.PieChartView_lableTextSize,0);
            ta.recycle();
        }

        // 创建画笔
        p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(lableTextSize);
        p.setTextAlign(Paint.Align.CENTER);

        setIsFistOffSet(firstOffset>0);
        if (isInEditMode())
        {
            p.setTextSize(32);
            datas=new ArrayList<>();
            datas.add(new FanItem(1,75, Color.GRAY));
            datas.add(new FanItem(2,15, Color.GREEN));
            datas.add(new FanItem(3,60, Color.DKGRAY));
            datas.add(new FanItem(4, 25, Color.GREEN));
            datas.add(new FanItem(5, 90, Color.BLUE));
            initDatas(datas);
        }
    }

    public void setDatas(List<FanItem> datas) {
        this.datas = datas;
        initDatas(this.datas);
    }

    public List<FanItem> getDatas() {
        return datas;
    }

    /**
     * 设置是否偏移
     * @param isFistOffSet
     */
    public void setIsFistOffSet(boolean isFistOffSet) {
        this.isFistOffSet = isFistOffSet;
    }

    /**
     * 获取绘制矩形
     * @return
     */
    public RectF getFanRectF() {
        return FanRectF;
    }

    /**
     * 设置可点击的扇形数据
     * @param datas 数据源
     * @param productColors 颜色
     * @param minClickAbleAreaPercent 可点击的最小百分比(如果比这百分比小就用这个的)
     */
    public void setFanClickAbleData(double[] datas, int[] productColors,double minClickAbleAreaPercent)
    {
        if (null==datas || datas.length>productColors.length)
        {
            return;
        }
        float totalAll=0f;
        for (double value :datas) {
            totalAll+=value;
        }
        double blankValue=totalAll*minClickAbleAreaPercent;
        List<FanItem> itemList=new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {
            //修正点击范围
            Double value=datas[i];
            if (value<blankValue)
            {
                value=blankValue;
            }
            FanItem item = new FanItem(i, Float.parseFloat(value.toString()),productColors[i]);
            item.setRealValue((float) datas[i]);
            item.setPercent((int) (100*datas[i]/totalAll));
            itemList.add(item);
        }
        setDatas(itemList);
        postInvalidate();
    }
    /**
     * 移动到第一个
     * @param first
     */
    public void setToFirst(FanItem first)
    {
        if (null!=first && null!=datas && first.getIndex() !=0)
        {
            Log.d(TAG, "排序前" + datas.toString());
            List<FanItem> afterList=datas.subList(first.getIndex(),datas.size());
            List<FanItem> beforeList=datas.subList(0, first.getIndex());

            this.datas=new ArrayList<>();
            for (int i = 0; i < afterList.size(); i++) {
                datas.add(afterList.get(i));
            }
            for (int i = 0; i < beforeList.size(); i++) {
                datas.add(beforeList.get(i));
            }
            initDatas(datas);
            Log.d(TAG, "排序后" + datas.toString());
        }
    }

    /**
     * 初始化角度数据
     * @param datas
     */
    private void initDatas(List<FanItem> datas)
    {
        if (null==datas)
        {
            return;
        }
        float total=0;
        for (int i = 0; i <datas.size(); i++) {
            total+= datas.get(i).getValue();
        }
        float startAngle=-1;
        for (int i = 0; i < datas.size(); i++) {
            FanItem f=datas.get(i);
            f.setIndex(i);
            f.setAngle(360*(f.getValue() /total));
            //第一个开始
            if (-1==startAngle)
            {
                startAngle=90- f.getAngle() /2;
            }
            if (startAngle<0)
            {
                startAngle=startAngle+360;
            }else if (startAngle>360)
            {
                startAngle=startAngle-360;
            }
            f.setStartAngle(startAngle);
            startAngle=startAngle+ f.getAngle();
        }
    }

    /**
     * 扇形的绘图信息
     */
    private void initFanDrawAbleInfo()
    {
        if (null== FanRectF)
        {
            switch (Gravity)
            {
                case Gravity_TOP:
                {
                    FanRectF = new RectF(0,0, getWidth(), getWidth());
                    break;
                }
                case Gravity_CENTRE:
                {
                    int w=getWidth();
                    int h=getHeight();
                    if (w==h)
                    {
                        FanRectF = new RectF(0,0,w, w);
                    }else if (h>w)
                    {
                        FanRectF = new RectF(0,(h-w)/2,w, w);
                    }else if (w>h)
                    {
                        FanRectF = new RectF((w-h)/2,0,h, h);
                    }
                    break;
                }
                case Gravity_FIXXY:
                {
                    FanRectF = new RectF(0,0,getWidth(), getHeight());
                    break;
                }
            }

        }
        if (null==offsetRectF)
        {
            offsetRectF =new RectF(0,firstOffset, FanRectF.width(), FanRectF.height()+firstOffset);
        }
        if (null==centre) {
            centre=new PointF(FanRectF.centerX(), FanRectF.centerY());
        }
        if (null==startPoint)
        {
            startPoint=new PointF(FanRectF.width(), FanRectF.height()/2);
        }
        if (SO==-1)
        {
            SO= getDistance(startPoint,centre);
        }
        radius = FanRectF.width()/2;
    }

    /**
     * 在扇形的对角线中间绘制文字
     * @param canvas
     * @param item
     * @param txtCentre
     */
    private void drawText(Canvas canvas, RectF txtCentre, FanItem item)
    {
        float angle=(item.getStartAngle() + item.getAngle() + item.getStartAngle())/2;
        Log.d(TAG, "开始 :" + item.getStartAngle() + " 结束:" + (item.getStartAngle() + item.getAngle()) + " 中心:" + angle);
        String str= item.getPercent()+"%";
        Rect txtRect = new Rect();
        p.getTextBounds(str, 0, str.length(), txtRect);
        float cenX= (float) (txtCentre.centerX()+(radius-txtRect.height()-txtRect.width())*Math.cos(Math.toRadians(angle)));
        float cexY= (float) (txtCentre.centerY()+(radius-txtRect.height()-txtRect.width())*Math.sin(Math.toRadians(angle)));
        p.setColor(Color.WHITE);
        canvas.drawText(str, cenX, cexY, p);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null==datas)
        {
            return;
        }
        initFanDrawAbleInfo();
        for (int i = 0; i < datas.size(); i++) {
            RectF drawRect;
            if (isFistOffSet && 0==i && datas.size()>=3)
            {
                drawRect=offsetRectF;
            }else
            {
                drawRect= FanRectF;
            }
            FanItem item=datas.get(i);
            p.setColor(item.getColor());
            canvas.drawArc(drawRect, item.getStartAngle(), item.getAngle(), true, p);
            drawText(canvas, drawRect, item);
        }
    }


    private static long lastClickTime;//防止快速点击
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.d(TAG,"*******ACTION_UP*******");
                if (null!=centre && null!=datas && null!=onFanClick && System.currentTimeMillis()-lastClickTime>500)
                {
                    PointF A=new PointF(e.getX(),e.getY());
                    double AO=getDistance(A,centre);//A到圆心
                    if (AO<=SO && AO>centreRadius)//在环内
                    {
                        double AS=getDistance(A,startPoint);//A到开始点
                        double angle=Math.toDegrees(Math.acos((AO*AO+SO*SO-AS*AS)/(2*AO*SO)));
                        //处理优角
                        if (A.y<startPoint.y)
                        {
                            angle=360-angle;
                        }
                        Log.d(TAG, "点击的angle:" + angle);
                        //验证角度在哪个扇形里面
                        for (int i = 0; i < datas.size(); i++) {
                            FanItem f=datas.get(i);
                            double endAngle= f.getStartAngle() + f.getAngle();
                            boolean isClick=false;
                            if (endAngle<=360)
                            {
                                isClick=angle>= f.getStartAngle() && angle<=endAngle;
                            }else
                            {
                                double after360=endAngle-360;
                                if (angle>= f.getStartAngle() && angle<=360)
                                {
                                    isClick=true;
                                }
                                if(angle>=0 && angle<=after360)
                                {
                                    isClick=true;
                                }
                            }
                            if (isClick)
                            {
                                onFanClick.onFanClick(f);
                                break;
                            }
                        }
                        lastClickTime=System.currentTimeMillis();
                    }
                }
                break;
            }
        }
        return super.onTouchEvent(e);
    }

    public OnFanItemClickListener getOnFanClick() {
        return onFanClick;
    }

    public void setOnFanClick(OnFanItemClickListener onFanClick) {
        this.onFanClick = onFanClick;
    }

    /**
     * 两点之间的距离
     * @param a
     * @param b
     * @return
     */
    private double getDistance(PointF a,PointF b)
    {
        return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
    }



}
