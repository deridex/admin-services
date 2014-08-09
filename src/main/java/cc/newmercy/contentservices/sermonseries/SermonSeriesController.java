package cc.newmercy.contentservices.sermonseries;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/sermonseries", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonSeriesController {

	private final Validator validator;

	public SermonSeriesController(Validator validator) {
		this.validator = Objects.requireNonNull(validator, "validator");
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SermonSeries> list(
			@RequestParam(required = false) String startId,
			@RequestParam(defaultValue = "10") @Min(1) int maxResults) {
		return null;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PersistentSermonSeries add(@RequestBody 	TransientSermonSeries transientSermonSeries) {
		Set<ConstraintViolation<TransientSermonSeries>> violations = validator.validate(transientSermonSeries);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		PersistentSermonSeries persistentSermonSeries = new PersistentSermonSeries(transientSermonSeries);

		persistentSermonSeries.setId("");

		return persistentSermonSeries;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SermonSeries get(@PathVariable("id") String id) {
		return new SermonSeries();
	}
}
