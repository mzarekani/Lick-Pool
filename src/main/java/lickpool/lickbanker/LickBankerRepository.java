package lickpool.lickbanker;

import java.util.Optional;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface LickBankerRepository extends CrudRepository<LickBanker, String> {
    Optional<LickBanker> findById(String id);
}