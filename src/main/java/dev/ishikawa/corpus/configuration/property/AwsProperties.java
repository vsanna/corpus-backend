package dev.ishikawa.corpus.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String accessKeyId;
    private String secretKey;
    private String region;
    private String overrideEndpoint;
    private String bucketName;
}
