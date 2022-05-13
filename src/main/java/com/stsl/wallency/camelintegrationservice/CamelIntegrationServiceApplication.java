package com.stsl.wallency.camelintegrationservice;

import com.stsl.wallency.camelintegrationservice.configuration.AppConfiguration;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(value = AppConfiguration.class)
public class CamelIntegrationServiceApplication {

	private final AppConfiguration appConfiguration;

	public CamelIntegrationServiceApplication(AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
	}

	public static void main(String[] args) {
		SpringApplication.run(CamelIntegrationServiceApplication.class, args);
	}



	@Bean
	ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean servlet = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/" + appConfiguration.getContextPath() + "/*");
		servlet.setName("CamelServlet");
		return servlet;
	}
}
