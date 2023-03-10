package io.github.loggingplugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot+aop实现的系统日志记录插件
 * GitHub：<a href="https://github.com/LiJunYi2/boot-logging-plugin">...</a>
 * @author LiJunYi
 * @date 2023/03/10
 */
@SpringBootApplication
public class BootLoggingPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootLoggingPluginApplication.class, args);
    }

}
