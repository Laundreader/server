package com.laundreader.external.NCPObjectStorage;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class NcpStorageConfig {
	private final String endPoint = "https://kr.object.ncloudstorage.com";

	private final String region = "kr-standard";

	@Value("${ncp.accessKey}")
	private String accessKey;

	@Value("${ncp.secretKey}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		S3Configuration s3Config = S3Configuration.builder()
			.checksumValidationEnabled(false) // 응답 검증 비활성화
			.build();

		return S3Client.builder()
			.serviceConfiguration(s3Config)
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)
			))
			.endpointOverride(URI.create(endPoint))
			.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)
			))
			.endpointOverride(URI.create(endPoint))
			.build();
	}
}
