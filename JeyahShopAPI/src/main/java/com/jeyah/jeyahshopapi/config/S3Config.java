package com.jeyah.jeyahshopapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${s3.endpoint}")
    private String ENDPOINT;
    @Value("${s3.access-key}")
    private String ACCESS_KEY;
    @Value("${s3.secret-key}")
    private String SECRET_KEY;


    @Bean
    public S3Client s3Client() throws Exception {
        return S3Client.builder()
                .endpointOverride(new URI(ENDPOINT))
                .region(Region.US_EAST_1) // required but ignored
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
