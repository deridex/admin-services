package cc.newmercy.contentservices.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class S3AssetStorage implements AssetStorage {

    private final String bucketName;

    private final AmazonS3 s3;

    public S3AssetStorage(String bucketName, AmazonS3 s3) {
        this.bucketName = checkNotNull(bucketName, "bucket name");
        this.s3 = checkNotNull(s3, "s3");
    }

    @Override
    public void save(String key, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        try {
            s3.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
