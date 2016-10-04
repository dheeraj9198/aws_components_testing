import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Created by dssachan on 04/10/16.
 */
public class SQSTest {

    public static void main(String[] args) throws Exception {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("creds.txt")));
        String ACCESSKEY = bufferedReader.readLine();
        String SECRETKEY = bufferedReader.readLine();
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESSKEY,SECRETKEY);
        final AmazonSQS sqs = new AmazonSQSClient(credentials);

        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(usWest2);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {
            // Create a queue
            System.out.println("Creating a new SQS queue called MyQueue.\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
            final String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

            // List queues
            System.out.println("Listing all queues in your account.\n");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();

            new Thread(new Runnable() {
                public void run() {
                    // Send a message
                    /*try{
                        Thread.sleep(10000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }*/
                    System.out.println("Sending a message to MyQueue.\n");
                    sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));
                }
            }).start();

            // Receive messages
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("Receiving messages from MyQueue.\n");
                    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
                    List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
                    for (Message message : messages) {
                        System.out.println("  Message");
                        System.out.println("    MessageId:     " + message.getMessageId());
                        System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                        System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                        System.out.println("    Body:          " + message.getBody());
                        for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                            System.out.println("  Attribute");
                            System.out.println("    Name:  " + entry.getKey());
                            System.out.println("    Value: " + entry.getValue());
                        }

                        // Delete a message
                        System.out.println("Deleting a message.\n");
                        String messageReceiptHandle = message.getReceiptHandle();
                        sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
                    }
                }
            }).start();
            System.out.println();

            // Delete a queue
            //System.out.println("Deleting the test queue.\n");
            //sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
        } catch (AmazonServiceException ase) {
            throw ase;
        } catch (AmazonClientException ace) {
          throw ace;
        }
    }
}
