package com.example.routerdemo.annotation_complite;

import com.example.routerdemo.annotation.Route;
import com.example.routerdemo.annotation.RouteMeta;
import com.example.routerdemo.annotation.TypeEnum;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/*****************************************************************
 * * File: - RouterProcessor
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/9/7
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/9/7    1.0         create
 ******************************************************************/
@AutoService(Processor.class)
@SupportedOptions({"moudleName"})
public class RouterProcessor extends AbstractProcessor {
    private Messager log;
    private Elements elements;
    private Types types;
    private Filer filer;


    private Map<String, Set<RouteMeta>> group;//路由分组
    private Map<String, String> entries;//路由入口

    private TypeMirror typeMirrorActivity;
    private TypeMirror typeMirrorAppCompatActivity;
    private TypeMirror typeMirrorFragmentV4;
    private TypeMirror typeMirrorFragment;
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();

        typeMirrorActivity = elements.getTypeElement(Const.ACTIVITY).asType();
        typeMirrorAppCompatActivity = elements.getTypeElement(Const.APPCOMPATACTIVITY).asType();
        typeMirrorFragmentV4 = elements.getTypeElement(Const.FRAGMENT_V4).asType();
        typeMirrorFragment = elements.getTypeElement(Const.FRAGMENT).asType();

        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get("moudleName");
        }
        if (moduleName == null || moduleName.length() == 0) {
            log.printMessage(Diagnostic.Kind.NOTE, "moduleName can't be null or empty");
            throw new RuntimeException("moduleName can't be null or empty");
        }
        group = new HashMap<>();
        entries = new HashMap<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Route.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations == null || annotations.isEmpty()) {
            log.printMessage(Diagnostic.Kind.NOTE, "process set is empty");
            return false;
        }

        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (routeElements == null || routeElements.isEmpty()) {
            log.printMessage(Diagnostic.Kind.NOTE, "process roundEnvironment is empty");
            return false;
        }
        //提取&分组路由信息
        extractAndGroupRouteInfo(routeElements);
        // 生成路由表文件
        if (group == null || group.size() == 0) {//无路由表信息
            return false;
        }
        generateRouteGroupFile();
        return true;
    }
    //public class app_Group_main implements IRouteGroup {
    //  public void loadRouteInfo(Map<String, RouteMeta> routes) {
    //    routes.put("/main/mainactivity",RouteMeta.build("/main/mainactivity","main",MainActivity.class,TypeEnum.ACTIVITY));
    //  }
    //}
    private void generateRouteGroupFile() {
        //1、生成参数类型:Map<String, RouteMeta>;
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        //2、生成参数：routes
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //3、遍历路由分组（Map<String, Set<RouteMeta>> group），生成路由表文件
        for (Map.Entry<String, Set<RouteMeta>> routeEntry : group.entrySet()) {
            String groupName = routeEntry.getKey();
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("loadRouteInfo")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec);
            for (RouteMeta routeMeta : routeEntry.getValue()) {//遍历&添加统一分组下的路由信息
                // routes.put("/main/mainactivity", RouteMeta.build(path, group, destination, rawType, type))
                methodSpec.addStatement("routes.put($S,$T.build($S,$S,$T.class,$T." + routeMeta.getType() + "))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        routeMeta.getPath(),
                        routeMeta.getGroup(),
                        ClassName.get(routeMeta.getRawType().asType()),
                        ClassName.get(TypeEnum.class));
            }
            //路由文件名
            String groupFileName = moduleName + "_Group_" + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupFileName)
                    .addSuperinterface(ClassName.get("com.example.routerdemo.annotation", "IRouteGroup"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec.build())
                    .build();
            JavaFile javaFile = JavaFile.builder("com.example.routerdemo", typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (Exception e) {
                log.printMessage(Diagnostic.Kind.ERROR, "generate route group file exception");
            }
        }
    }

    private void extractAndGroupRouteInfo(Set<? extends Element> routeElements) {
        log.printMessage(Diagnostic.Kind.NOTE, "start to classify route info");
        for (Element ele : routeElements) {
            TypeMirror annoEle = ele.asType();
            if (ele.getKind() == ElementKind.CLASS) {
                Route route = ele.getAnnotation(Route.class);
                String path = route.path();
                String groupName = extractGroupFromPath(path);
                RouteMeta routeMeta = new RouteMeta(path, groupName, ele, getElementKind(annoEle));//封装路由信息
                Set<RouteMeta> groupRoutes = group.get(groupName);
                if (groupRoutes == null) {
                    groupRoutes = new HashSet<>();
                    groupRoutes.add(routeMeta);
                    group.put(groupName, groupRoutes); // 路由分组
                } else {
                    groupRoutes.add(routeMeta);
                }
            }
        }
        log.printMessage(Diagnostic.Kind.NOTE, "classify route info  -- finish -- group size is " + group.size());
        log.printMessage(Diagnostic.Kind.NOTE, "start to generate route group file");
    }

    private String extractGroupFromPath(String path) {
        if (TextUtil.isEmpty(path) || !path.startsWith("/")) {
            return null;
        }
        // 截取group
        String groupName = path.substring(1, path.indexOf("/", 1));
        return TextUtil.isEmpty(groupName) ? "" : groupName;
    }

    // 获取被注解的元素的类型
    private TypeEnum getElementKind(TypeMirror typeMirror) {
        if (types.isSubtype(typeMirror, typeMirrorActivity)) {
            return TypeEnum.ACTIVITY;
        } else if (types.isSubtype(typeMirror, typeMirrorFragment)) {
            return TypeEnum.FRAGMENT;
        } else if (types.isSubtype(typeMirror, typeMirrorFragmentV4)) {
            return TypeEnum.FRAGMENTV4;
        } else if (types.isSubtype(typeMirror, typeMirrorAppCompatActivity)) {
            return TypeEnum.ACTIVITY;
        }
        return TypeEnum.DEFAULT;
    }
}
