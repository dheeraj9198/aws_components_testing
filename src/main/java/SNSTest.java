/**
 * Created by dssachan on 04/10/16.
 */

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;


public class SNSTest {
    public static void main(String[] strings) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("creds.txt")));
        String ACCESSKEY = bufferedReader.readLine();
        String SECRETKEY = bufferedReader.readLine();
        final BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESSKEY, SECRETKEY);

/*
        initReceiver(credentials);
*/
        initSender(credentials);
    }

    private static void initSender(AWSCredentials credentials) {
        // Create a client
        AmazonSNSClient service = new AmazonSNSClient(credentials);

        service.setRegion(Region.getRegion(Regions.US_WEST_2));
        // Create a topic
        CreateTopicRequest createReq = new CreateTopicRequest()
                .withName("MyTopic");
        CreateTopicResult createRes = service.createTopic(createReq);

        final String arn = createRes.getTopicArn();
        // Publish to a topic
        PublishRequest publishReq = new PublishRequest()
                .withTopicArn(createRes.getTopicArn())
                .withSubject("hello")
                .withMessage("Example notification sent at " + new Date());
        PublishResult publishResult = service.publish(publishReq);
        System.out.println(publishResult.getMessageId());
    }

    private static void initReceiver(AWSCredentials credentials) {
        // Create a client
        AmazonSNSClient service = new AmazonSNSClient(credentials);
        service.setRegion(Region.getRegion(Regions.US_WEST_2));

        // Create a topic
        CreateTopicRequest createReq = new CreateTopicRequest()
                .withName("MyTopic");
        CreateTopicResult createRes = service.createTopic(createReq);

//subscribe to an SNS topic
        SubscribeRequest subRequest = new SubscribeRequest(createRes.getTopicArn(), "email", "xyz@gmail.com");
        service.subscribe(subRequest);
//get request id for SubscribeRequest from SNS metadata
        System.out.println("SubscribeRequest - " + service.getCachedResponseMetadata(subRequest));
        System.out.println("Check your email and confirm subscription.");

    }
}
