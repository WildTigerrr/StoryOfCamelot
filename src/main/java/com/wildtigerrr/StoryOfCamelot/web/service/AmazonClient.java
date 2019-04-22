package com.wildtigerrr.StoryOfCamelot.web.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class AmazonClient {

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
                "storyofcameloteu",
                filePath
        ));
        return object.getObjectContent();
    }

}
