package com.example.routerdemo.annotation;

import java.util.Map;

/*****************************************************************
 * * File: - IRouteGroup
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/7
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/7    1.0         create
 ******************************************************************/
public interface IRouteGroup {
    // 加载对应的分组路由信息
    void loadRouteInfo(Map<String, RouteMeta> routes);
}
