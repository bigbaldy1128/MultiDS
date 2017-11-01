package com.qihoo.multids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@ServletComponentScan
public class MultipleDataSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultipleDataSourceApplication.class, args);
	}
}
