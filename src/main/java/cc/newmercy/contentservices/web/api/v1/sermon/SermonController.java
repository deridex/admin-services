package cc.newmercy.contentservices.web.api.v1.sermon;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cc.newmercy.contentservices.aws.AssetStore;
import cc.newmercy.contentservices.web.id.IdService;
import cc.newmercy.contentservices.web.time.ConsistentClock;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequestMapping(value = "/v1/sermon-series/{sermonSeriesId}/sermons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SermonController {

    private static final String ASSET_ID_SERIES_NAME = "asset";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConsistentClock clock;

    private final SermonRepository sermonRepository;

    private final IdService idService;

    private final SermonAssetRepository sermonAssetRepository;

    private final String assetS3KeyPrefix;

    private final AssetStore assetStore;

    public SermonController(
            ConsistentClock clock,
            SermonRepository sermonRepository,
            IdService idService,
            SermonAssetRepository sermonAssetRepository,
            String assetS3KeyPrefix,
            AssetStore assetStore) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sermonRepository = Objects.requireNonNull(sermonRepository, "sermon repository");
        this.idService = Objects.requireNonNull(idService, "id service");
        this.sermonAssetRepository = Objects.requireNonNull(sermonAssetRepository, "sermon asset repository");

        Objects.requireNonNull(assetS3KeyPrefix, "key prefix");
        Preconditions.checkArgument(!assetS3KeyPrefix.isEmpty(), "key prefix");
        this.assetS3KeyPrefix = assetS3KeyPrefix;

        this.assetStore = Objects.requireNonNull(assetStore, "asset storage");
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

        String assetId = idService.next(ASSET_ID_SERIES_NAME);

        TransientAsset transientAsset = new TransientAsset();
        transientAsset.setId(assetId);
        transientAsset.setLength(file.getSize());
        transientAsset.setContentType(file.getContentType());
        transientAsset.setKey(String.format("%s/%tY/%2$tY%2$tm%2$td-%s-%s",
                assetS3KeyPrefix,
                sermon.getDate(),
                assetId,
                name));

        logger.debug("saving temporary asset {}", transientAsset);

        SermonAsset asset = sermonAssetRepository.save(sermonId, sermonVersion, transientAsset);

        assetStore.save(transientAsset, file.getInputStream());

        return asset;
    }

    @RequestMapping(value = "/{sermonId}/assets", method = RequestMethod.GET)
    @ResponseBody
    public List<SermonAsset> listAssets(@PathVariable("sermonId") String sermonId, @RequestParam("v") int sermonVersion) {
        return sermonAssetRepository.list(sermonId, sermonVersion);
    }
}
