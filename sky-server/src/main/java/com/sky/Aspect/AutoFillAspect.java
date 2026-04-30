package com.sky.Aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoPointCut(){}

//    @Before("autoPointCut()")
//    public void autoFill(JoinPoint joinPoint){
//        log.info("开始进行公共字段填充..............");
//
//        //获取签名对象
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//
//        //获取注解的对象的值
//        OperationType value = signature.getMethod().getAnnotation(AutoFill.class).value();
//
//        //当前方法的实体对象的参数
//        Object[] args = joinPoint.getArgs();
//        if(args == null || args.length == 0){return;}
//        Object entity = args[0];
//        //准备插入数据
//        LocalDateTime now = LocalDateTime.now();
//        Long currentId = BaseContext.getCurrentId();
//
//        //通过反射赖和字段名赖对对应的方法进行插入数据
//        try {
//            if(value == OperationType.INSERT){
//                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
//                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
//
//                setCreateTime.invoke(entity, now);
//                setCreateUser.invoke(entity, currentId);
//            }
//            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
//            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//
//            setUpdateTime.invoke(entity, now);
//            setUpdateUser.invoke(entity, currentId);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 填充创建时间和更新时间以及创建人、更新人,版本2,防止传入list集合时没有对数组进行便利
     * @param joinPoint
     */
    @Before("autoPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充..............");

        //获取签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //获取注解的对象的值
        OperationType value = signature.getMethod().getAnnotation(AutoFill.class).value();

        //当前方法的实体对象的参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){return;}
        Object entity = args[0];
        //准备插入数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //通过反射来和字段名来对对应的方法进行插入数据
        try {
            if(entity instanceof List){
                List<Object> list = (List<Object>) entity;
                for (Object item : list) {
                    doFill(item, value, now, currentId);
                }
            } else {
                doFill(entity, value, now, currentId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doFill(Object entity, OperationType value, LocalDateTime now, Long currentId) {
        try {
            if(value == OperationType.INSERT){
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
            }
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        } catch (Exception e) {
            log.debug("实体类 {} 缺少自动填充字段，已忽略", entity.getClass().getSimpleName());
        }
    }
}
