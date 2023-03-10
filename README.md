# boot-logging-plugin
springboot项目记录系统操作日志插件，引入即用

### 项目描述

在项目开发过程中，记录系统用户操作日志必不可少，不免存在每个项目都要去写一遍有关系统日志记录的方法。为了节省开发过程中这一块的时间，于是造出了**boot-logging-plugin** 插件，用于记录系统日志。

### 使用方式

###### 1、引入依赖

```xml
<dependency>
   <groupId>io.github.LiJunYi2</groupId>
   <artifactId>boot-logging-plugin</artifactId>
   <version>${latest-version}</version>
</dependency>

<!--使用fastjson2进行解析入参，后续会考虑根据引入的依赖进行动态解析-->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.24</version>
</dependency>
```

###### 2、实现`IOperationLogService`接口

```java
/**
 * @version 1.0.0
 * @className: LogInfoWriteServiceImpl
 * @description: 此处一定要将IOperationLogService交给spring进行管理
 * @author: LiJunYi
 * @create: 2023/3/10 16:09
 */
@Service
public class LogInfoWriteServiceImpl implements IOperationLogService {

    /**
     * 记录操作日志
     *
     * @param baseLogEntity 基地日志实体
     */
    @Override
    public void recordOperateLog(BaseLogEntity baseLogEntity) {
        System.out.println("得到一条系统操作日志:" + baseLogEntity);
    }

    /**
     * 获取系统用户信息
     * 此处可根据自身系统进行完善
     * @return {@link BaseUserEntity}
     */
    @Override
    public BaseUserEntity getSysUserInfo() {
        System.out.println("设置自己对应系统用户信息");
        BaseUserEntity sysUser = new BaseUserEntity();
        sysUser.setOperateUserName("占山");
        sysUser.setOperateUserId(200L);
        sysUser.setOperatorType(1);
        sysUser.setOperateDeptName("六处");
        return sysUser;
    }
}
```

###### 3、测试结果

```shell
2023-03-10 16:30:09.256  INFO 4820 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2023-03-10 16:30:09.256  INFO 4820 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2023-03-10 16:30:09.257  INFO 4820 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
设置自己对应系统用户信息
得到一条系统操作日志:BaseLogEntity(id=null, operateModule=用户模块, businessType=1, requestUrl=/log/user, method=com.example.test.controller.TestController.queryUser(), requestMethod=GET, requestParam={"userName":["zhangsan"]}, operatorType=0, operateUserId=200, operateUserName=占山, operateDeptName=六处, operateIp=192.168.187.1, operateDescription=查询用户操作, errorMsg=null, jsonResult={"operateUserName":"zhangsan"}, operateTime=Fri Mar 10 16:30:09 CST 2023)
设置自己对应系统用户信息
得到一条系统操作日志:BaseLogEntity(id=null, operateModule=用户模块, businessType=2, requestUrl=/log/save, method=com.example.test.controller.TestController.saveUser(), requestMethod=POST, requestParam={}, operatorType=0, operateUserId=200, operateUserName=占山, operateDeptName=六处, operateIp=192.168.187.1, operateDescription=保存用户信息, errorMsg=null, jsonResult={}, operateTime=Fri Mar 10 16:30:19 CST 2023)

```

