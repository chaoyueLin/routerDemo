package com.example.routerdemo;

import android.app.Application;
import android.util.Log;

import com.example.routerdemo.annotation.IEntry;
import com.example.routerdemo.annotation.IRouteGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*****************************************************************
 * * File: - CRouter
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/7
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/7    1.0         create
 ******************************************************************/
public class CRouter {
    private static final String TAG = "CRouter";
    private static HashMap<String, Class<? extends IRouteGroup>> map = new HashMap();

    private CRouter() {
    }

    private static class SingleHolder {
        private static CRouter INSTANCE = new CRouter();
    }

    public CRouter getInstance() {
        return SingleHolder.INSTANCE;
    }

    //初始化路由，加载路由表入口文件至内存，具体路由文件在使用的时候再加载
    public static void init(Application context) {
        loadIntoEntries(context);
        for (Map.Entry<String, Class<? extends IRouteGroup>> entry : map.entrySet()) {
            Log.e(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue().getName());
        }
    }

    //加载路由表入口文件至内存
    private static void loadIntoEntries(Application context) {
        try {
            Set<String> routeFiles = ClassUtils.getFileNameByPackageName(context, "com.example.routerdemo");
            if (routeFiles != null && routeFiles.size() > 0) {
                for (String fileName : routeFiles) {
                    if (fileName.startsWith("com.example.routerdemo.router_entry_")) {
                        ((IEntry) Class.forName(fileName).newInstance()).loadRouteEntry(map);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "loadIntoEntries fail");
        }
    }
}
