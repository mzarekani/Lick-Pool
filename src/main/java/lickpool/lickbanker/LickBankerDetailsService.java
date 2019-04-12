package lickpool.lickbanker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/***
 * This service is only for Oauth. May be combined with LickBankerService later
 ***/
public class LickBankerDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    LickBankerService lickBankerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        LickBanker user = lickBankerService.getLickBanker(username);

        User.UserBuilder builder;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(user.getHashedPassword());
            builder.roles(user.getRole());
        } else {
            throw new UsernameNotFoundException("User not found.");
        }

        return builder.build();
    }

}
