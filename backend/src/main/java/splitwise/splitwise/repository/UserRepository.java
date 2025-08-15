package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(Long userid);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);

    List<User> findByEmailIn(List<String> emails);
}
