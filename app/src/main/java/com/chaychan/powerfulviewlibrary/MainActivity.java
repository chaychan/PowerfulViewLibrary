package com.chaychan.powerfulviewlibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chaychan.viewlib.ExpandableLinearLayout;
import com.chaychan.viewlib.PowerfulEditText;
import com.chaychan.viewlib.expandabletextview.ExpandableTextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PowerfulEditText petUsername = (PowerfulEditText) findViewById(R.id.pet);
        petUsername.setOnRightClickListener(new PowerfulEditText.OnRightClickListener() {
            @Override
            public void onClick(EditText editText) {
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)){
                    Toast.makeText(MainActivity.this, "执行搜索逻辑", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ExpandableTextView etv = (ExpandableTextView) findViewById(R.id.tv_intro);
        etv.setText("啊哈可接受的看哈市的哈数据库和的看就好撒看就,好将卡上的空间哈看就,好啊数据库和的空间哈市科空间哈市科技很多借口撒谎的健康哈数据库空间哈市科技很多借口撒谎的健康哈数据库空间哈市科技很多借口撒谎的健康哈数据库空间哈市科技很多借口撒谎的健康哈数据库技很多借口撒谎的健康哈数据库将撒谎的健康哈啊市科技哈看就好啊数据库的哈看将撒谎空间的哈市科技哈看将");

        View view1 = View.inflate(this, R.layout.item_expandable_linear_layout, null);
        View view2 = View.inflate(this, R.layout.item_expandable_linear_layout, null);
        View view3 = View.inflate(this, R.layout.item_expandable_linear_layout, null);
        View view4 = View.inflate(this, R.layout.item_expandable_linear_layout, null);

        ExpandableLinearLayout expandableLinearLayout = (ExpandableLinearLayout) findViewById(R.id.ell);

        expandableLinearLayout.addItem(view1);
        expandableLinearLayout.addItem(view2);
        expandableLinearLayout.addItem(view3);
        expandableLinearLayout.addItem(view4);
    }
}
