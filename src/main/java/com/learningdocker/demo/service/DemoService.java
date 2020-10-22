package com.learningdocker.demo.service;

import com.learningdocker.demo.data.DataObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoService {

    @Value("${cloud.aws.credentials.accessKey}")
    private String key;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    private S3Client s3Client;

    @PostConstruct
    public void initialize() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(key, secretKey);

        s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider
                .create(awsCreds)).region(Region.US_EAST_1).build();
    }

    public void uploadFile(DataObject dataObject) throws S3Exception,
            AwsServiceException, SdkClientException, URISyntaxException,
            FileNotFoundException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("apigatewaylambdas3bucket").key(dataObject.getName()).acl(ObjectCannedACL.PUBLIC_READ).build();
        File file = new File(getClass().getClassLoader().getResource(dataObject.getName()).getFile());

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    public void downloadFile(DataObject dataObject) throws NoSuchKeyException, S3Exception, AwsServiceException, SdkClientException, IOException {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("apigatewaylambdas3bucket").key(dataObject.getName()).build();
        Resource resource = new ClassPathResource("");
        s3Client.getObject(getObjectRequest, Paths.get(resource.getFile().getPath() + "/test.txt"));
    }

    public DataObject addBucket(DataObject dataObject) {
        dataObject.setName(dataObject.getName() + System.currentTimeMillis());

        CreateBucketRequest createBucketRequest = CreateBucketRequest
                .builder()
                .bucket(dataObject.getName()).build();

        s3Client.createBucket(createBucketRequest);
        return dataObject;
    }

    public List<String> listBuckets() {
        List<String> names = new ArrayList<>();
        ListBucketsRequest listBucketsRequest = ListBucketsRequest
                .builder().build();
        ListBucketsResponse listBucketsResponse = s3Client
                .listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream()
                .forEach(x -> names.add(x.name()));
        return names;
    }

    public List<String> listObjects() {

        List<String> names = new ArrayList<>();

        ListObjectsRequest listObjectsRequest =
                ListObjectsRequest.builder().bucket("apigatewaylambdas3bucket").build();

        ListObjectsResponse listObjectsResponse = s3Client
                .listObjects(listObjectsRequest);

        listObjectsResponse.contents().stream()
                .forEach(x -> names.add(x.key()));
        return names;
    }

    public List<String> listObjects(String bucket) {

        List<String> names = new ArrayList<>();

        ListObjectsRequest listObjectsRequest =
                ListObjectsRequest.builder().bucket(bucket).build();

        ListObjectsResponse listObjectsResponse = s3Client
                .listObjects(listObjectsRequest);

        listObjectsResponse.contents().stream()
                .forEach(x -> names.add(x.key()));
        return names;
    }

    public void deleteFile(DataObject dataObject) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket("apigatewaylambdas3bucket").key(dataObject.getName()).build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public void deleteBucket(String bucket) {

        List<String> keys = this.listObjects(bucket);
        List<ObjectIdentifier> identifiers = new ArrayList<>();

        int iteration = 0;

        for (String key : keys) {
            ObjectIdentifier objIdentifier = ObjectIdentifier.builder()
                    .key(key).build();
            identifiers.add(objIdentifier);
            iteration++;

            if (iteration == 1000) {
                iteration = 0;
                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(bucket).delete(Delete.builder()
                                .objects(identifiers).build()).build();
                s3Client.deleteObjects(deleteObjectsRequest);
                identifiers.clear();
            }

        }

        if (identifiers.size() > 0) {
            DeleteObjectsRequest deleteObjectsRequest =
                    DeleteObjectsRequest.builder().bucket(bucket)
                            .delete(Delete.builder().objects(identifiers)
                                    .build()).build();
            s3Client.deleteObjects(deleteObjectsRequest);
        }

        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucket).build();
        s3Client.deleteBucket(deleteBucketRequest);
    }


    /*
    AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create("my_access_key", "my_secret_key"));
    S3AsyncClient s3Client;
    try {
        s3Client = S3AsyncClient.builder().credentialsProvider(creds)
                .region(Region.US_WEST_1)
                .endpointOverride(new URI("https://someobjectstorage.server.com:9021"))
                .build();
        CompletableFuture<GetObjectResponse> futureGet = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket("my_bucket_name")
                        .key("/somepath/anotherpath/myData.pdf")
                        .build(),
                AsyncResponseTransformer.toFile(Paths.get("/osfile/myfile.pdf")));
        futureGet.get();
    } catch (URISyntaxException e1) {
        e1.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
}


@Configuration
public class S3Config {

    @Bean(destroyMethod = "close")
    public S3Client s3Client() {
        return  S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
     */
}
