package lickpool;

import java.util.List;
import lickpool.DatabaseConfig.LickPoolConfigService;
import lickpool.lickbanker.LickBankerService;
import lickpool.records.LickPoolRecords;
import lickpool.records.LickPoolRecordsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WelcomeController {

    @Autowired
    LoginService service;
    @Autowired
    LickBankerService lickBankerService;
    @Autowired
    LickPoolRecordsService lickPoolRecordsService;
    @Autowired
    LickPoolConfigService lickPoolConfigService;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcomePage(ModelMap model) {
        if (!lickPoolConfigService.isLickPoolInitialized()) {
            return "redirect:/admin";
        }
        String currentUser = lickBankerService.getCurrentUser();

        List<String> lickBankers = lickBankerService.getLickBankers();
        lickBankers.remove(currentUser.contains(" ") ? currentUser.substring(0, currentUser.indexOf(' ')) : currentUser);

        model.put("userName", currentUser);
        model.put("lickBankers", lickBankers);
        model.put("totalPool", "10000");
        model.put("currentIteration", lickPoolConfigService.getCurrentIteration());
        model.put("bettingAmount", lickPoolConfigService.getBettingAmount());

        List<LickPoolRecords> lickPoolRecordsList =  lickPoolRecordsService.getRecordsForUser(currentUser);
        if (lickPoolRecordsList != null) {
            model.put("lickPoolRecordsList", lickPoolRecordsList);
        }
        return "welcome";
    }

    @PostMapping(value = "/welcome")
    public String showWelcomePage() {
        return "redirect:/transactions";
    }

    @PostMapping(value = "/welcome/addBet")
    @ResponseBody
    public ResponseEntity<String> addBet(final @RequestParam("betOn") String betOn) {
        String currentUser = lickBankerService.getCurrentUser();
        if (lickPoolRecordsService.checkIfRecordExists(currentUser)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        LickPoolRecords lickPoolRecords = lickPoolRecordsService.addBet(currentUser, betOn);
        return new ResponseEntity<>(new JSONObject().put("iteration", lickPoolRecords.getIteration())
                                                    .put("startDate", lickPoolRecords.getStartDate())
                                                    .put("bettingAmount", lickPoolRecords.getBettingAmount())
                                                    .put("totalPool", lickPoolRecords.getTotalPool()).toString(),
                                    HttpStatus.OK);
    }
}