package com.leyou.auth.annotation;

import com.leyou.auth.config.MvcConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用JWT验证开关，会通过mvc的拦截器拦截用户请求，并获取用户信息，存入UserContext
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MvcConfiguration.class)
@Documented
@Inherited
public @interface EnableJwtVerification {
}