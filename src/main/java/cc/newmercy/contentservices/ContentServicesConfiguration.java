package cc.newmercy.contentservices;

import java.util.Arrays;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import cc.newmercy.contentservices.v1.sermonseries.SermonSeriesController;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
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
	public Object requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();

		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
		jacksonConverter.setObjectMapper(jsonMapper());

		adapter.setMessageConverters(Arrays.asList(jacksonConverter));

		return adapter;
	}

	@Bean
	public SermonSeriesController sermonSeriesController() {
		return new SermonSeriesController(validator());
	}
}
