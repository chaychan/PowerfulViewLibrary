package com.chaychan.powerfulviewlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chaychan.powerfulviewlibrary.R;
import com.chaychan.viewlib.ExpandableLinearLayout;

public class ExpandableLinearLayoutDemoActivity extends AppCompatActivity {

    private ExpandableLinearLayout ellProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_linear_layout_demo);

        ellProduct = (ExpandableLinearLayout) findViewById(R.id.ell_product);
        for (int i = 0; i < 9; i++) {
            View view = View.inflate(this, R.layout.item_product, null);
            ellProduct.addItem(view);
        }
    }
}
