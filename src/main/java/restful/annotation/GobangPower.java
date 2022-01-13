package restful.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lwz
 * @create 2021-11-22 19:04
 * @description: 拦截器注解
 * 使用int[]类型存放多个值
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GobangPower {
    /**
     * LOGIN: 需要登录
     * AUTO_LOGIN: 需要自动登录
     * ADMIN: 需要管理员权限
     */
    int LOGIN = 1;
    int AUTO_LOGIN = 2;
    int ADMIN = 3;

    int[] value() default {};
}
