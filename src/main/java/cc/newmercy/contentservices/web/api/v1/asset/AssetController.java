package cc.newmercy.contentservices.web.api.v1.asset;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import cc.newmercy.contentservices.aws.AssetStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequestMapping(value = "/v1/assets", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AssetRepository assetRepository;

    private final AssetStorage assetService;

    public AssetController(AssetRepository assetRepository, AssetStorage assetService) {
        this.assetRepository = checkNotNull(assetRepository, "asset repository");
        this.assetService = checkNotNull(assetService, "asset service");
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public TemporaryAsset upload(@RequestParam("file") MultipartFile file) throws IOException {
        TransientAsset transientAsset = new TransientAsset();
        transientAsset.setLength(file.getSize());
        transientAsset.setContentType(file.getContentType());

        logger.debug("saving temporary asset {}", transientAsset);

        TemporaryAsset temporaryAsset = assetRepository.save(transientAsset);

        assetService.save("tmp/" + temporaryAsset.getId(), file.getSize(), file.getInputStream());

        return temporaryAsset;
    }
}
