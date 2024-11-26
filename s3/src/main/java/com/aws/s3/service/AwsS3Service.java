package com.aws.s3.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class AwsS3Service {
    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);

    @Autowired
    private S3Client s3Client;

    public String uploadFile(String bucketName, String folderName, String fileName, MultipartFile file) {
        try {
            logger.info("Uploading file to S3 bucket: {}", bucketName);
            String keyName = folderName + "/" + fileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            logger.info("PutOjectResponse  {}", response.checksumSHA256());//null
            logger.info("File uploaded successfully: {}", keyName);//template/54321_awsCognito.png
            GetUrlRequest urlRequest = GetUrlRequest.builder().bucket(bucketName).key(keyName).build();
            logger.info("GetUrlRequest: {}", urlRequest.toString());

            URI uri = s3Client.utilities().getUrl(urlRequest).toURI();
            logger.info("with uri: {}", uri);
            logger.info("with parseuri: {}", s3Client.utilities().parseUri(uri).toString());

            return uri.toString();

        } catch (Exception e) {
            logger.error("Error uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }


        
    }

    public byte[] downloadFromS3(String s3Url, String templateId, String bucketName) {
    if (s3Url == null || s3Url.isEmpty()) {
        throw new IllegalArgumentException("s3Url cannot be null or empty");
    }
    if (templateId == null || templateId.isEmpty()) {
        throw new IllegalArgumentException("templateId cannot be null or empty");
    }

    try {
        // Extract S3 key from the S3 URL
        String filePath = extractKeyFromS3Url(s3Url);

        // Log the values for debugging
        System.out.println("Bucket Name: " + bucketName);
        System.out.println("File Path: " + filePath);

        // Create the GetObjectRequest
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(filePath)
                .bucket(bucketName)
                .build();

        // Get the object bytes
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(objectRequest, ResponseTransformer.toBytes());

        // if (objectBytes == null) {
        //     return objectBytes.asByteArray();
        // }

        // Return the byte array
        return objectBytes.asByteArray();

    } catch (IllegalArgumentException e) {
        System.err.println("Invalid Input: " + e.getMessage());
        throw e;
    } catch (NoSuchKeyException e) {
        System.err.println("File not found in S3: " + e.awsErrorDetails().errorMessage());
        throw  e;
    } catch (S3Exception e) {
        System.err.println("Error accessing S3: " + e.awsErrorDetails().errorMessage());
        throw  e;
    } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        throw e;
    }
}

/**
 * Helper method to extract the S3 key from the S3 URL.
 */
private String extractKeyFromS3Url(String s3Url) {
    try {
        URI uri = new URI(s3Url);
        String path = uri.getPath(); // e.g., /folder/file.txt
        if (path.startsWith("/")) {
            path = path.substring(1); // Remove leading slash
        }
        return path;
    } catch (URISyntaxException e) {
        throw new IllegalArgumentException("Invalid S3 URL format: " + s3Url, e);
    }
}



}
