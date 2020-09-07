package com.example.routerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.routerdemo.annotation.Route;

@Route(path = "/main/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}