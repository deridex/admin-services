package cc.newmercy.contentservices.web.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import cc.newmercy.contentservices.ServerStopper;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.web.api.v1.admin.AdminController;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesController;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class WebConfiguration implements WebBindingInitializer {
	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	private LocalValidatorFactoryBean validator;

	@Autowired
	private ServerStopper serverStopper;

	@Autowired
	private Neo4jTransaction neo4jTransaction;

	@Autowired
	private SermonSeriesRepository SermonSeriesRepository;

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
		return new SermonSeriesController(SermonSeriesRepository, validator);
	}

	@Bean
	public AdminController adminController() {
		return new AdminController(serverStopper);
	}

	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		binder.setValidator(validator);
	}
}
