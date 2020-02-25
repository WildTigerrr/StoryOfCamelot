package com.wildtigerrr.StoryOfCamelot.web.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.wildtigerrr.StoryOfCamelot.web.service.DataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;

@Service
@Profile("!test")
public class AmazonClient implements DataProvider {

    private String bucketName = "storyofcameloteu";

    private AmazonS3 s3client;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(getEnvId(), getEnvKey());
        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Override
    public InputStream getObject(String filePath) {
        S3Object object = s3client.getObject(new GetObjectRequest(
                bucketName,
                filePath
        ));
        return object.getObjectContent();
    }

    @Override
    public void saveString(String name, String data) {
        s3client.putObject(bucketName, name, data);
    }

    @Override
    public void saveFile(String name, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, name, file));
    }

    @Value("${AWS_S3_ID}")
    private String awsIdProperty;
    @Value("${AWS_S3_KEY}")
    private String awsKeyProperty;

    private String getEnvId() {
        String id = System.getenv("AWS_S3_ID");
        if (id == null) return awsIdProperty;
        else return id;
    }

    private String getEnvKey() {
        String key = System.getenv("AWS_S3_KEY");
        if (key == null) return awsKeyProperty;
        else return key;
    }

}
