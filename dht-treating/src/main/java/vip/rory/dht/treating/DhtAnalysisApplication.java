package vip.rory.dht.treating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import vip.rory.dht.treating.service.DispositionService;

@SpringBootApplication
@ComponentScan(basePackages = { "vip.rory.dht" })
public class DhtAnalysisApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DhtAnalysisApplication.class, args);
        DispositionService dispositionService = run.getBean(DispositionService.class);
        dispositionService.handleUnreadableMetadataRecord();
        dispositionService.handleUnreadableFileRecord();
    }

}
