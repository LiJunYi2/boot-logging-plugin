package io.github.loggingplugin.aspect;

import com.alibaba.fastjson2.JSONObject;
import io.github.loggingplugin.aspect.annotation.SysLog;
import io.github.loggingplugin.entity.BaseLogEntity;
import io.github.loggingplugin.entity.BaseUserEntity;
import io.github.loggingplugin.service.IOperationLogService;
import io.github.loggingplugin.util.IpUtils;
import io.github.loggingplugin.util.LogServletUtils;
import io.github.loggingplugin.util.LogStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @version 1.0.0
 * @className: LogAspect
 * @description: 操作日志切面处理类
 * @author: LiJunYi
 * @create: 2023/3/9 14:58
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 日志切点
     */
    @Pointcut("@annotation( io.github.loggingplugin.aspect.annotation.SysLog)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行保存操作日志
     *
     * @param joinPoint 连接点
     * @param result    切入方法返回值
     */
    @AfterReturning(value = "logPointCut()",returning = "result")
    public void saveOperationLog(JoinPoint joinPoint, Object result) {
        SysLog sysLog = getAnnotationLog(joinPoint);
        if (null != sysLog)
        {
            BaseLogEntity operationLog = new BaseLogEntity();
            try {
                setBaseLog(joinPoint,sysLog,operationLog);
                // 是否保存响应的参数
                if (sysLog.isSaveResponseData())
                {
                    operationLog.setJsonResult(LogStringUtils.substring(JSONObject.toJSONString(result),0,2000));
                }
                // 保存日志
                operationLogService.recordOperateLog(operationLog);
            }catch (Exception e)
            {
                log.error("LogAspect#saveOperationLog error:{}",e.getMessage());
            }
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 连接点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "logPointCut()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint,Exception e) {
        SysLog sysLog = getAnnotationLog(joinPoint);
        if (null != sysLog)
        {
            BaseLogEntity exceptionLog = new BaseLogEntity();
            try {
                setBaseLog(joinPoint,sysLog,exceptionLog);
                exceptionLog.setErrorMsg(LogStringUtils.substring(e.getMessage(),0,2000));
                // 保存日志
                operationLogService.recordOperateLog(exceptionLog);
            }catch (Exception ex)
            {
                log.error("LogAspect#doAfterThrowing error:{}",ex.getMessage());
            }
        }
    }

    /**
     * 设置日志的基本参数
     *
     * @param joinPoint     连接点
     * @param baseLogEntity 基本日志对象
     * @param sysLog        日志注解
     */
    private void setBaseLog(JoinPoint joinPoint, SysLog sysLog, BaseLogEntity baseLogEntity) {
        // 操作模块
        baseLogEntity.setOperateModule(sysLog.operateModule());
        // 业务类型
        baseLogEntity.setBusinessType(sysLog.businessType());
        // 请求url
        baseLogEntity.setRequestUrl(LogStringUtils.substring(LogServletUtils.getRequest().getRequestURI(),0,255));
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        baseLogEntity.setMethod(className + "." + methodName + "()");
        // 设置请求方式
        baseLogEntity.setRequestMethod(LogServletUtils.getRequest().getMethod());
        // 操作者类别
        baseLogEntity.setOperatorType(sysLog.operatorType());
        // 操作者ip地址
        baseLogEntity.setOperateIp(IpUtils.getIpAddr(LogServletUtils.getRequest()));
        // 操作时间
        baseLogEntity.setOperateTime(new Date());
        // 操作描述
        baseLogEntity.setOperateDescription(sysLog.operateDescription());
        BaseUserEntity user = operationLogService.getSysUserInfo();
        // 操作者id
        baseLogEntity.setOperateUserId(user.getOperateUserId());
        // 操作者用户名
        baseLogEntity.setOperateUserName(user.getOperateUserName());
        // 操作者部门名
        baseLogEntity.setOperateDeptName(user.getOperateDeptName());
        // 是否保存请求的参数
        if (sysLog.isSaveRequestData())
        {
            // 获取参数的信息
            setRequestValue(joinPoint, baseLogEntity);
        }
    }

    /**
     * 获取请求的参数，放到baseLogEntity中
     *
     * @param joinPoint     连接点
     * @param baseLogEntity 日志基础对象
     */
    private void setRequestValue(JoinPoint joinPoint, BaseLogEntity baseLogEntity) {
        Map<String, String[]> map = LogServletUtils.getRequest().getParameterMap();
        String params = LogStringUtils.EMPTY;
        if (null != map && !map.isEmpty())
        {
            params = JSONObject.toJSONString(map);
        }else
        {
            Object args = joinPoint.getArgs();
            if (null != args)
            {
                params = argsArrayToString(joinPoint.getArgs());
            }
        }
        baseLogEntity.setRequestParam(LogStringUtils.substring(params,0,2000));
    }

    /**
     * 参数拼装
     *
     * @param paramsArray 参数数组
     * @return {@link String}
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null)
        {
            for (Object o : paramsArray)
            {
                if (null != o)
                {
                    try
                    {
                        Object jsonObj = JSONObject.toJSONString(o);
                        params.append(jsonObj.toString()).append(" ");
                    }
                    catch (Exception ignored)
                    {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 是否存在注解，如果存在就获取
     *
     * @param joinPoint 连接点
     * @return {@link SysLog}
     */
    private SysLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (null != method)
        {
            return  method.getAnnotation(SysLog.class);
        }
        return null;
    }
}
