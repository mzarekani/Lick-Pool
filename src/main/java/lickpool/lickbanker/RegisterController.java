package lickpool.lickbanker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private LickBankerService lickBankerService;
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    public RegisterController(LickBankerService lickBankerService) {
        this.lickBankerService = lickBankerService;
    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showLoginPage(){
        return "register";
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String showWelcomePage(ModelMap model, @RequestParam String name, @RequestParam String password){
        if (name.equals("") || password.equals("")) {
            model.put(ERROR_MESSAGE, "Invalid Credentials");
            return "register";
        }
        if (lickBankerService.checkIfAccountExists(name)) {
            model.put(ERROR_MESSAGE, "Username exists. Are you trying to impersonate somebody you silly goose?");
            return "register";
        }
        if(!lickBankerService.createLickBanker(name, password)) {
            model.put(ERROR_MESSAGE, "Something went wrong yo");
            return "register";
        }
        return "redirect:/login";
        //return "redirect:/welcome?name=" + name;
    }

}