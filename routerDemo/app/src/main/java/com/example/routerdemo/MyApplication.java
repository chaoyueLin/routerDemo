package com.example.routerdemo;

import android.app.Application;

/*****************************************************************
 * * File: - MyApplication
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/8    1.0         create
 ******************************************************************/
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CRouter.init(this);
    }
}
