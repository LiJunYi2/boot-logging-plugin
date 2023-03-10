package io.github.loggingplugin.service;

import io.github.loggingplugin.entity.BaseLogEntity;
import io.github.loggingplugin.entity.BaseUserEntity;

/**
 * @version 1.0.0
 * @className: IOperationLogService
 * @description: 日志预留接口
 * @author: LiJunYi
 * @create: 2023/3/9 15:00
 */
public interface IOperationLogService {

    /**
     * 记录操作日志
     *
     * @param baseLogEntity 基本日志对象
     */
    void recordOperateLog(BaseLogEntity baseLogEntity);

    /**
     * 设置自己系统的用户信息
     *
     * @return {@link BaseUserEntity}
     */
    BaseUserEntity getSysUserInfo();
}
