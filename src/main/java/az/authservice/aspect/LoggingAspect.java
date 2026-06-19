package az.authservice.aspect;

import az.authservice.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(log)")
    public Object logAround(ProceedingJoinPoint joinPoint, Log log)
            throws Throwable {

        var prefix = "ActionLog." + log.value().name();
        var args = formatArgs(joinPoint.getArgs());

        LoggingAspect.log.info("{}.start - {}", prefix, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            var duration = System.currentTimeMillis() - start;

            LoggingAspect.log.info("{}.end - duration: {}ms", prefix, duration);
            return result;

        } catch (Exception ex) {
            var duration = System.currentTimeMillis() - start;

            LoggingAspect.log.error("{}.error - duration: {}ms, exception: {}",
                    prefix, duration, ex.getClass().getSimpleName());
            throw ex;
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "no args";

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    if (arg instanceof String s && s.startsWith("Bearer"))
                        return "authHeader: Bearer ***";
                    return arg.toString();
                })
                .collect(Collectors.joining(", "));
    }
}
