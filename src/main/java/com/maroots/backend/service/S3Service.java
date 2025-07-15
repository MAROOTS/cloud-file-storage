package com.maroots.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
      @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.endpoint}")  // For MinIO
    private String endpoint;

    private S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey,secretKey)
                ))
                .endpointOverride(URI.create(endpoint)) //required for MiniO
                .forcePathStyle(true) //required for MiniO
                .build();
    }
    public String uploadFile(MultipartFile file) throws IOException {
        S3Client s3 = getS3Client();
        try {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ignored){

        }
        String key = UUID.randomUUID() + "." + file.getOriginalFilename();

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
        return key;
    }


    public byte[] downloadFile(String key) {
        S3Client s3 = getS3Client();

        ResponseBytes<GetObjectResponse> objectBytes = s3.getObject(
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(),
            ResponseTransformer.toBytes()
        );

    return objectBytes.asByteArray();
}

}
