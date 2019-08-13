package vip.rory.dht.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import vip.rory.dht.spider.config.RuntimeEnv;
import vip.rory.dht.spider.entity.Node;
import vip.rory.dht.spider.service.MessageReceiver;
import vip.rory.dht.spider.service.MessageSenderService;

@SpringBootApplication
@ComponentScan(basePackages = { "vip.rory.dht" })
public class DhtSpiderApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(DhtSpiderApplication.class, args);
        MessageSenderService messageSenderService = run.getBean(MessageSenderService.class);
        // 对创世节点发送find_node请求
        for (Node genesisNode : RuntimeEnv.genesisNodes) {
            messageSenderService.findNode(genesisNode);
        }
        // 开始监听消息
        MessageReceiver messageReceiver = run.getBean(MessageReceiver.class);
        messageReceiver.listen();
        messageReceiver.listen();
    }

}
