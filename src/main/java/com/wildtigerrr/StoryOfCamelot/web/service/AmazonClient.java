package com.wildtigerrr.StoryOfCamelot.web.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;

@Service("amazonClient")
public class AmazonClient {

    private String bucketName = "storyofcameloteu";

    private AmazonS3 s3client;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(System.getenv("AWS_S3_ID"), System.getenv("AWS_S3_KEY"));
        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public InputStream getObject(String filePath) {
        S3Object object = s3client.getObject(new GetObjectRequest(
                bucketName,
                filePath
        ));
        return object.getObjectContent();
    }

    public void saveString(String name, String data) {
        s3client.putObject(bucketName, name, data);
    }

    public void saveFile(String name, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, name, file));
//        PutObjectRequest request = new PutObjectRequest(bucketName, path + name, file);
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType("plain/text");
//        metadata.addUserMetadata("x-amz-meta-title", "someTitle");
//        request.setMetadata(metadata);
//        s3client.putObject(request);
    }

}
