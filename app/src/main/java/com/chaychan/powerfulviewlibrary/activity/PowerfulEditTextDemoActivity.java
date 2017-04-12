package com.chaychan.powerfulviewlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.chaychan.powerfulviewlibrary.R;
import com.chaychan.powerfulviewlibrary.utils.UIUtils;
import com.chaychan.viewlib.PowerfulEditText;

/**
 * PowfulEditText演示
 */
public class PowerfulEditTextDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powerful_et_demo);

        PowerfulEditText petUsername = (PowerfulEditText) findViewById(R.id.pet);
        petUsername.setOnRightClickListener(new PowerfulEditText.OnRightClickListener() {
            @Override
            public void onClick(EditText editText) {
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    UIUtils.showToast("搜索的内容不能为空");
                    return;
                }
               UIUtils.showToast("执行搜索逻辑");
            }
        });
    }
}
