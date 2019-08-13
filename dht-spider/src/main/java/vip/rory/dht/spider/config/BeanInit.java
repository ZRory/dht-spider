package vip.rory.dht.spider.config;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import vip.rory.bencode.Bencode;
import vip.rory.dht.common.util.GenerateUtils;
import vip.rory.dht.spider.entity.Node;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午4:41:23
 */
@Order(value = 2)
@Configuration
public class BeanInit {
    
    @Autowired
    private RuntimeEnv runtimeEnv;

    /**
     * 初始化udpserver
     * 
     * @throws SocketException
     */
    @SuppressWarnings("static-access")
    @Bean
    public DatagramSocket initDatagramSocket() throws SocketException {
        return new DatagramSocket(runtimeEnv.localPort);
    }

    /**
     * 初始化本机节点
     */
    @Bean(name = "localNode")
    public Node initLocalNode() {
        Node node = new Node();
        node.setIp(RuntimeEnv.localIp);
        node.setPort(RuntimeEnv.localPort);
        node.setLastContactTime(LocalDateTime.now());
        node.setNodeIdHex(GenerateUtils.generateNodeIdHex());
        node.setRank(Integer.MAX_VALUE);
        node.encode();
        return node;
    }

    /**
     * 初始化bencode
     */
    @Bean
    public Bencode initBencode() {
        return new Bencode(Charset.forName("ISO-8859-1"));
    }
    
}
