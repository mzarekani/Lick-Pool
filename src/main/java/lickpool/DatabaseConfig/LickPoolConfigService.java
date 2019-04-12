package lickpool.DatabaseConfig;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LickPoolConfigService {

    private static final String LICK_POOL_HASH_KEY = "config";
    private DynamoDBMapper mapper;
    private LickPoolConfig lickPoolConfig;

    @Autowired
    public LickPoolConfigService(DynamoDBConfig dynamoDBConfig) {
        mapper = new DynamoDBMapper(dynamoDBConfig.amazonDynamoDB());
        lickPoolConfig = mapper.load(LickPoolConfig.class, LICK_POOL_HASH_KEY);
    }

    public void loadLickPoolConfig() {
        lickPoolConfig = mapper.load(LickPoolConfig.class, LICK_POOL_HASH_KEY);
    }

    public Integer getCurrentIteration() {
        return lickPoolConfig.getIteration();

    }

    public String getStartDate() {
        return lickPoolConfig.getStartDate();
    }

    public List<String> getLeavers() {
        return lickPoolConfig.getLeavers();

    }

    public BigDecimal getBettingAmount() {
        return lickPoolConfig.getBettingAmount();
    }

    public void save(LickPoolConfig lickPoolConfig) {
        mapper.save(lickPoolConfig);
        this.lickPoolConfig = lickPoolConfig;
    }

    public boolean isLickPoolInitialized() {
        return lickPoolConfig != null;
    }
}