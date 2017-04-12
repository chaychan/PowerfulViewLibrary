package com.chaychan.viewlib.piechartview;

/**
 * 扇形Item数据<br/>
 * Created on 2016/4/22
 *
 * @author yekangqi
 */
public  class FanItem{
    private int id;
    //数值,跟显示有关
    private float value;
    //实际上的数据
    private float realValue;
    //百分比
    private int percent;
    //颜色
    private int color;
    //角度
    private float angle;
    //开始角度
    private float startAngle;
    //顺序
    private int index;

    public FanItem(int id, float value, int color) {
        this.setId(id);
        this.setValue(value);
        this.setColor(color);
    }

    @Override
    public String toString() {
        return "FanItem{" +
                "id=" + getId() +
                ", angle=" + getAngle() +
                ", startAngle=" + getStartAngle() +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getRealValue() {
        return realValue;
    }

    public void setRealValue(float realValue) {
        this.realValue = realValue;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
