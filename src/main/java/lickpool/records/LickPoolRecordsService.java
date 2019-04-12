package lickpool.records;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lickpool.DatabaseConfig.DynamoDBConfig;
import lickpool.DatabaseConfig.LickPoolConfigService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LickPoolRecordsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LickPoolRecordsService.class);

    private DynamoDBMapper mapper;
    private LickPoolConfigService lickPoolConfigService;

    @Autowired
    public LickPoolRecordsService(LickPoolConfigService lickPoolConfigService, DynamoDBConfig dynamoDBConfig) {
        this.lickPoolConfigService = lickPoolConfigService;
        mapper = new DynamoDBMapper(dynamoDBConfig.amazonDynamoDB());
    }

    public List<LickPoolRecords> getRecordsForUser(String user) {
        List<LickPoolRecords> lickPoolRecordsList = null;
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":user", new AttributeValue().withS(user));
            DynamoDBQueryExpression<LickPoolRecords> queryExpression = new DynamoDBQueryExpression<LickPoolRecords>()
                    .withKeyConditionExpression("id = :user")
                    .withExpressionAttributeValues(eav);
            lickPoolRecordsList = mapper.query(LickPoolRecords.class, queryExpression);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
            getTotalPool(lickPoolRecordsList);
            return lickPoolRecordsList;
    }

    public List<LickPoolRecords> getRecordsForAll() {
        List<LickPoolRecords> lickPoolRecordsList = null;
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            lickPoolRecordsList = mapper.scan(LickPoolRecords.class, scanExpression);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
        getTotalPool(lickPoolRecordsList);
        return new ArrayList<>(lickPoolRecordsList);
    }

    private void getTotalPool(List<LickPoolRecords> lickPoolRecordsList) {
        if (lickPoolRecordsList != null) {
            for (int i = 0; i < lickPoolRecordsList.size(); i++) {
                lickPoolRecordsList.get(i).setTotalPool(getTotalPoolForIteration(lickPoolRecordsList.get(i).getIteration()));
            }
        }
    }

    private BigDecimal getTotalPoolForIteration(Integer iteration) {
        List<LickPoolRecords> lickPoolRecordsList = null;
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":iteration", new AttributeValue().withN(iteration.toString()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("iteration = :iteration").withExpressionAttributeValues(eav);

            lickPoolRecordsList = mapper.scan(LickPoolRecords.class, scanExpression);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
        BigDecimal totalPool = BigDecimal.valueOf(0);
        if (lickPoolRecordsList != null) {
            for(LickPoolRecords lickPoolRecord : lickPoolRecordsList) {
                totalPool =  lickPoolRecord.getBettingAmount().add(totalPool);
            }
        }
        return totalPool;
    }

    private LickPoolRecords getRecord(String user) {
        LickPoolRecords lickPoolRecord = null;
        try {
            Integer iteration = lickPoolConfigService.getCurrentIteration();
            lickPoolRecord = mapper.load(LickPoolRecords.class, user, iteration);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
        return lickPoolRecord;
    }

    public LickPoolRecords addBet(String currentUser, String betOn) {
        String startDate = lickPoolConfigService.getStartDate();
        int iteration = lickPoolConfigService.getCurrentIteration();
        BigDecimal bettingAmount = lickPoolConfigService.getBettingAmount();
        LickPoolRecords lickPoolRecords = new LickPoolRecords(currentUser,
                                                              iteration, betOn, bettingAmount,
                                                              getTotalPoolForIteration(iteration).add(bettingAmount),
                                                              startDate, "Open", false, " ");
        mapper.save(lickPoolRecords);
        return lickPoolRecords;
    }

    public boolean checkIfRecordExists(String currentUser) {
        return getRecord(currentUser) != null;
    }

    private String calculateEndDate(String startDate) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date start = null;

        try {
            start = dateFormat.parse(startDate);
            start = DateUtils.addDays(start, 14);
        } catch (ParseException parseException) {
            LOGGER.debug(parseException.getMessage());
        }

        if (start != null) {
            return dateFormat.format(start);
        } else {
            return "";
        }
    }

    public void closePreviousPoolBets(String todayDate) {
        List<LickPoolRecords> lickPoolRecordsList = getOpenRecords();
        for (LickPoolRecords lickPoolRecord : lickPoolRecordsList) {
            lickPoolRecord.setEndDate(todayDate);
            mapper.save(lickPoolRecord);
        }
    }

    private List<LickPoolRecords> getOpenRecords() {
        List<LickPoolRecords> lickPoolRecordsList = null;
        try {
            Integer iteration = lickPoolConfigService.getCurrentIteration();
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":iteration", new AttributeValue().withN(iteration.toString()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("iteration = :iteration").withExpressionAttributeValues(eav);

            lickPoolRecordsList = mapper.scan(LickPoolRecords.class, scanExpression);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
        return lickPoolRecordsList;
    }

    public void payOut(String luckyBastard) {
        List<LickPoolRecords> lickPoolRecordsList = getUnpaidRecords();
        for (LickPoolRecords lickPoolRecord : lickPoolRecordsList) {
            lickPoolRecord.setPaidOut(true);
            lickPoolRecord.setWhoLeft(luckyBastard);
            mapper.save(lickPoolRecord);
        }
    }

    public List<LickPoolRecords> getUnpaidRecords() {
        List<LickPoolRecords> lickPoolRecordsList = null;
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paidOut", new AttributeValue().withN(Integer.toString(0)));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paidOut = :paidOut").withExpressionAttributeValues(eav);

            lickPoolRecordsList = mapper.scan(LickPoolRecords.class, scanExpression);
        } catch (AmazonDynamoDBException amazonDynamoDBException) {
            LOGGER.debug(amazonDynamoDBException.getMessage());
        }
        return lickPoolRecordsList;
    }

}
