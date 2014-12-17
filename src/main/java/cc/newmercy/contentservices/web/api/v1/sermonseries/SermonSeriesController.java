package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import cc.newmercy.contentservices.web.time.ConsistentClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RequestMapping(value = "/v1/sermon-series", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonSeriesController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SermonSeriesRepository repo;

	private final Validator validator;

	private final ConsistentClock clock;

	public SermonSeriesController(SermonSeriesRepository repo, Validator validator, ConsistentClock clock) {
		this.repo = Objects.requireNonNull(repo, "sermon series repository");
		this.validator = Objects.requireNonNull(validator, "validator");
		this.clock = Objects.requireNonNull(clock, "clock");
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PersistentSermonSeries add(@RequestBody 	TransientSermonSeries transientSermonSeries) {
		Set<ConstraintViolation<TransientSermonSeries>> violations = validator.validate(transientSermonSeries);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		logger.debug("saving new sermon series {}", transientSermonSeries);

		return repo.save(transientSermonSeries, clock.now());
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
