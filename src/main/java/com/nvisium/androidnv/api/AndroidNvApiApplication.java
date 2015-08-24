package com.nvisium.androidnv.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@ComponentScan
@EnableJpaRepositories(basePackages = "com.nvisium.androidnv.api.repository")
@EntityScan(basePackages = "com.nvisium.androidnv.api.model")
@SpringBootApplication
public class AndroidNvApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AndroidNvApiApplication.class, args);
	}
}
