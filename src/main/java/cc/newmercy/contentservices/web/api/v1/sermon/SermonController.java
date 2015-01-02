package cc.newmercy.contentservices.web.api.v1.sermon;

import java.util.List;
import java.util.Objects;

import cc.newmercy.contentservices.web.time.ConsistentClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RequestMapping(value = "/v1/sermon-series/{sermonSeriesId}/sermons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConsistentClock clock;

    private final SermonRepository sermonRepository;

    public SermonController(ConsistentClock clock, SermonRepository sermonRepository) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sermonRepository = Objects.requireNonNull(sermonRepository, "sermon repository");
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<PersistentSermon> listSermons(@PathVariable("sermonSeriesId") String sermonSeriesId) {
        return sermonRepository.list(sermonSeriesId);
    }

    @RequestMapping(value = "/{sermonId}", method = RequestMethod.GET)
    @ResponseBody
    public PersistentSermon get(@PathVariable("sermonSeriesId") String sermonSeriesId, @PathVariable("sermonId") String sermonId) {
        return sermonRepository.get(sermonSeriesId, sermonId);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersistentSermon addSermon(@PathVariable("sermonSeriesId") String sermonSeriesId, @RequestBody TransientSermon sermon) {
        logger.debug("adding {} sermon {}", sermonSeriesId, sermon);

        PersistentSermon persistentPersistentSermon = sermonRepository.save(sermonSeriesId, sermon, clock.now());

        return persistentPersistentSermon;
    }
}
