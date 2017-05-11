package com.chaychan.powerfulviewlibrary.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chaychan.powerfulviewlibrary.bean.DemoBean;

public class MainActivity extends ListActivity {

    private DemoBean[] mDatas= {
            new DemoBean("PowerfulEditTextDemo",PowerfulEditTextDemoActivity.class),
            new DemoBean("ExpandableTextViewDemo",ExpandableTextViewDemoActivity.class),
            new DemoBean("PieChartViewDemo",PieChartViewDemoActivity.class),
            new DemoBean("RunningTextViewDemo",RunningTextViewDemoActivity.class),
            new DemoBean("ExpandableLinearLayoutDemo",ExpandableLinearLayoutDemoActivity.class)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<DemoBean> adapter = new ArrayAdapter<DemoBean>(this, android.R.layout.simple_list_item_1, mDatas);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //为条目设置点击事件
        DemoBean dataBean = mDatas[position];
        startActivity(new Intent(this,dataBean.clazz));//跳转到对应的activity
    }
}
