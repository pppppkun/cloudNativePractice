package nju.cloud.prac;

import java.lang.annotation.*;

/**
 * @Author: pkun
 * @CreateTime: 2020-07-26 01:34
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int limitNum() default 100;
}
