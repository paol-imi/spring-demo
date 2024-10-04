package com.example.library.aspect;

import com.example.library.metrics.PerformanceMetrics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private final PerformanceMetrics metrics;

    public PerformanceMonitoringAspect(PerformanceMetrics metrics) {
        this.metrics = metrics;
    }

    @Around("execution(* com.example.library.controller.*.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        this.metrics.recordMethodExecutionTime(className, methodName, executionTime);

        return result;
    }
}