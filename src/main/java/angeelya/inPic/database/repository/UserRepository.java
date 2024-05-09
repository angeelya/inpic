package angeelya.inPic.database.repository;
import angeelya.inPic.database.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends CrudRepository<User,Long> {
    Optional<User> findByLogin(String name);
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndUserImageNotNullOrUserImageNull(Long id);

}
