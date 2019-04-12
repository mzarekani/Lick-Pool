package lickpool.lickbanker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lickpool.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @Autowired
    LoginService service;

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String showLoginPage(ModelMap model){
        return "login";
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String showWelcomePage(ModelMap model, @RequestParam String name, @RequestParam String password){

        Boolean authenticated = false;
        if(!authenticated){
            authenticated = service.validateUser(name, password);
        }
        if (!authenticated) {
            model.put("errorMessage", "Invalid Credentials");
            model.put("authenticated", false);
            return "login";
        }

        model.put("name", name);
        model.put("password", password);
        model.put("authenticated", true);


        return "redirect:/welcome?name=" + name;
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.setAuthenticated(false);
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.clearContext();
        request.logout();
        request.getSession().invalidate();
        return "redirect:/login";
    }

    @RequestMapping(value="/authenticated", method = RequestMethod.GET)
    @ResponseBody
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return null != authentication && !("anonymousUser").equals(authentication.getName());
    }
}