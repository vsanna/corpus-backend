package dev.ishikawa.corpus.repository.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import dev.ishikawa.corpus.configuration.property.AwsProperties;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class FileRepositoryS3 implements FileRepository, InitializingBean {

    private final AwsProperties awsProperties;
    private final AmazonS3 s3Client;

    public Optional<byte[]> getObject(String key) {
        String bucket = awsProperties.getBucketName();
        try {
            if (!s3Client.doesObjectExist(bucket, key)) {
                return Optional.empty();
            }
            S3Object response = s3Client.getObject(bucket, key);
            return Optional.of(response.getObjectContent().readAllBytes());
        } catch (Exception e) {
            log.warn("couldn't handle request or response from s3 with bucket:{} and key:{}.",
                    bucket, key);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putObject(String key, byte[] data) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        log.info("Uploading S3 object at key: {}", key);
        s3Client.putObject(awsProperties.getBucketName(), key, new ByteArrayInputStream(data),
                objectMetadata);
    }

    @Override
    public void afterPropertiesSet() {
        if (!s3Client.doesBucketExistV2(awsProperties.getBucketName())) {
            log.info("Amazon S3 bucket={} does not exist, creating.",
                    awsProperties.getBucketName());
            s3Client.createBucket(awsProperties.getBucketName());
        }
    }
}
