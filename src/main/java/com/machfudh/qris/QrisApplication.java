package com.machfudh.qris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class QrisApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrisApplication.class, args);
	}

}
