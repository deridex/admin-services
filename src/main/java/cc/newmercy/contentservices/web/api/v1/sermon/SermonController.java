package cc.newmercy.contentservices.web.api.v1.sermon;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RequestMapping(value = "/v1/sermon-series/{sermonSeriesId}/sermons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SermonRepository sermonRepository;

    public SermonController(SermonRepository sermonRepository) {
        this.sermonRepository = checkNotNull(sermonRepository, "sermon repository");
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersistentSermon addSermon(@PathVariable("sermonSeriesId") String sermonSeriesId, @RequestBody TransientSermon sermon) {
        logger.debug("adding {} sermon {}", sermonSeriesId, sermon);

        PersistentSermon persistentSermon = sermonRepository.save(sermonSeriesId, sermon);

        return persistentSermon;
    }
}
