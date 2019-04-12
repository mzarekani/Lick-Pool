package lickpool.lickbanker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lickpool.DatabaseConfig.LickPoolConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LickBankerService {
    CrudRepository<LickBanker, String> repository;
    private LickPoolConfigService lickPoolConfigService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public LickBankerService(CrudRepository<LickBanker, String> repository, LickPoolConfigService lickPoolConfigService) {
        this.repository = repository;
        this.lickPoolConfigService = lickPoolConfigService;
    }

    public boolean createLickBanker(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        LickBanker lickBanker = new LickBanker(username, hashedPassword, "USER");
        repository.save(lickBanker);
        return checkIfAccountExists(username);
    }

    public boolean checkIfAccountExists(String id) {
        return repository.findById(id).isPresent();
    }

    public LickBanker getLickBanker(String userName) {
        Optional<LickBanker> lickBanker = repository.findById(userName);
        return lickBanker.map(user -> {
            return user;
        }).orElse(null);
    }

    public String getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public List<String> getLickBankers() {
        List<String> completeList = new ArrayList<>(Arrays.asList("Alister", "Robert", "Todd", "Byron", "Mike", "Alex",
                                                                  "Olga", "Stephen", "Victor", "Brett", "Chuck", "Nancy", "Paul",
                                                                  "Gurpreet", "Dahlila", "Stockho", "Shannon", "Terry", "Matt", "Niko"));

        if (lickPoolConfigService.isLickPoolInitialized()) {
            removeLeavers(completeList, lickPoolConfigService.getLeavers());
        }
        return completeList;
    }

    public void removeLeavers(List<String> completeList, List<String> leavers) {
        for (String leaver : leavers) {
            completeList.remove(leaver);
        }
    }
}
