package cc.newmercy.contentservices.web.api.v1.sermon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Transactional
@RequestMapping(value = "/v1/sermon-series/{sermonSeriesId}/sermons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<?, ?> add(@PathVariable("sermonSeriesId") String sermonSeriesId, @RequestBody TransientSermon sermon) {
        logger.debug("adding {} sermon {}", sermonSeriesId, sermon);

        return Collections.emptyMap();
    }
}
