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

<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.24</version>
</dependency>
```

###### 2、实现`IOperationLogService`接口

```java
@Service
public class LogInfoWriteServiceImpl implements IOperationLogService {

    /**
     * 日志入库
     * 库存入MySql、Redis、ES等
     * @param baseLogEntity 日志基础对象
     */
    @Override
    public void recordOperateLog(BaseLogEntity baseLogEntity) {
        System.out.println(baseLogEntity);
        // 这里再调用一个存储数据库的service即可
        System.out.println("正在记录数据库日志 - 正常");
    }

    /**
     * 这里可以设置成实际系统用户
     *
     * @return {@link BaseUserEntity}
     */
    @Override
    public BaseUserEntity getSysUserInfo() {
        BaseUserEntity user = new BaseUserEntity();
        user.setOperateUserId(1L);
        user.setOperateUserName("lijunyi");
        user.setOperatorType(1);
        user.setOperateDeptName("研发部门");
        return user;
    }
}
```

