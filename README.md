# boot-logging-plugin
记录系统操作日志以及脱敏插件，即入即用

### 项目描述

在项目开发过程中，记录系统用户操作日志必不可少，不免存在每个项目都要去写一遍有关系统日志记录的方法。为了节省开发过程中这一块的时间，于是造出了**boot-logging-plugin** 插件，用于记录系统日志，同时加入了脱敏注解，依赖`jackson`实现对敏感数据进行脱敏处理。

### 使用方式

##### 1、表结构参考

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`  (
  `id` bigint(20) NOT NULL COMMENT '日志主键',
  `operate_module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作模块',
  `business_type` int(2) NULL DEFAULT NULL COMMENT '业务类型,根据自己的业务自定义类型，例如:0=其它,1=新增,2=修改,3=删除......',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求url',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `request_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `operator_type` int(2) NULL DEFAULT NULL COMMENT '操作者类别',
  `operate_user_id` bigint(20) NULL DEFAULT NULL COMMENT '操作者id',
  `operate_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作者用户名',
  `operate_dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作者部门名',
  `operate_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作者ip地址',
  `operate_description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作描述',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误信息',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
```

##### 2、引入依赖

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

##### 3、实现`IOperationLogService`接口

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
        return new BaseUserEntity()
            .setOperateUserName("占山")
            .setOperateUserId(200L)
            .setOperatorType(1)
            .setOperateDeptName("六处");
    }
}
```

##### 4、测试接口

```java
/**
 * @version 1.0.0
 * @className: TestController
 * @description:
 * @author: LiJunYi
 * @create: 2023/3/10 16:09
 */
@RestController
@RequestMapping("log")
public class TestController {

    /**
     * 查询用户接口参数测试
     *
     * @param userName 用户名
     * @return {@link BaseUserEntity}
     */
    @GetMapping("user")
    @SysLog(operateModule = "用户模块",businessType = 1,operateDescription = "查询用户操作")
    public BaseUserEntity queryUser(String userName)
    {
        BaseUserEntity user = new BaseUserEntity();
        user.setOperateUserName(userName);
        return user;
    }

    /**
     * 保存用户接口Body参数测试
     *
     * @param user user
     * @return {@link BaseUserEntity}
     */
    @PostMapping("save")
    @SysLog(operateModule = "用户模块",businessType = 2,operateDescription = "保存用户信息")
    public BaseUserEntity saveUser(@RequestBody BaseUserEntity user)
    {
        return user;
    }
}

```

##### 5、测试结果

```shell
2023-03-13 12:05:11.350  INFO 18780 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2023-03-13 12:05:11.350  INFO 18780 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2023-03-13 12:05:11.350  INFO 18780 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms
设置自己对应系统用户信息
获取到一一条操作日志:BaseLogEntity(id=null, operateModule=用户模块, businessType=2, requestUrl=/log/save, method=com.example.test.controller.TestController.saveUser(), requestMethod=POST, requestParam={"operateDeptName":"研发部","operateIp":"127.0.0.1","operateUserId":2,"operateUserName":"POST测试","operatorType":1}, operatorType=0, operateUserId=200, operateUserName=占山, operateDeptName=六处, operateIp=192.168.187.1, operateDescription=保存用户信息, errorMsg=, jsonResult={"operateDeptName":"研发部","operateIp":"127.0.0.1","operateUserId":2,"operateUserName":"POST测试","operatorType":1}, operateTime=Tue Mar 14 09:43:56 CST 2023, costTime=1)
设置自己对应系统用户信息
获取到一一条操作日志:BaseLogEntity(id=null, operateModule=用户模块, businessType=1, requestUrl=/log/user, method=com.example.test.controller.TestController.queryUser(), requestMethod=GET, requestParam={"userName":"zhangsan"}, operatorType=0, operateUserId=200, operateUserName=占山, operateDeptName=六处, operateIp=192.168.187.1, operateDescription=查询用户操作, errorMsg=, jsonResult={"operateUserName":"zhangsan"}, operateTime=Tue Mar 14 09:43:58 CST 2023, costTime=1)

```

### 脱敏注解@Sensitive

```java
/**
 * @version 1.0.0
 * @className: Person
 * @description: 用户类
 * @author: LiJunYi
 * @create: 2023/3/13 10:13
 */
@Data
public class Person {

    /**
     * 姓名脱敏
     */
    @Sensitive(strategy = SensitiveStrategy.USERNAME)
    private String userName;

    /**
     * 密码
     */
    @Sensitive(strategy = SensitiveStrategy.PASSWORD)
    private String password;

    /**
     * 电子邮件
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    private String email;

    /**
     * 手机号脱敏
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;

    /**
     * 身份证号码脱敏
     */
    @Sensitive(strategy = SensitiveStrategy.ID_CARD)
    private String idCard;

    /**
     * 银行卡
     */
    @Sensitive(strategy = SensitiveStrategy.BANK_CARD)
    private String bankCard;

    /**
     * 地址信息脱敏
     */
    @Sensitive(strategy = SensitiveStrategy.ADDRESS)
    private String address;
}

```

##### 测试结果

```json
{
	"userName": "不*人",
	"password": "************",
	"email": "2****@163.com",
	"phone": "187****1111",
	"idCard": "2314****3623",
	"bankCard": "622202****6744",
	"address": "浙江省****市余****"
}
```
