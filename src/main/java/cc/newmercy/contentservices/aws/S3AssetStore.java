package cc.newmercy.contentservices.aws;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3AssetStore implements AssetStore {

    private final AmazonS3 s3;

    private final String bucketName;

    public S3AssetStore(AmazonS3 s3, String bucketName) {
        this.bucketName = checkNotNull(bucketName, "bucket name");
        this.s3 = checkNotNull(s3, "s3");
    }

    @Override
    public void save(String key, ObjectMetadata metadata, InputStream data) {
        s3.putObject(bucketName, key, data, metadata);
    }

    @Override
    public void delete(String key) {
        s3.deleteObject(bucketName, key);
    }
}
