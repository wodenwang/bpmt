package com.riversoft.platform.stat;

import com.riversoft.core.Config;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by exizhai on 19/07/2014.
 */
@Aspect
public class Perf4jInterceptor {

    private Map<String, StopWatch> watches = new HashMap<String, StopWatch>();

    private boolean isEnable(){
        return Boolean.valueOf(Config.get("perf4j.enable") == null ? "false" : Config.get("perf4j.enable"));
    }

    @Before("execution (* *..*Service.*(..)) || execution (* *..*Action.*(..))")
    public void before(JoinPoint point) throws Throwable {
        if(isEnable()) {
            String completeMethodName = getCompleteMethodName(point);
            StopWatch stopWatch;
            if (watches.containsKey(completeMethodName)) {
                stopWatch = watches.get(completeMethodName);
                stopWatch.start();
            } else {
                stopWatch = new Slf4JStopWatch(completeMethodName, Arrays.toString(point.getArgs()));
                watches.put(completeMethodName, stopWatch);
            }
        }
    }

    @AfterReturning("execution (* *..*Service.*(..)) || execution (* *..*Action.*(..))")
    public void afterReturning(JoinPoint point) throws Throwable {
        if(isEnable()) {
            String completeMethodName = getCompleteMethodName(point);
            if (watches.containsKey(completeMethodName)) {
                StopWatch stopWatch = watches.get(completeMethodName);
                stopWatch.stop();
            }
        }
    }

    private String getCompleteMethodName(JoinPoint point) {
        String className = "";
        if (point.getSignature().getDeclaringTypeName() != null) {
            className = point.getSignature().getDeclaringTypeName();
            int loc = className.indexOf("@");
            if (loc >= 0) {
                className = className.substring(0, loc);
            }
        }
        return className + "." + point.getSignature().getName();
    }
}
