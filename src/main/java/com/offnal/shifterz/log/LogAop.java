package com.offnal.shifterz.log;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.log.service.LogService;
import com.offnal.shifterz.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAop {

    private final LogService logService;

    // ───────────── Pointcut ─────────────
    @Pointcut("execution(* com.offnal.shifterz..controller.*.*(..))")
    public void controllerPointcut() {}

    @Pointcut("execution(* com.offnal.shifterz..service.*.*(..)) && !within(com.offnal.shifterz..service.LogService)")
    public void servicePointcut() {}

    // ───────────── Controller 로그 (저장O + 콘솔) ─────────────
    @Before("controllerPointcut()")
    public void logControllerEnter(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Member member = AuthService.getCurrentMember();

        String params = extractParams(joinPoint);
        String msg = "[Controller] Enter: " + method.getName() + " | Request: " + params;

        log.info(msg);
        logService.saveLog(member, 'C', msg); // C = Controller Enter
    }

    @AfterReturning(value = "controllerPointcut()", returning = "returnObj")
    public void logControllerReturn(JoinPoint joinPoint, Object returnObj) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Member member = AuthService.getCurrentMember();

        String msg = "[Controller] Return: " + method.getName() + " | Response: " + stringify(returnObj);

        log.info(msg);
        logService.saveLog(member, 'R', msg); //R = Return
    }

    // ───────────── Service 로그 (저장X + 콘솔) ─────────────
    @Before("servicePointcut()")
    public void logServiceEnter(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String params = extractParams(joinPoint);

        log.info("[Service] Enter: {} | Request: {}", method.getName(), params);
    }

    @AfterReturning(value = "servicePointcut()", returning = "returnObj")
    public void logServiceReturn(JoinPoint joinPoint, Object returnObj) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        log.info("[Service] Return: {} | Response: {}", method.getName(), stringify(returnObj));
    }

    // ───────────── 공통 Exception 로그 (Controller + Service) ─────────────
    @AfterThrowing(value = "controllerPointcut() || servicePointcut()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Member member = AuthService.getCurrentMember();

        String msg = "[Error] in " + method.getName() + " - " + ex.getMessage();

        log.error(msg, ex);
        logService.saveLog(member, 'E', msg); // E = Error
    }

    private String extractParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) return "No params";

        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) continue;
            sb.append(arg.getClass().getSimpleName())
                    .append(":")
                    .append(stringify(arg))
                    .append("; ");
        }
        return sb.toString();
    }

    private String stringify(Object obj) {
        try {
            return (obj != null) ? obj.toString() : "null";
        } catch (Exception e) {
            return "unprintable";
        }
    }
}
