package io.github.loggingplugin.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.loggingplugin.aspect.annotation.SysLog;
import io.github.loggingplugin.aspect.enums.HttpMethod;
import io.github.loggingplugin.entity.BaseLogEntity;
import io.github.loggingplugin.entity.BaseUserEntity;
import io.github.loggingplugin.service.IOperationLogService;
import io.github.loggingplugin.util.IpUtils;
import io.github.loggingplugin.util.LogServletUtils;
import io.github.loggingplugin.util.LogStringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

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
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 计算操作消耗时间
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 处理请求前执行
     *
     * @param joinPoint 连接点
     * @param sysLog    注解
     */
    @Before(value = "@annotation(sysLog)")
    public void doBeforeProcessing(JoinPoint joinPoint, SysLog sysLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行保存操作日志
     *
     * @param joinPoint 连接点
     * @param result    切入方法返回值
     */
    @AfterReturning(pointcut = "@annotation(sysLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, SysLog sysLog, Object result) {
        recordOperationLog(joinPoint, sysLog, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 连接点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(sysLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, SysLog sysLog, Exception e) {
        recordOperationLog(joinPoint, sysLog, e, null);
    }

    /**
     * 设置系统日志基本参数数据
     *
     * @param joinPoint 连接点
     * @param sysLog    日志注解
     * @param e         异常
     * @param result    调用的方法返回结果
     */
    private void recordOperationLog(JoinPoint joinPoint, SysLog sysLog, Exception e, Object result) {
        try {
            BaseLogEntity baseLogEntity = new BaseLogEntity();
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            // 获取自定义的系统用户信息
            BaseUserEntity user = operationLogService.getSysUserInfo();
            // 操作模块
            baseLogEntity.setOperateModule(sysLog.operateModule())
                    // 业务类型
                    .setBusinessType(sysLog.businessType())
                    // 请求url
                    .setRequestUrl(LogStringUtils.substring(LogServletUtils.getRequest().getRequestURI(), 0, 255))
                    // 设置方法名称
                    .setMethod(className + "." + methodName + "()")
                    // 设置请求方式
                    .setRequestMethod(LogServletUtils.getRequest().getMethod())
                    // 操作者类别
                    .setOperatorType(sysLog.operatorType())
                    // 操作者ip地址
                    .setOperateIp(IpUtils.getIpAddr(LogServletUtils.getRequest()))
                    // 操作时间
                    .setOperateTime(new Date())
                    // 操作描述
                    .setOperateDescription(sysLog.operateDescription())
                    // 操作者id
                    .setOperateUserId(user.getOperateUserId())
                    // 操作者用户名
                    .setOperateUserName(user.getOperateUserName())
                    // 操作者部门名
                    .setOperateDeptName(user.getOperateDeptName())
                    // 先设置错误信息为空
                    .setErrorMsg(LogStringUtils.EMPTY);
            // 是否保存请求的参数
            if (sysLog.isSaveRequestData()) {
                setRequestValue(joinPoint, baseLogEntity);
            }
            // 是否需要保存response
            if (sysLog.isSaveResponseData() && LogStringUtils.isNotNull(result))
            {
                baseLogEntity.setJsonResult(LogStringUtils.substring(JSON.toJSONString(result), 0, 2000));
            }
            if (e != null) {
                // 异常信息
                baseLogEntity.setErrorMsg(LogStringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置消耗时间
            baseLogEntity.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存日志
            operationLogService.recordOperateLog(baseLogEntity);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取请求的参数，放到baseLogEntity中
     *
     * @param joinPoint     连接点
     * @param baseLogEntity 日志基础对象
     */
    private void setRequestValue(JoinPoint joinPoint, BaseLogEntity baseLogEntity) {
        String requestMethod = baseLogEntity.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            baseLogEntity.setRequestParam(LogStringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = LogServletUtils.getParamMap(LogServletUtils.getRequest());
            baseLogEntity.setRequestParam(LogStringUtils.substring(JSON.toJSONString(paramsMap), 0, 2000));
        }
    }

    /**
     * 参数拼装
     *
     * @param paramsArray 参数数组
     * @return {@link String}
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (null != o) {
                    try {
                        Object jsonObj = JSONObject.toJSONString(o);
                        params.append(jsonObj.toString()).append(" ");
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return params.toString().trim();
    }
}
