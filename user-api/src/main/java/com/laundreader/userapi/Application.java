package com.laundreader.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.laundreader.userapi",
	"com.laundreader.external",
	"com.laundreader.common",
	"com.laundreader.domain"
})
@EntityScan(basePackages = "com.laundreader.domain")
@EnableJpaRepositories(basePackages = "com.laundreader.domain")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
