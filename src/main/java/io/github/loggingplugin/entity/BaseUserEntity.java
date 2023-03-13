package io.github.loggingplugin.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @version 1.0.0
 * @className: BaseUserEntity
 * @description: 基本用户信息
 * @author: LiJunYi
 * @create: 2023/3/9 16:11
 */
@Data
@Accessors(chain = true)
@ToString
public class BaseUserEntity {

    /**
     * 操作者类别
     */
    private Integer operatorType;

    /**
     * 操作者id
     */
    private Long operateUserId;

    /**
     * 操作者用户名
     */
    private String operateUserName;

    /**
     * 操作者部门名
     */
    private String operateDeptName;

    /**
     * 操作者ip地址
     */
    private String operateIp;
}
