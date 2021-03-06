package cc.newmercy.contentservices.web.api.v1.sermon;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cc.newmercy.contentservices.web.time.ConsistentClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequestMapping(value = "/v1/sermon-series/{sermonSeriesId}/sermons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConsistentClock clock;

    private final SermonRepository sermonRepository;

    private final SermonAssetRepository sermonAssetRepository;

    public SermonController(
            ConsistentClock clock,
            SermonRepository sermonRepository,
            SermonAssetRepository sermonAssetRepository) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sermonRepository = Objects.requireNonNull(sermonRepository, "sermon repository");
        this.sermonAssetRepository = Objects.requireNonNull(sermonAssetRepository, "sermon asset repository");
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

    @RequestMapping(value = "/{sermonId}", method = RequestMethod.PUT)
    @ResponseBody
    public PersistentSermon update(
            @PathVariable("sermonId") String sermonId,
            @RequestParam("v") int version,
            @RequestBody PersistentSermon editedSermon) {
        return sermonRepository.update(sermonId, version, editedSermon);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersistentSermon addSermon(
            @PathVariable("sermonSeriesId") String sermonSeriesId,
            @RequestParam("v") int sermonSeriesVersion,
            @RequestBody TransientSermon sermon) {
        logger.debug("adding {} sermon {}", sermonSeriesId, sermon);

        PersistentSermon persistentPersistentSermon = sermonRepository.save(sermonSeriesId, sermonSeriesVersion, sermon, clock.now());

        return persistentPersistentSermon;
    }

    @RequestMapping(value = "/{sermonId}/assets", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public SermonAsset addAsset(
            @PathVariable("sermonSeriesId") String sermonSeriesId,
            @PathVariable("sermonId") String sermonId,
            @RequestParam("v") int sermonVersion,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws IOException {
        PersistentSermon sermon = sermonRepository.get(sermonSeriesId, sermonId);

        TransientAsset transientAsset = new TransientAsset();
        transientAsset.setLength(file.getSize());
        transientAsset.setContentType(file.getContentType());
        transientAsset.setName(name);
        transientAsset.setDate(sermon.getDate());

        logger.debug("saving temporary asset {}", transientAsset);

        return sermonAssetRepository.save(sermonId, sermonVersion, transientAsset, file.getInputStream());
    }

    @RequestMapping(value = "/{sermonId}/assets", method = RequestMethod.GET)
    @ResponseBody
    public List<SermonAsset> listAssets(@PathVariable("sermonId") String sermonId) {
        return sermonAssetRepository.list(sermonId);
    }

    @RequestMapping(value = "/{sermonId}/assets/{assetId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAsset(@PathVariable("sermonId") String sermonId, @PathVariable("assetId") String assetId) {
        sermonAssetRepository.delete(sermonId, assetId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
