import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;

/**
 * Created by dssachan on 23/09/16.
 */
public class DyanmoDBTest {

    public static void main(String[] args) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("creds.txt")));
        String ACCESSKEY = bufferedReader.readLine();
        String SECRETKEY = bufferedReader.readLine();
        BasicAWSCredentials creds = new BasicAWSCredentials(ACCESSKEY,SECRETKEY);
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(creds);
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        String tableName = "dheeraj";

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName);
        ScanResult result = dynamoDBClient.scan(scanRequest);

        ListTablesResult tables = dynamoDBClient.listTables();

        DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
        Table table = dynamoDB.getTable(tableName);


        String pk = UUID.randomUUID().toString();
        Item item = new Item()
                .withPrimaryKey("pk",pk )
                .withString("Title", "Book 120 Title")
                .withString("ISBN", "120-1111111111")
                .withNumber("Price", 20)
                .withString("Dimensions", "8.5x11.0x.75")
                .withNumber("PageCount", 500)
                .withBoolean("InPublication", false)
                .withString("ProductCategory", "Book");
        PutItemOutcome putItemOutcome = table.putItem(item);

        Item item1 = table.getItem("pk", pk);

        System.out.println(item1.toJSONPretty());
    }
}
