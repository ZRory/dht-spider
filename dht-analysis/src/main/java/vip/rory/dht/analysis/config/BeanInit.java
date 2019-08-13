package vip.rory.dht.analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vip.rory.bencode.Bencode;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午4:41:23
 */
@Configuration
public class BeanInit {

    /**
     * 初始化bencode
     */
    @Bean
    public Bencode initBencode() {
        return new Bencode();
    }

}
