package com.aws.sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;


@Configuration
public class AwsSNSConfig {

    @Bean
    SnsClient snsClient() {
		return SnsClient.builder()
				.region(Region.AP_SOUTH_1)
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create("<AccessToken>", "<SecretToken>")
								)).build();
	}
	
	
}
