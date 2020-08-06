package nju.cloud.prac;

// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
// import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: pkun
 * @CreateTime: 2020-07-26 01:37
 */
@Component
@Scope
@Aspect
public class RateLimitAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    //用来存放不同接口的RateLimiter(key为接口名称，value为RateLimiter)
    // private ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();

    // private static ObjectMapper objectMapper = new ObjectMapper();

    // private RateLimiter rateLimiter;

    private static final int TIMEOUT = 1000;

    @Autowired
    private HttpServletResponse response;

    @Pointcut("@annotation(nju.cloud.prac.RateLimit)")
    public void serviceLimit() {
    }

    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Object obj = null;
        //获取拦截的方法名
        Signature sig = joinPoint.getSignature();
        //获取拦截的方法名
        MethodSignature msig = (MethodSignature) sig;
        //返回被织入增加处理目标对象
        Object target = joinPoint.getTarget();
        //为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        //获取注解信息
        RateLimit annotation = currentMethod.getAnnotation(RateLimit.class);
        double limitNum = annotation.limitNum(); //获取注解每秒加入桶中的token
        String functionName = msig.getName(); // 注解所在方法名区分不同的限流策略

        //生成不同的桶名称
        String BUCKET = "BUCKET_" + functionName;
        String BUCKET_COUNT = "BUCKET_COUNT_" + functionName;
        String BUCKET_MONITOR = "BUCKET_MONITOR_" + functionName;

        //获取 Jedis
        Jedis jedis = JedisUtil.getJedisPool().getResource();

        //获取唯一标识符、时间戳
        String identifier = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        
        //删除之前的请求，计数器自增
        Transaction transaction = jedis.multi();
        transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), "-inf".getBytes(), String.valueOf(now - TIMEOUT).getBytes());
        ZParams params = new ZParams();
        params.weights(1.0, 0.0);
        transaction.zinterstore(BUCKET, params, BUCKET, BUCKET_MONITOR);
        transaction.incr(BUCKET_COUNT);
        List<Object> results = transaction.exec();
        long counter = (Long) results.get(results.size() - 1);

        //加入当前的请求
        transaction = jedis.multi();
        transaction.zadd(BUCKET_MONITOR, now, identifier);
        transaction.zadd(BUCKET, counter, identifier);
        transaction.zrank(BUCKET, identifier);
        results = transaction.exec();
        //获取排名，判断当前请求是否取得令牌
        long rank = (Long) results.get(results.size() - 1);
        if (rank < limitNum) {
            try {
                obj = joinPoint.proceed();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        else {
            //没有获取到令牌，清理这次的垃圾
            transaction = jedis.multi();
            transaction.zrem(BUCKET_MONITOR, identifier);
            transaction.zrem(BUCKET, identifier);
            transaction.exec();
            outErrorResult("429 TooManyRequest");
        }

        //归还 redis
        jedis.close();

        //获取rateLimiter
        /*if(map.containsKey(functionName)){
            rateLimiter = map.get(functionName);
        }else {
            map.put(functionName, RateLimiter.create(limitNum));
            rateLimiter = map.get(functionName);
        }

        try {
            if (rateLimiter.tryAcquire()) {
                //执行方法
                obj = joinPoint.proceed();
            } else {
                //拒绝了请求（服务降级）
//                String result = objectMapper.writeValueAsString(ResponseVO.Error(429, "TooManyRequest"));
//                log.info("拒绝了请求：" + result);
                outErrorResult("429 TooManyRequest");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/
        return obj;
    }
    //将结果返回
    public void outErrorResult(String result) {
        response.setContentType("application/json;charset=UTF-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(result.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // static {
    //     objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // }

}