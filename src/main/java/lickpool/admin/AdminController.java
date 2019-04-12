package lickpool.admin;

import java.math.BigDecimal;
import java.util.List;
import lickpool.lickbanker.LickBankerService;
import lickpool.records.LickPoolRecords;
import lickpool.records.LickPoolRecordsComparator;
import lickpool.records.LickPoolRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private AdminControllerService adminControllerService;
    private LickBankerService lickBankerService;
    private LickPoolRecordsService lickPoolRecordsService;

    @Autowired
    AdminController(AdminControllerService adminControllerService, LickBankerService lickBankerService, LickPoolRecordsService lickPoolRecordsService) {
        this.adminControllerService = adminControllerService;
        this.lickBankerService = lickBankerService;
        this.lickPoolRecordsService = lickPoolRecordsService;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String showPage(ModelMap model) {
        model.put("lickBankers", lickBankerService.getLickBankers());
        List<LickPoolRecords> lickPoolRecordsList =  lickPoolRecordsService.getRecordsForAll();

        lickPoolRecordsList.sort(new LickPoolRecordsComparator());

        if (lickPoolRecordsList != null) {
            model.put("lickPoolRecordsList", lickPoolRecordsList);
        }
        return "admin";
    }

    @RequestMapping(value = "/admin/newPoolPeriod", method = RequestMethod.POST)
    public String newPoolPeriod(@RequestParam(required = false) String luckyBastard, @RequestParam
            BigDecimal bettingAmount) {
        adminControllerService.newPoolPeriod(luckyBastard, bettingAmount);
        showPage(new ModelMap());
        return "redirect:/admin";
    }
}