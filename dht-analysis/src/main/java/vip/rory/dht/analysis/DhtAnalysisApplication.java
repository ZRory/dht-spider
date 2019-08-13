package vip.rory.dht.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "vip.rory.dht" })
public class DhtAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DhtAnalysisApplication.class, args);
    }

}
