package lickpool.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lickpool.DatabaseConfig.LickPoolConfig;
import lickpool.DatabaseConfig.LickPoolConfigService;
import lickpool.records.LickPoolRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminControllerService {

    private LickPoolConfigService lickPoolConfigService;
    private LickPoolRecordsService lickPoolRecordsService;

    @Autowired
    public AdminControllerService(LickPoolConfigService lickPoolConfigService,
                                  LickPoolRecordsService lickPoolRecordsService) {
        this.lickPoolConfigService = lickPoolConfigService;
        this.lickPoolRecordsService = lickPoolRecordsService;
    }

    void newPoolPeriod(String luckyBastard, BigDecimal bettingAmount) {
        String todayDate = getTodayDate();
        LickPoolConfig lickPoolConfig = new LickPoolConfig();
        lickPoolConfig.setStartDate(todayDate);
        if (lickPoolConfigService.isLickPoolInitialized()) {
            lickPoolRecordsService.closePreviousPoolBets(todayDate);
            lickPoolConfig.setIteration(lickPoolConfigService.getCurrentIteration() + 1);
        } else {
            lickPoolConfig.setIteration(1);
        }
        lickPoolConfig.setBettingAmount(bettingAmount);
        if (luckyBastard != null && !luckyBastard.equals("")) {
            List<String> leavers;
            if (lickPoolConfigService.isLickPoolInitialized()) {
                 leavers = lickPoolConfigService.getLeavers();
            } else {
                leavers = new ArrayList<>();
            }
            leavers.add(luckyBastard);
            lickPoolConfig.setLeavers(leavers);
            lickPoolRecordsService.payOut(luckyBastard);
        }
        lickPoolConfigService.save(lickPoolConfig);
    }

    private String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
