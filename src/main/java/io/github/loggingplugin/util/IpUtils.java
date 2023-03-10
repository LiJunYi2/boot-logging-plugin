package io.github.loggingplugin.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @version 1.0.0
 * @className: IpUtils
 * @description: IP工具类
 * @author: LiJunYi
 * @create: 2023/3/9 16:01
 */
public class IpUtils {

    private static final String COMMA = ",";
    private static final String IP = "127.0.0.1";
    private static final String LOCAL_IP = "0:0:0:0:0:0:0:1";

    /**
     * x-forwarded-for是识别通过HTTP代理或负载均衡方式连接到Web服务器的客户端
     * 最原始的IP地址的HTTP请求头字段
     */
    private static final String HEADER = "x-forwarded-for";

    private static final String UNKNOWN = "unknown";

    /**
     * 经过apache http服务器的请求才会有
     * 用apache http做代理时一般会加上Proxy-Client-IP请求头
     * 而WL-Proxy-Client-IP是它的weblogic插件加上的头。
     */
    private static final String WL_IP = "WL-Proxy-Client-IP";
    private static final Integer IP_LENGTH = 15;

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(HEADER);
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals(IP) || ip.equals(LOCAL_IP)) {
                // 根据网卡获取本机配置的IP地址
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (Objects.nonNull(inetAddress)) {
                    ip = inetAddress.getHostAddress();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实的IP地址，多个IP按照','分割
        if (null != ip && ip.length() > IP_LENGTH) {
            // "***.***.***.***".length() = 15
            if (ip.indexOf(COMMA) > 0) {
                ip = ip.substring(0, ip.indexOf(COMMA));
            }
        }
        return ip;
    }
}
