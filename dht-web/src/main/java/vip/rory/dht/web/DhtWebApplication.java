package vip.rory.dht.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "vip.rory.dht" })
public class DhtWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhtWebApplication.class, args);
	}

}
