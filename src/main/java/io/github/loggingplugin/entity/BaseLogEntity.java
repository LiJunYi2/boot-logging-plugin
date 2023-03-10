package io.github.loggingplugin.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.0.0
 * @className: BaseLogEntity
 * @description: 日志基本对象
 * @author: LiJunYi
 * @create: 2023/3/9 14:27
 */
@Data
@ToString
public class BaseLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 操作模块
     */
    private String operateModule;

    /**
     * 业务类型
     * 根据自己的业务自定义类型，例如:0=其它,1=新增,2=修改,3=删除......
     */
    private Integer businessType;

    /**
     * 请求url
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParam;

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

    /**
     * 操作描述
     */
    private String operateDescription;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 返回参数
     */
    private String jsonResult;

    /**
     * 操作时间
     */
    private Date operateTime;
}
