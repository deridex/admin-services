package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Transactional
@RequestMapping(value = "/v1/sermon-series", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonSeriesController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SermonSeriesRepository repo;

	private final Validator validator;

	public SermonSeriesController(SermonSeriesRepository repo, Validator validator) {
		this.repo = Objects.requireNonNull(repo, "sermon series repository");
		this.validator = Objects.requireNonNull(validator, "validator");
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<PersistentSermonSeries> list(
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Valid @Min(1) @Max(100) int pageSize) {
		return repo.list(page, pageSize);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PersistentSermonSeries add(@RequestBody 	TransientSermonSeries transientSermonSeries) {
		Set<ConstraintViolation<TransientSermonSeries>> violations = validator.validate(transientSermonSeries);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		logger.debug("saving new sermon series {}", transientSermonSeries);

		return repo.save(transientSermonSeries);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public PersistentSermonSeries get(@PathVariable("id") String id) {
		PersistentSermonSeries series = repo.get(id);

		return series;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PersistentSermonSeries update(@PathVariable("id") String id, @RequestBody EditedSermonSeries editedSeries) {
		return repo.update(id, editedSeries);
	}
}
