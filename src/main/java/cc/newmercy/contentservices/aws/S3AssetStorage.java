package cc.newmercy.contentservices.aws;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import cc.newmercy.contentservices.web.api.v1.sermon.TransientAsset;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3AssetStorage implements AssetStorage {

    private final String bucketName;

    private final AmazonS3 s3;

    public S3AssetStorage(String bucketName, AmazonS3 s3) {
        this.bucketName = checkNotNull(bucketName, "bucket name");
        this.s3 = checkNotNull(s3, "s3");
    }

    @Override
    public void save(String key, TransientAsset transientAsset, InputStream data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(transientAsset.getLength());
        metadata.setContentType(transientAsset.getContentType());

        s3.putObject(bucketName, key, data, metadata);
    }
}
