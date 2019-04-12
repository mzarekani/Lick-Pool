package lickpool;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public boolean validateUser(String userid, String password) {
        return userid.equalsIgnoreCase("matt")
               && password.equalsIgnoreCase("password");
    }

}