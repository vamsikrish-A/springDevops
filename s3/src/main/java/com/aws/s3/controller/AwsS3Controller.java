package com.aws.s3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aws.s3.service.AwsS3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class AwsS3Controller {

    @Autowired
    private AwsS3Service awsS3Service;

    @PostMapping("/uploadfile")
    public ResponseEntity<String> postMethodName(
        @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folderName,
            @RequestParam("fileName") String fileName,
            @RequestParam("bucketName") String bucketName
    ) {
        //TODO: process POST request
        String filenameString = fileName+"_"+file.getOriginalFilename();
        String response = awsS3Service.uploadFile(bucketName, folderName, filenameString, file);
        
        return ResponseEntity.ok("File Uploaded successfully :: {}"+response);
    }
    
    @GetMapping(value = "/getfile", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getMethodName(@RequestParam("url") String url,
                                     @RequestParam("template") String template,
                                     @RequestParam("bucketName") String bucketName) {
        byte[] img = awsS3Service.downloadFromS3(url, template, bucketName);
        return ResponseEntity.ok(img);
    }
    

}
