package cc.newmercy.contentservices.aws;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

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
    public void save(String key, long length, InputStream data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(length);

        s3.putObject(bucketName, key, data, metadata);
    }
}
