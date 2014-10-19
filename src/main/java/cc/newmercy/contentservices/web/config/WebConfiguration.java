package cc.newmercy.contentservices.web.config;

import java.util.Arrays;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import cc.newmercy.contentservices.ServerStopper;
import cc.newmercy.contentservices.web.api.v1.admin.AdminController;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesController;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class WebConfiguration {
	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	private Validator validator;

	@Autowired
	private ServerStopper serverStopper;

	@Bean
	public Object requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();

		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
		jacksonConverter.setObjectMapper(jsonMapper);

		adapter.setMessageConverters(Arrays.asList(jacksonConverter));

		return adapter;
	}

	@Bean
	public SermonSeriesController sermonSeriesController() {
		return new SermonSeriesController(validator);
	}

	@Bean
	public AdminController adminController() {
		return new AdminController(serverStopper);
	}
}
