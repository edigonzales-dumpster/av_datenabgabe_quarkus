package ch.so.agi.cadastraldatadisposal;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Path("/av_datenabgabe")
public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    @ConfigProperty(name = "dataServiceUrl")
    private String dataServiceUrl;

    @ConfigProperty(name = "pdfMapUrl")
    private String pdfMapUrl;

    @ConfigProperty(name = "s3BaseUrl")
    private String s3BaseUrl;

    @ConfigProperty(name = "itfsoBucketName")
    private String itfsoBucketName;

    @ConfigProperty(name = "itfchBucketName")
    private String itfchBucketName;

    @ConfigProperty(name = "dxfBucketName")
    private String dxfBucketName;
    
    @ConfigProperty(name = "shpUrl")
    private String shpUrl;

    @ConfigProperty(name = "awsAccessKey") 
    String awsAccessKey;

    @ConfigProperty(name = "awsSecretKey") 
    String awsSecretKey;

    private AWSCredentials credentials;

    @PostConstruct
    public void doLog() {
        credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        LOGGER.info("Start S3 request");
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
        
        ObjectListing objectListing = s3client.listObjects(itfsoBucketName);        
        Map<String, Date> objectMap = objectListing.getObjectSummaries().stream().collect(
                Collectors.toMap(S3ObjectSummary::getKey, S3ObjectSummary::getLastModified));
        LOGGER.info("End S3 request");

        LOGGER.info(objectMap.toString());
        
        
        return "hello";
    }

}
