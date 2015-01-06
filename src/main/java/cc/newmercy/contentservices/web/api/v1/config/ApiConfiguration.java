package cc.newmercy.contentservices.web.api.v1.config;

import java.util.Arrays;

import cc.newmercy.contentservices.aws.AssetStore;
import cc.newmercy.contentservices.web.api.v1.sermon.SermonAssetRepository;
import cc.newmercy.contentservices.web.api.v1.sermon.SermonController;
import cc.newmercy.contentservices.web.api.v1.sermon.SermonRepository;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesController;
import cc.newmercy.contentservices.web.api.v1.sermonseries.SermonSeriesRepository;
import cc.newmercy.contentservices.web.id.IdService;
import cc.newmercy.contentservices.web.time.ConsistentClock;
import cc.newmercy.contentservices.web.time.DefaultClock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class ApiConfiguration implements WebBindingInitializer {

	private String s3KeyPrefix = "sermons";

	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	private LocalValidatorFactoryBean validator;

	@Autowired
	private SermonSeriesRepository sermonSeriesRepository;

	@Autowired
	private SermonRepository sermonRepository;

	@Autowired
	private SermonAssetRepository sermonAssetRepository;

	@Autowired
	private IdService idService;

	@Autowired
	private AssetStore assetStore;

	@Bean
	public Object requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();

		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(jsonMapper);

		adapter.setMessageConverters(Arrays.asList(jacksonConverter));

		return adapter;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConsistentClock consistentClock() {
		return new DefaultClock();
	}

	@Bean
	public SermonSeriesController sermonSeriesController() {
		return new SermonSeriesController(sermonSeriesRepository, validator, consistentClock());
	}

	@Bean
	public SermonController sermonController() {
		return new SermonController(
				consistentClock(),
				sermonRepository,
				idService,
				sermonAssetRepository,
				s3KeyPrefix,
				assetStore);
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();

		multipartResolver.setMaxUploadSize(1024 * 1024 * 1024 * 2);

		return multipartResolver;
	}

	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		binder.setValidator(validator);
	}
}
