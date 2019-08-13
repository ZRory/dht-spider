package vip.rory.dht.spider.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import vip.rory.dht.spider.entity.Node;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午4:28:27
 */
@Order(value = 1)
@Configuration
@ConfigurationProperties(prefix = "application")
public class RuntimeEnv {

    /**
     * 本机IP地址
     */
    public static String              localIp;
    /**
     * 本机监听端口
     */
    public static Integer             localPort;

    /**
     * 初始节点
     */
    public static List<Node>          genesisNodes;

    /**
     * 已发送消息表
     */
    //public static Map<String, String> sendTable    = Collections.synchronizedMap(new LinkedHashMap<String, String>());
    //public static Map<String, String> sendTable = Collections.synchronizedMap(new LinkedHashMap<String, String>(6144));

    /**
     * @param localIp the localIp to set
     */
    @Value("${application.local.ip}")
    public void setLocalIp(String localIp) {
        RuntimeEnv.localIp = localIp;
    }

    /**
     * @param localPort the localPort to set
     */
    @Value("${application.local.port}")
    public void setLocalPort(Integer localPort) {
        RuntimeEnv.localPort = localPort;
    }

    /**
     * @param genesisNode the genesisNode to set
     */
    public void setGenesisNodes(List<Node> genesisNodes) {
        RuntimeEnv.genesisNodes = genesisNodes;
    }

}
