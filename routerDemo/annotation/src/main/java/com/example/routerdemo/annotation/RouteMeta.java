package com.example.routerdemo.annotation;

import javax.lang.model.element.Element;

/*****************************************************************
 * * File: - RouteMeta
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/7
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/7    1.0         create
 ******************************************************************/
public class RouteMeta {
    private String path; //路由
    private String group; //路由分组名，路由为/xxx/yyy，则xxx作为分组名
    private Class<?> destination; //路由跳转的目标类
    private Element rawType;//编译时注解的元素 - 即被注解的元素：class
    private TypeEnum type;//注解的目标类的类型，比如avtivity，fragment等

    public RouteMeta() {
    }

    public RouteMeta(String path, String group, Element rawType, TypeEnum type) {
        this.path = path;
        this.group = group;
        this.rawType = rawType;
        this.type = type;
    }

    public RouteMeta(String path, String group, Class<?> destination, TypeEnum type) {
        this.path = path;
        this.group = group;
        this.destination = destination;
        this.type = type;
    }


    public static RouteMeta build(String path, String group, Class<?> destination, TypeEnum type) {
        return new RouteMeta(path, group, destination, type);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
