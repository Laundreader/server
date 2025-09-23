package com.laundreader.external.NCPObjectStorage;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class NcpStorageService {
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public String uploadFile(String bucketName, MultipartFile file, String key) {
		try (InputStream inputStream = file.getInputStream()) {
			// S3 업로드 요청 생성
			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();

			// 파일 업로드
			s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			// 업로드된 파일의 URI 반환
			return key;

		} catch (S3Exception e) {
			throw new Exception500("Amazon S3 error while uploading file: " + e.awsErrorDetails().errorMessage());
		} catch (IOException e) {
			throw new Exception500("IO error while uploading file: " + e.getMessage());
		}
	}

	public void deleteFile(String bucketName, String key) {
		try {
			s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
		} catch (S3Exception e) {
			throw new Exception500("Delete failed: " + e.awsErrorDetails().errorMessage());
		}
	}

	public String generateGetPresignedUrl(String bucketName, String key, long expirationSeconds) {
		try {
			// 서명된 URL 생성 요청 설정
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofSeconds(expirationSeconds))
				.getObjectRequest(getObjectRequest)
				.build();

			return s3Presigner.presignGetObject(presignRequest).url().toString();
		} catch (S3Exception e) {
			throw new Exception500("Failed to generate signed URL: " + e.awsErrorDetails().errorMessage());
		} catch (Exception e) {
			throw new Exception500("Failed to generate signed URL: " + ErrorMessage.INTERNAL_ERROR);
		}
	}
}
