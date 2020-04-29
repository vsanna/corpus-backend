package dev.ishikawa.corpus.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import dev.ishikawa.corpus.configuration.property.AwsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties
public class AwsUtilConfig {

    private final AwsProperties awsProperties;

    @Bean("s3Client")
    @Profile( {"dev", "stg", "platform"})
    public AmazonS3 createS3Util() {
        // credentials are automatically set from instance on k8s so we don't need to pass credentials here.
        return AmazonS3ClientBuilder.standard().build();
    }

    @Bean("s3Client")
    @Profile( {"default", "local"})
    public AmazonS3 createS3UtilLocal() {
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(
                new EndpointConfiguration(awsProperties.getOverrideEndpoint(),
                    awsProperties.getRegion()))
            .withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(awsProperties.getAccessKeyId(),
                    awsProperties.getSecretKey())))
            .build();
    }

}
