package com.chaychan.powerfulviewlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaychan.powerfulviewlibrary.R;
import com.chaychan.viewlib.ExpandableLinearLayout;

public class ExpandableLinearLayoutDemoActivity extends AppCompatActivity {

    public static final String USE_DEFAULT_BOTTOM = "useDefaultBottom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean useDefaultBottom = getIntent().getBooleanExtra(USE_DEFAULT_BOTTOM, true);
        if (useDefaultBottom){
            //使用默认底部
            setContentView(R.layout.page_ell_default_bottom_demo);
        }else{
            setContentView(R.layout.page_ell_custom_bottom_demo);
            customeBottomUsage();
        }
    }


    /**
     * 使用自定义底部的用法
     */
    private void customeBottomUsage() {
        final ExpandableLinearLayout ellProduct = (ExpandableLinearLayout) findViewById(R.id.ell_product);
        RelativeLayout rlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        final TextView tvTip =  (TextView) findViewById(R.id.tv_tip);

        //添加数据
        for (int i = 0; i < 4; i++) {
            View view = View.inflate(this, R.layout.item_product, null);
            ellProduct.addItem(view);
        }

        ellProduct.setOnStateChangeListener(new ExpandableLinearLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(boolean isExpanded) {
                if (isExpanded){
                    //展开
                    tvTip.setText("收起");
                }else{
                    tvTip.setText("展开");
                }
            }
        });

        rlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ellProduct.toggle();
            }
        });
    }
}
