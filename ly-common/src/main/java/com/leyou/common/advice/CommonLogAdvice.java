package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Slf4j
@Aspect
@Component
public class CommonLogAdvice {

    // 扫描所有带@Service注解的类中的所有方法
//    @Around("within(@org.springframework.stereotype.Service *)")
    //之前的切入点语法只对Service自己的方法有效，如果是继承自ServiceImpl的方法则失效。因此需要修改CommonLogAdvice中的切入点语法：
    @Around("within(@org.springframework.stereotype.Service *) || within(com.baomidou.mybatisplus.extension.service.IService+)")
    public Object handleExceptionLog(ProceedingJoinPoint jp) throws Throwable {
        try {
            // 记录方法进入日志
            log.debug("{}方法准备调用，参数: {}", jp.getSignature(), Arrays.toString(jp.getArgs()));
            long a = System.currentTimeMillis();
            // 调用切点方法
            Object result = jp.proceed();
            // 记录方法结束日志
            log.debug("{}方法调用成功，执行耗时{}", jp.getSignature(), System.currentTimeMillis() - a);
            return result;
        } catch (Throwable throwable) {
            log.error("{}方法执行失败，原因：{}", jp.getSignature(), throwable.getMessage(), throwable);
            // 判断异常是否是LyException
            if(throwable instanceof LyException){
                // 如果是，不处理，直接抛
                throw throwable;
            }else{
                // 如果不是，转为LyException
                throw new LyException(500, throwable);
            }
        }
    }
}