package cc.newmercy.contentservices.config;

import javax.validation.Validator;
import javax.ws.rs.client.Client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import cc.newmercy.contentservices.ServerStopper;
import cc.newmercy.contentservices.jaxrs.ClientFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
public class ContentServicesConfiguration {
	@Bean
	public Validator validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public ObjectMapper jsonMapper() {
		return new ObjectMapper();
	}

	@Bean
	public ServerStopper serverStopper() {
		return new ServerStopper();
	}

	@Bean
	public Client jaxrsClient() {
		return ClientFactory.newClient(jsonMapper());
	}
}
