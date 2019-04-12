package lickpool.records;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.math.BigDecimal;

@DynamoDBTable(tableName = "lickpool_records")
public class LickPoolRecords {
    private String id;
    private int iteration;
    private BigDecimal bettingAmount;
    private String betOn;
    private String startDate;
    private String endDate;
    private boolean paidOut;
    private BigDecimal totalPool;
    private String whoLeft;

    LickPoolRecords(String id, int iteration, String betOn, BigDecimal bettingAmount,
                    BigDecimal totalPool, String startDate, String endDate, boolean paidOut, String whoLeft) {
        this.id = id;
        this.iteration = iteration;
        this.betOn = betOn;
        this.bettingAmount = bettingAmount;
        this.totalPool = totalPool;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paidOut = paidOut;
        this.whoLeft = whoLeft;
    }

    public LickPoolRecords() {}

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey
    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    @DynamoDBAttribute
    public BigDecimal getBettingAmount() {
        return bettingAmount;
    }

    public void setBettingAmount(BigDecimal bettingAmount) {
        this.bettingAmount = bettingAmount;
    }

    @DynamoDBAttribute
    public String getBetOn() {
        return betOn;
    }

    public void setBetOn(String betOn) {
        this.betOn = betOn;
    }

    @DynamoDBAttribute
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @DynamoDBAttribute
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @DynamoDBAttribute
    public boolean getPaidOut() {
        return paidOut;
    }

    public void setPaidOut(boolean paidOut) {
        this.paidOut = paidOut;
    }

    @DynamoDBAttribute
    public String getWhoLeft() {
        return whoLeft;
    }

    public void setWhoLeft(String whoLeft) {
        this.whoLeft = whoLeft;
    }

    @DynamoDBIgnore
    public BigDecimal getTotalPool() {
        return totalPool;
    }

    public void setTotalPool(BigDecimal totalPool) {
        this.totalPool = totalPool;
    }
}
