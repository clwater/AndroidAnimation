package com.clwater.animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomSunView cv_main = findViewById(R.id.cv_main);
        cv_main.setText("测试");
        cv_main.start();
    }
}