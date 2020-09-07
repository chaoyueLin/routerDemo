package com.example.routerdemo.annotation;

import com.example.routerdemo.annotation.IRouteGroup;

import java.util.Map;

/*****************************************************************
 * * File: - IEntry
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/7
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/7    1.0         create
 ******************************************************************/
public interface IEntry {
    // 加载路由入口文件
    // 比如：entries.put("main", JRouterTest_Group_main.class);其中JRouterTest_Group_main是group为main的路由表文件
    void loadRouteEntry(Map<String, Class<? extends IRouteGroup>> entries);
}
