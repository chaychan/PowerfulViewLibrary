package com.chaychan.powerfulviewlibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chaychan.viewlib.PowerfulEditText;

public class MainActivity extends AppCompatActivity {

    private PowerfulEditText petUsername;
    private PowerfulEditText petPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petUsername = (PowerfulEditText) findViewById(R.id.pet_username);
        petPwd = (PowerfulEditText) findViewById(R.id.pet_pwd);
    }

    public void submit(View view){
        String str = petUsername.getText().toString().trim();
    }
}
