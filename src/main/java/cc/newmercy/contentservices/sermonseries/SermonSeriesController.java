package cc.newmercy.contentservices.sermonseries;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/sermonseries", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonSeriesController {
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SermonSeries> list(
			@RequestParam(required = false) String startId,
			@RequestParam(defaultValue = "10") @Min(1) int maxResults) {
		return null;
	}

	@RequestMapping(value = "/{id}")
	@ResponseBody
	public SermonSeries get(@PathVariable("id") String id) {
		return new SermonSeries();
	}
}
